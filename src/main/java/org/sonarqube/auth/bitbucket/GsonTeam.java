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

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;

/**
 * Rapresent a single Team information. It's a lite version
 */
public class GsonTeam {

  @SerializedName("username")
  private String userName;

  @SerializedName("display_name")
  private String displayName;

  public GsonTeam() {
      // even if empty constructor is not required for Gson, it is strongly
      // recommended:
      // http://stackoverflow.com/a/18645370/229031
  }

  GsonTeam(String username, @Nullable String displayName) {
      this.userName = username;
      this.displayName = displayName;
  }

  public String getUserName() {
      return userName;
  }

  public String getDisplayName() {
      return displayName;
  }
}