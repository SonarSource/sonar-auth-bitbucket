# Bitbucket Authentication Plugin for SonarQube #

[![Build Status](https://api.travis-ci.org/SonarSource/sonar-auth-bitbucket.svg)](https://travis-ci.org/SonarSource/sonar-auth-bitbucket) [![Quality gate](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=org.sonarsource.auth.bitbucket%3Asonar-auth-bitbucket-plugin&metric=alert_status)](https://next.sonarqube.com/sonarqube/dashboard?id=org.sonarsource.auth.bitbucket%3Asonar-auth-bitbucket-plugin) [![Coverage](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=org.sonarsource.auth.bitbucket%3Asonar-auth-bitbucket-plugin&metric=coverage)](https://next.sonarqube.com/sonarqube/component_measures?id=org.sonarsource.auth.bitbucket%3Asonar-auth-bitbucket-plugin&metric=coverage) 

This plugin enables user authentication via [Bitbucket](https://bitbucket.org/). 
Both [Bitbucket Cloud](https://bitbucket.org) and Bitbucket Enterprise (Server/Data Center) are supported. 

If you want to analyse Bitbucket Pull Requests you should use [this](https://github.com/AmadeusITGroup/sonar-stash) or [this](https://github.com/mibexsoftware/sonar-bitbucket-plugin) plugin.

## Features
Plugin | Requires SonarQube | Features
------ | ------------------ | --------
 1.0   | 6.7 or greater     | Authentication, Single Sign-On
 1.1   | 7.2 or greater     | Support of Bitbucket Enterprise, account renaming

## Installation ##
1. Install the plugin through the [Marketplace](https://docs.sonarqube.org/display/SONAR/Marketplace) or [download](https://github.com/SonarSource/sonar-auth-bitbucket/releases) it into the *SONARQUBE_HOME/extensions/plugins* directory
2. Restart the SonarQube server

## Configuration ##
1. In Bitbucket, create a Developer application:
   * Go to "Add-ons" -> "OAuth" -> "Add consumer"
   * Name : Something like "My Company SonarQube"
   * URL : SonarQube URL
   * Callback URL : SonarQube_URL/oauth2/callback
   * Permissions : Check Account -> Read (Email will automatically be selected)
2. In SonarQube:
   * Go to "Administration" -> "Configuration" -> "General Settings" -> "Security" -> "Bitbucket"
   * Set the "Enabled" property to true
   * Set the "OAuth consumer Key" from the value provided by the Bitbucket OAuth consumer
   * Set the "OAuth consumer Secret" from the value provided by the Bitbucket OAuth consumer
3. Open the login form, a new button "Log in with Bitbucket" allow users to connect to SonarQube with their Bitbucket account.

> Note: enabling HTTPS is recommended
> * SonarQube must be publicly accessible through HTTPS
> * The property 'sonar.core.serverBaseURL' must be set to this public HTTPS URL

## General Configuration ##

Property | Description | Default value
---------| ----------- | -------------
sonar.auth.bitbucket.allowUsersToSignUp|Allow new users to authenticate. When set to 'false', only existing users will be able to authenticate to the server|true
sonar.auth.bitbucket.clientId.secured|Consumer Key provided by Bitbucket when registering the consumer|None
sonar.auth.bitbucket.clientSecret.secured|Consumer password provided by Bitbucket when registering the consumer|None
sonar.auth.bitbucket.enabled|Enable Bitbucket users to login. Value is ignored if consumer Key and Secret are not defined|false
sonar.auth.bitbucket.loginStrategy|When the login strategy is set to 'Unique', the user's login will be auto-generated the first time so that it is unique. When the login strategy is set to 'Same as Bitbucket login', the user's login will be the Bitbucket login. This last strategy allows, when changing the authentication provider, to keep existing users (if logins from new provider are the same than Bitbucket)|Unique
sonar.auth.bitbucket.apiUrl|Allows configurable bitbucket api url for pointing it to your bitbucket server. This will allow users & credentials to be reused from configured bitbucket|https://api.bitbucket.org// 

# Have question or feedback?

Use the [SonarSource forum](https://community.sonarsource.com/) with the tag `bitbucket`. Please do not forget to specify versions of plugin and SonarQube if the question relates to a bug.

# License

Copyright 2016-2018 SonarSource.

Licensed under the [GNU Lesser General Public License, Version 3.0](http://www.gnu.org/licenses/lgpl.txt)
