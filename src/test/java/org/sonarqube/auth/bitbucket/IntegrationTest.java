/*
 * Bitbucket Authentication for SonarQube
 * Copyright (C) 2016-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarqube.auth.bitbucket;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.server.authentication.OAuth2IdentityProvider;
import org.sonar.api.server.authentication.UnauthorizedException;
import org.sonar.api.server.authentication.UserIdentity;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntegrationTest {

  private static final String CALLBACK_URL = "http://localhost/oauth/callback/bitbucket";

  @Rule
  public MockWebServer bitbucket = new MockWebServer();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  // load settings with default values
  private MapSettings settings = new MapSettings(new PropertyDefinitions(BitbucketSettings.definitions()));
  private BitbucketSettings bitbucketSettings = new BitbucketSettings(settings);
  private UserIdentityFactory userIdentityFactory = new UserIdentityFactory(bitbucketSettings);
  private BitbucketScribeApi scribeApi = new BitbucketScribeApi(bitbucketSettings);
  private BitbucketIdentityProvider underTest = new BitbucketIdentityProvider(bitbucketSettings, userIdentityFactory, scribeApi);

  @Before
  public void setUp() {
    settings.setProperty("sonar.auth.bitbucket.clientId.secured", "the_id");
    settings.setProperty("sonar.auth.bitbucket.clientSecret.secured", "the_secret");
    settings.setProperty("sonar.auth.bitbucket.enabled", true);
    settings.setProperty("sonar.auth.bitbucket.apiUrl", format("http://%s:%d", bitbucket.getHostName(), bitbucket.getPort()));
    settings.setProperty("sonar.auth.bitbucket.webUrl", format("http://%s:%d", bitbucket.getHostName(), bitbucket.getPort()));
  }

  /**
   * First phase: SonarQube redirects browser to Bitbucket authentication form, requesting the
   * minimal access rights ("scope") to get user profile.
   */
  @Test
  public void redirect_browser_to_bitbucket_authentication_form() throws Exception {
    DumbInitContext context = new DumbInitContext("the-csrf-state");
    underTest.init(context);
    assertThat(context.redirectedTo)
      .startsWith(bitbucket.url("site/oauth2/authorize").toString())
      .contains("scope=" + encode("account", StandardCharsets.UTF_8.name()));
  }

  /**
   * Second phase: Bitbucket redirects browser to SonarQube at /oauth/callback/bitbucket?code={the verifier code}.
   * This SonarQube web service sends three requests to Bitbucket:
   * <ul>
   *   <li>get an access token</li>
   *   <li>get the profile (login, name) of the authenticated user</li>
   *   <li>get the emails of the authenticated user</li>
   * </ul>
   */
  @Test
  public void authenticate_successfully() throws Exception {
    bitbucket.enqueue(newSuccessfulAccessTokenResponse());
    bitbucket.enqueue(newUserResponse("john", "John"));
    bitbucket.enqueue(newPrimaryEmailResponse("john@bitbucket.org"));

    HttpServletRequest request = newRequest("the-verifier-code");
    DumbCallbackContext callbackContext = new DumbCallbackContext(request);
    underTest.callback(callbackContext);

    // contrary to GitHub, Bitbucket does not support verification of CSRF state
    assertThat(callbackContext.csrfStateVerified.get()).isFalse();
    // generate an unique login by default (suffixed by "@bitbucket"), instead of copying bitbucket login as-this.
    assertThat(callbackContext.userIdentity.getLogin()).isEqualTo("john@bitbucket");
    assertThat(callbackContext.userIdentity.getName()).isEqualTo("John");
    assertThat(callbackContext.userIdentity.getEmail()).isEqualTo("john@bitbucket.org");
    assertThat(callbackContext.redirectedToRequestedPage.get()).isTrue();

    // Verify the requests sent to Bitbucket
    RecordedRequest accessTokenRequest = bitbucket.takeRequest();
    assertThat(accessTokenRequest.getPath()).startsWith("/site/oauth2/access_token");
    RecordedRequest userRequest = bitbucket.takeRequest();
    assertThat(userRequest.getPath()).startsWith("/2.0/user");
    RecordedRequest emailRequest = bitbucket.takeRequest();
    assertThat(emailRequest.getPath()).startsWith("/2.0/user/emails");
    // do not request user teams, team restriction is disabled by default
    assertThat(bitbucket.getRequestCount()).isEqualTo(3);
  }

  @Test
  public void callback_throws_ISE_if_error_when_requesting_user_profile() {
    bitbucket.enqueue(newSuccessfulAccessTokenResponse());
    // https://api.bitbucket.org/2.0/user fails
    bitbucket.enqueue(new MockResponse().setResponseCode(500).setBody("{error}"));

    DumbCallbackContext callbackContext = new DumbCallbackContext(newRequest("the-verifier-code"));
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("Can not get Bitbucket user profile. HTTP code: 500, response: {error}");
    underTest.callback(callbackContext);

    assertThat(callbackContext.csrfStateVerified.get()).isTrue();
    assertThat(callbackContext.userIdentity).isNull();
    assertThat(callbackContext.redirectedToRequestedPage.get()).isFalse();
  }

  @Test
  public void allow_authentication_if_user_is_member_of_one_restricted_team() {
    settings.setProperty("sonar.auth.bitbucket.teams", new String[]{"team1", "team2"});

    bitbucket.enqueue(newSuccessfulAccessTokenResponse());
    bitbucket.enqueue(newUserResponse("john", "John"));
    bitbucket.enqueue(newPrimaryEmailResponse("john@bitbucket.org"));
    bitbucket.enqueue(newTeamsResponse("team3", "team2"));

    HttpServletRequest request = newRequest("the-verifier-code");
    DumbCallbackContext callbackContext = new DumbCallbackContext(request);
    underTest.callback(callbackContext);

    assertThat(callbackContext.userIdentity.getLogin()).isEqualTo("john@bitbucket");
    assertThat(callbackContext.redirectedToRequestedPage.get()).isTrue();
  }

  @Test
  public void forbid_authentication_if_user_is_not_member_of_one_restricted_team() {
    settings.setProperty("sonar.auth.bitbucket.teams", new String[]{"team1", "team2"});

    bitbucket.enqueue(newSuccessfulAccessTokenResponse());
    bitbucket.enqueue(newUserResponse("john", "John"));
    bitbucket.enqueue(newPrimaryEmailResponse("john@bitbucket.org"));
    bitbucket.enqueue(newTeamsResponse("team3"));

    expectedException.expect(UnauthorizedException.class);

    DumbCallbackContext context = new DumbCallbackContext(newRequest("the-verifier-code"));
    underTest.callback(context);
  }

  @Test
  public void forbid_authentication_if_user_is_not_member_of_any_team() {
    settings.setProperty("sonar.auth.bitbucket.teams", new String[]{"team1", "team2"});

    bitbucket.enqueue(newSuccessfulAccessTokenResponse());
    bitbucket.enqueue(newUserResponse("john", "John"));
    bitbucket.enqueue(newPrimaryEmailResponse("john@bitbucket.org"));
    bitbucket.enqueue(newTeamsResponse(/* no teams */));

    expectedException.expect(UnauthorizedException.class);

    DumbCallbackContext context = new DumbCallbackContext(newRequest("the-verifier-code"));
    underTest.callback(context);
  }

  /**
   * Response sent by Bitbucket to SonarQube when generating an access token
   */
  private static MockResponse newSuccessfulAccessTokenResponse() {
    return new MockResponse().setBody("{\"access_token\":\"e72e16c7e42f292c6912e7710c838347ae178b4a\",\"scope\":\"user\"}");
  }

  /**
   * Response of https://api.bitbucket.org/2.0/user
   */
  private static MockResponse newUserResponse(String login, String name) {
    return new MockResponse().setBody("{\"username\":\"" + login + "\", \"display_name\":\"" + name + "\"}");
  }

  /**
   * Response of https://api.bitbucket.org/2.0/teams/{username}
   */
  private static MockResponse newTeamsResponse(String... teams) {
    String s = Arrays.stream(teams)
      .map(team -> "{\"username\":\"" + team + "\"}")
      .collect(Collectors.joining(","));
    return new MockResponse().setBody("{\"values\":[" + s + "]}");
  }

  /**
   * Response of https://api.bitbucket.org/2.0/user/emails
   */
  private static MockResponse newPrimaryEmailResponse(String email) {
    return new MockResponse().setBody("{\"values\":[{\"active\": true,\"email\":\"" + email + "\",\"is_primary\": true}]}");
  }

  private static HttpServletRequest newRequest(String verifierCode) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("code")).thenReturn(verifierCode);
    return request;
  }

  private static class DumbCallbackContext implements OAuth2IdentityProvider.CallbackContext {
    final HttpServletRequest request;
    final AtomicBoolean csrfStateVerified = new AtomicBoolean(false);
    final AtomicBoolean redirectedToRequestedPage = new AtomicBoolean(false);
    UserIdentity userIdentity = null;

    public DumbCallbackContext(HttpServletRequest request) {
      this.request = request;
    }

    @Override
    public void verifyCsrfState() {
      this.csrfStateVerified.set(true);
    }

    @Override
    public void redirectToRequestedPage() {
      redirectedToRequestedPage.set(true);
    }

    @Override
    public void authenticate(UserIdentity userIdentity) {
      this.userIdentity = userIdentity;
    }

    @Override
    public String getCallbackUrl() {
      return CALLBACK_URL;
    }

    @Override
    public HttpServletRequest getRequest() {
      return request;
    }

    @Override
    public HttpServletResponse getResponse() {
      throw new UnsupportedOperationException("not used");
    }
  }

  private static class DumbInitContext implements OAuth2IdentityProvider.InitContext {
    String redirectedTo = null;
    private final String generatedCsrfState;

    public DumbInitContext(String generatedCsrfState) {
      this.generatedCsrfState = generatedCsrfState;
    }

    @Override
    public String generateCsrfState() {
      return generatedCsrfState;
    }

    @Override
    public void redirectTo(String url) {
      this.redirectedTo = url;
    }

    @Override
    public String getCallbackUrl() {
      return CALLBACK_URL;
    }

    @Override
    public HttpServletRequest getRequest() {
      return null;
    }

    @Override
    public HttpServletResponse getResponse() {
      return null;
    }
  }
}
