/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.jenkins.plugins.health;

import java.io.IOException;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.jenkins.plugins.health.lib.DerivedPageManager;

import hudson.Extension;
import hudson.model.Descriptor;

/**
 * Managing {@link HealthCheck} configurations.
 *
 * TODO(nghia): Because {@link HealthZone}s have URLs based on their names, we
 * need to make sure that names are different from one another otherwise some
 * {@link HealthZone}s will share URLs leading to undefined/undesirable
 * behaviors. We need to add more UI validation code here.
 */
@Extension
public class HealthCheckManager extends DerivedPageManager<HealthZone> {
  /**
   * Load an instance of {@link HealthCheckManager} from Jenkins configuration
   * from disk if the configuration file (by {@link #getConfigFile()}) exists.
   *
   * Otherwise create a new instance.
   */
  public HealthCheckManager() throws IOException {
    super(HealthZone.class);
  }

  /** {@inheritDoc} */
  @Override
  protected String getConfigFileName() {
    return "healthChecks.xml";
  }

  /** {@inheritDoc} */
  @Override
  public String getUrlName() {
    return "healthConsole";
  }

  /** {@inheritDoc} */
  @Override
  public String getDisplayName() {
    return Messages.HealthCheckManager_DisplayName();
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    return Messages.HealthCheckManager_Description();
  }

  /**
   * @return {@link Descriptor}s of registered {@link HealthCheck} extensions.
   */
  public static List<Descriptor<HealthCheck>> getHealthCheckDescriptors() {
    return HealthCheck.all();
  }

  // Re-expose into this package for testing use.
  @VisibleForTesting
  @Override
  protected void setDerivedPages(List<HealthZone> zones) {
    super.setDerivedPages(zones);
  }
}
