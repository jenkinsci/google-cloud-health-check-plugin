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

import org.kohsuke.stapler.export.ExportedBean;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.jenkins.plugins.health.lib.DerivedPageAction;

import hudson.Extension;
import hudson.security.Permission;
import hudson.security.PermissionGroup;
import hudson.security.PermissionScope;

import jenkins.model.Jenkins;

/**
 * Expose {@link HealthCheck} results through the Jenkins UI, and also through
 * the Jenkins remote access API.
 *
 * TODO(nghia): - consider a {@code HealthCheckListener} extension point?
 * TODO(nghia): - consider a {@code HealthCheckTrigger} trigger?
 */
@ExportedBean @Extension
public class HealthCheckAction extends DerivedPageAction {
  /** {@inheritDoc} */
  @Override
  public String getDisplayName() { // RootAction
    return Messages.HealthCheckAction_DisplayName();
  }

  /** {@inheritDoc} */
  @Override
  public String getUrlName() { // RootAction
    return "/health";
  }

  /**
   * @return the {@link HealthCheckManager}.
   */
  @Override
  @VisibleForTesting
  protected HealthCheckManager getDerivedPageManager() {
    return Iterables.getOnlyElement(
        Jenkins.getActiveInstance().getExtensionList(HealthCheckManager.class));
  }

  /** {@inheritDoc} */
  @Override
  protected Permission getReadPermission() {
    return CHECK;
  }

  /**
   * Introduce a new type of Permission group for Health Check.
   */
  public static final PermissionGroup PERMISSIONS = new PermissionGroup(
      HealthCheckAction.class, Messages._HealthCheck_Permissions_Title());

  /**
   * Introduce the Health/Check permission.
   */
  public static final Permission CHECK = new Permission(PERMISSIONS,
      "Check",
      Messages._HealthCheck_Permissions_Check(),
      Jenkins.READ,
      PermissionScope.JENKINS);

}
