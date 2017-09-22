# Bitbucket Authentication Plugin for SonarQube #

This plugin enables user authentication and Single Sign-On via [Bitbucket](https://bitbucket.org/).
If you want to analyse Bitbucket Pull Requests you should use [this](https://github.com/AmadeusITGroup/sonar-stash) or [this](https://github.com/mibexsoftware/sonar-bitbucket-plugin) plugin.

## Installation ##
1. Install the plugin through the [Update Center](http://docs.sonarqube.org/display/SONAR/Update+Center) or [download](https://github.com/SonarQubeCommunity/sonar-auth-bitbucket/releases) it into the *SONARQUBE_HOME/extensions/plugins* directory
1. Restart the SonarQube server

## Configuration ##
1. In Bitbucket, create a Developer application :
  1. Go to "Add-ons" -> "OAuth" -> "Add consumer"
  2. Name : Something like "My Company SonarQube"
  3. URL : SonarQube URL
  4. Callback URL : SonarQube_URL/oauth2/callback
  5. Permissions : Check Account -> Read (Email will automatically be selected)
2. In SonarQube :
  1. Go to "Administration" -> "Configuration" -> "General Settings" -> "Security" -> "Bitbucket"
  2. Set the "Enabled" property to true
  3. Set the "OAuth consumer Key" from the value provided by the Bitbucket OAuth consumer
  4. Set the "OAuth consumer Secret" from the value provided by the Bitbucket OAuth consumer
3. Go to the login form, a new button "Log in with Bitbucket" allow users to connect to SonarQube with their Bitbucket accounts.

> Note: Only HTTPS is supported
> * SonarQube must be publicly accessible through HTTPS only
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
To provide feedback (request a feature, report a bug etc.) use the [SonarQube Google Group](https://groups.google.com/forum/#!forum/sonarqube). Please do not forget to specify plugin and SonarQube versions if it relates to a bug.
If you have a question on how to use plugin direct it to [StackOverflow](http://stackoverflow.com/questions/tagged/sonarqube+bitbucket) tagged both `sonarqube` and `bitbucket`.

# Development
[![Build Status](https://api.travis-ci.org/SonarQubeCommunity/sonar-auth-bitbucket.svg)](https://travis-ci.org/SonarQubeCommunity/sonar-auth-bitbucket)
