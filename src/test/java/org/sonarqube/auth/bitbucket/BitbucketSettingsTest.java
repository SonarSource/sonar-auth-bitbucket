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

import org.junit.Test;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.config.internal.MapSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarqube.auth.bitbucket.BitbucketSettings.LOGIN_STRATEGY_DEFAULT_VALUE;
import static org.sonarqube.auth.bitbucket.BitbucketSettings.LOGIN_STRATEGY_PROVIDER_LOGIN;

public class BitbucketSettingsTest {

  private Settings settings = new MapSettings(new PropertyDefinitions(BitbucketSettings.definitions()));

  private BitbucketSettings underTest = new BitbucketSettings(settings);

  @Test
  public void is_enabled() {
    settings.setProperty("sonar.auth.bitbucket.clientId.secured", "id");
    settings.setProperty("sonar.auth.bitbucket.clientSecret.secured", "secret");
    settings.setProperty("sonar.auth.bitbucket.loginStrategy", LOGIN_STRATEGY_DEFAULT_VALUE);

    settings.setProperty("sonar.auth.bitbucket.enabled", true);
    assertThat(underTest.isEnabled()).isTrue();

    settings.setProperty("sonar.auth.bitbucket.enabled", false);
    assertThat(underTest.isEnabled()).isFalse();
  }

  @Test
  public void is_enabled_always_return_false_when_client_id_is_null() {
    settings.setProperty("sonar.auth.bitbucket.enabled", true);
    settings.setProperty("sonar.auth.bitbucket.clientId.secured", (String) null);
    settings.setProperty("sonar.auth.bitbucket.clientSecret.secured", "secret");
    settings.setProperty("sonar.auth.bitbucket.loginStrategy", LOGIN_STRATEGY_DEFAULT_VALUE);

    assertThat(underTest.isEnabled()).isFalse();
  }

  @Test
  public void is_enabled_always_return_false_when_client_secret_is_null() {
    settings.setProperty("sonar.auth.bitbucket.enabled", true);
    settings.setProperty("sonar.auth.bitbucket.clientId.secured", "id");
    settings.setProperty("sonar.auth.bitbucket.clientSecret.secured", (String) null);
    settings.setProperty("sonar.auth.bitbucket.loginStrategy", LOGIN_STRATEGY_DEFAULT_VALUE);

    assertThat(underTest.isEnabled()).isFalse();
  }

  @Test
  public void default_login_strategy_is_unique_login() {
    assertThat(underTest.loginStrategy()).isEqualTo(BitbucketSettings.LOGIN_STRATEGY_UNIQUE);
  }

  @Test
  public void return_client_id() {
    settings.setProperty("sonar.auth.bitbucket.clientId.secured", "id");
    assertThat(underTest.clientId()).isEqualTo("id");
  }

  @Test
  public void return_client_secret() {
    settings.setProperty("sonar.auth.bitbucket.clientSecret.secured", "secret");
    assertThat(underTest.clientSecret()).isEqualTo("secret");
  }

  @Test
  public void return_login_strategy() {
    settings.setProperty("sonar.auth.bitbucket.loginStrategy", LOGIN_STRATEGY_PROVIDER_LOGIN);
    assertThat(underTest.loginStrategy()).isEqualTo(LOGIN_STRATEGY_PROVIDER_LOGIN);
  }

  @Test
  public void allow_users_to_sign_up() {
    settings.setProperty("sonar.auth.bitbucket.allowUsersToSignUp", "true");
    assertThat(underTest.allowUsersToSignUp()).isTrue();

    settings.setProperty("sonar.auth.bitbucket.allowUsersToSignUp", "false");
    assertThat(underTest.allowUsersToSignUp()).isFalse();
  }

  @Test
  public void configure_api_Url() throws Exception {
    final String configuredApiUrl = "https://test-company.api.bitbucket.org/";

    settings.setProperty("sonar.auth.bitbucket.apiUrl", configuredApiUrl);

    assertThat(underTest.apiURL()).isEqualTo(configuredApiUrl);
  }

  @Test
  public void default_apiUrl() {
    assertThat(underTest.apiURL()).isEqualTo("https://api.bitbucket.org/");
  }

  @Test
  public void default_webUrl() {
    assertThat(underTest.webURL()).isEqualTo("https://bitbucket.org/");
  }

  @Test
  public void definitions() {
    assertThat(BitbucketSettings.definitions()).hasSize(6);
  }

}
