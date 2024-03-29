# Bitbucket Authentication Plugin for SonarQube #

[![Build Status](https://api.travis-ci.org/SonarSource/sonar-auth-bitbucket.svg)](https://travis-ci.org/SonarSource/sonar-auth-bitbucket) [![Quality gate](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=org.sonarsource.auth.bitbucket%3Asonar-auth-bitbucket-plugin&metric=alert_status)](https://next.sonarqube.com/sonarqube/dashboard?id=org.sonarsource.auth.bitbucket%3Asonar-auth-bitbucket-plugin) [![Coverage](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=org.sonarsource.auth.bitbucket%3Asonar-auth-bitbucket-plugin&metric=coverage)](https://next.sonarqube.com/sonarqube/component_measures?id=org.sonarsource.auth.bitbucket%3Asonar-auth-bitbucket-plugin&metric=coverage) 

## Note : This plugin is compatible with SonarQube up to 9.1, and won't be compatible with the next SonarQube versions as it's now a built-in feature of SonarQube 9.2 and later.

This plugin enables user authentication via [Bitbucket](https://bitbucket.org/). 
Only [Bitbucket Cloud](https://bitbucket.org) is supported. 

If you want to analyse Bitbucket Pull Requests you can use [AmadeusITGroup/sonar-stash](https://github.com/AmadeusITGroup/sonar-stash) or [mibexsoftware/sonar-bitbucket-plugin](https://github.com/mibexsoftware/sonar-bitbucket-plugin) plugin.

## Features

Plugin | Requires SonarQube | Features
------ | ------------------ | --------
 1.0   | 6.7 or greater     | Authentication, Single Sign-On
 1.1   | 7.2 or greater     | ~~Support of Bitbucket Enterprise~~ (buggy, see [AUTHBB-1](https://jira.sonarsource.com/browse/AUTHBB-1)), team restriction, account renaming

## Installation

1. Install the plugin through the [Marketplace](https://docs.sonarqube.org/display/SONAR/Marketplace) or [download](https://binaries.sonarsource.com/Distribution/sonar-auth-bitbucket-plugin/) it into the *SONARQUBE_HOME/extensions/plugins* directory
2. Restart the SonarQube server

## Configuration

1. In Bitbucket, create a OAuth consumer:
   * Click on your account avatar in the bottom left corner and select Settings.
   * Click on "Access Management" -> "OAuth consumers" -> "Add consumer"
   * Name : Something like "My SonarQube"
   * URL : the SonarQube base URL, for example `http://my_server`
   * Callback URL : the SonarQube base URL suffixed with `/oauth2/callback`, for example `http://my_server/oauth2/callback` 
   * Permissions : Check "Account -> Read" (Email will be automatically selected)
   * Save. The generated key and secret are displayed when selecting the consumer.
2. In SonarQube:
   * Go to "Administration" -> "Configuration" -> "General Settings" -> "Security" -> "Bitbucket"
   * Set "Enabled" to true
   * Set "OAuth consumer key" with the key generated by the Bitbucket OAuth consumer
   * Set "OAuth consumer secret" with the secret provided by the Bitbucket OAuth consumer
3. Open the login form, a new button "Log in with Bitbucket" allow users to connect to SonarQube with their Bitbucket account.

> Note: enabling HTTPS is recommended
> * SonarQube should be publicly accessible through HTTPS
> * Set the SonarQube property "Administration" -> "Configuration" -> "General" -> "Server base URL", for example `https://my_server`
> * Use `https://` URLs in settings of OAuth consumer in Bitbucket.

## General Configuration

Property | Description | Default value
---------| ----------- | -------------
Enabled|Enable Bitbucket users to login. Value is ignored if consumer Key and Secret are not defined.|false
Allow users to sign-up|Allow new users to authenticate. When set to 'false', only existing users will be able to authenticate.|true
OAuth consumer key|Consumer Key provided by Bitbucket when registering the consumer|None
OAuth consumer secret|Consumer password provided by Bitbucket when registering the consumer|None
Bitbucket API URL|~~Base URL of the Bitbucket server. Used to connect to Bitbucket Enterprise.~~ Buggy. See [AUTHBB-1](https://jira.sonarsource.com/browse/AUTHBB-1)|https://api.bitbucket.org
Teams|Users must be members of at least one team in order to be able to authenticate.|None (team restriction is disabled) 
Login generation strategy|When the login strategy is set to 'Unique', the user's login will be auto-generated the first time so that it is unique. When the login strategy is set to 'Same as Bitbucket login', the user's login will be the Bitbucket login. This last strategy allows, when changing the authentication provider, to keep existing users (if logins from new provider are the same than Bitbucket)|Unique

## Have question or feedback?

To ask questions or provide feedback (request a feature, report a bug etc.), use the [SonarSource forum](https://community.sonarsource.com/) with the tag `bitbucket`. Please do not forget to specify versions of plugin and SonarQube if the question relates to a bug.

## Links

* [Downloads](https://binaries.sonarsource.com/Distribution/sonar-auth-bitbucket-plugin/)
* [Tickets](https://jira.sonarsource.com/browse/AUTHBB)
 
## License

Copyright 2016-2018 SonarSource.

Licensed under the [GNU Lesser General Public License, Version 3.0](http://www.gnu.org/licenses/lgpl.txt)
