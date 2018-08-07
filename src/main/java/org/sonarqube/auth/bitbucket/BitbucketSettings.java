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

import java.util.Arrays;
import java.util.List;
import javax.annotation.CheckForNull;

import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.Settings;
import org.sonar.api.server.ServerSide;

import static java.lang.String.format;
import static org.sonar.api.PropertyType.SINGLE_SELECT_LIST;

@ServerSide
public class BitbucketSettings {

  public static final String CONSUMER_KEY = "sonar.auth.bitbucket.clientId.secured";
  public static final String CONSUMER_SECRET = "sonar.auth.bitbucket.clientSecret.secured";
  public static final String ENABLED = "sonar.auth.bitbucket.enabled";
  public static final String ALLOW_USERS_TO_SIGN_UP = "sonar.auth.bitbucket.allowUsersToSignUp";
  public static final String TEAM_RESTRICTION = "sonar.auth.bitbucket.teams";
  public static final String API_URL = "sonar.auth.bitbucket.apiUrl";
  public static final String DEFAULT_API_URL = "https://api.bitbucket.org/";

  // URLs are not configurable yet
  public static final String WEB_URL = "sonar.auth.bitbucket.webUrl";
  public static final String DEFAULT_WEB_URL = "https://bitbucket.org/";
  public static final String LOGIN_STRATEGY = "sonar.auth.bitbucket.loginStrategy";
  public static final String LOGIN_STRATEGY_UNIQUE = "Unique";
  public static final String LOGIN_STRATEGY_PROVIDER_LOGIN = "Same as Bitbucket login";
  public static final String LOGIN_STRATEGY_DEFAULT_VALUE = LOGIN_STRATEGY_UNIQUE;
  public static final String CATEGORY = "security";
  public static final String SUBCATEGORY = "bitbucket";

  private final Settings settings;

  public BitbucketSettings(Settings settings) {
    this.settings = settings;
  }

  @CheckForNull
  public String clientId() {
    return settings.getString(CONSUMER_KEY);
  }

  @CheckForNull
  public String clientSecret() {
    return settings.getString(CONSUMER_SECRET);
  }

  public boolean isEnabled() {
    return settings.getBoolean(ENABLED) && clientId() != null && clientSecret() != null;
  }

  public boolean allowUsersToSignUp() {
    return settings.getBoolean(ALLOW_USERS_TO_SIGN_UP);
  }

  public String[] teamRestriction() {
    return settings.getStringArray(TEAM_RESTRICTION);
  }

  public String loginStrategy() {
    return settings.getString(LOGIN_STRATEGY);
  }

  public String webURL() {
    String url = settings.getString(WEB_URL);
    if (url == null) {
      url = DEFAULT_WEB_URL;
    }
    return urlWithEndingSlash(url);
  }

  public String apiURL() {
    String url = settings.getString(API_URL);
    if (url == null) {
      url = DEFAULT_API_URL;
    }
    return urlWithEndingSlash(url);
  }

  private static String urlWithEndingSlash(String url) {
    if (!url.endsWith("/")) {
      return url + "/";
    }
    return url;
  }

  public static List<PropertyDefinition> definitions() {
    int index = 1;
    return Arrays.asList(
      PropertyDefinition.builder(ENABLED)
        .name("Enabled")
        .description("Enable Bitbucket users to login. Value is ignored if client ID and secret are not defined.")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .type(PropertyType.BOOLEAN)
        .defaultValue(String.valueOf(false))
        .index(index++)
        .build(),
      PropertyDefinition.builder(CONSUMER_KEY)
        .name("OAuth consumer Key")
        .description("Consumer Key provided by Bitbucket when registering the consumer.")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .index(index++)
        .build(),
      PropertyDefinition.builder(CONSUMER_SECRET)
        .name("OAuth consumer Secret")
        .description("Consumer Secret provided by Bitbucket when registering the consumer.")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .index(index++)
        .build(),
      PropertyDefinition.builder(ALLOW_USERS_TO_SIGN_UP)
        .name("Allow users to sign-up")
        .description("Allow new users to authenticate. When set to 'false', only existing users will be able to authenticate to the server.")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .type(PropertyType.BOOLEAN)
        .defaultValue(String.valueOf(true))
        .index(index++)
        .build(),
      PropertyDefinition.builder(TEAM_RESTRICTION)
        .name("Teams")
        .description("Only members of at least one of these teams will be able to authenticate. Keep empty to disable team restriction.")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .multiValues(true)
        .index(index++)
        .build(),
      PropertyDefinition.builder(LOGIN_STRATEGY)
        .name("Login generation strategy")
        .description(format("When the login strategy is set to '%s', the user's login will be auto-generated the first time so that it is unique. " +
          "When the login strategy is set to '%s', the user's login will be the Bitbucket login.",
          LOGIN_STRATEGY_UNIQUE, LOGIN_STRATEGY_PROVIDER_LOGIN))
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .type(SINGLE_SELECT_LIST)
        .defaultValue(LOGIN_STRATEGY_DEFAULT_VALUE)
        .options(LOGIN_STRATEGY_UNIQUE, LOGIN_STRATEGY_PROVIDER_LOGIN)
        .index(index++)
        .build(),
      PropertyDefinition.builder(API_URL)
        .name("Bitbucket API URL")
        .description("Bitbucket API URL. Default is Bitbucket Cloud")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .defaultValue(DEFAULT_API_URL)
        .index(index)
        .build());
  }

}
