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
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.internal.PluginContextImpl;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthBitbucketPluginTest {
  Plugin.Context context = new PluginContextImpl.Builder()
    .setSonarRuntime(SonarRuntimeImpl.forSonarQube(Version.create(6, 7), SonarQubeSide.SERVER))
    .build();
  AuthBitbucketPlugin underTest = new AuthBitbucketPlugin();

  @Test
  public void test_extensions() {
    underTest.define(context);
    assertThat(context.getExtensions()).hasSize(11);
  }

}
