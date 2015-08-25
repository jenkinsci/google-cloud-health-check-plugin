/*
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.jenkins.plugins.health.lib;

import java.util.List;

import javax.annotation.Nullable;

import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.export.ExportedBean;

import hudson.model.Api;
import hudson.model.RootAction;
import hudson.security.Permission;

import jenkins.model.Jenkins;

/**
 * Expose {@link DerivedPageComponent}s' assembled pages through the Jenkins UI
 * and through the Jenkins remote access API.
 */
@ExportedBean
public abstract class DerivedPageAction implements RootAction, StaplerProxy {

  /**
   * @param zoneName the name of the {@link DerivedPageZone} to look up.
   * @return the {@link DerivedPageZone} for the given name, null if none.
   */
  @Nullable public DerivedPageZone getZone(String zoneName) {
    return getDerivedPageManager().getZone(zoneName);
  }

  /**
   * @return the {@link DerivedPageZone}s.
   */
  public List<? extends DerivedPageZone> getZones() {
    return getDerivedPageManager().getDerivedPages();
  }

  /** {@inheritDoc} */
  @Override
  public String getIconFileName() { // RootAction
    Permission permission = getReadPermission();
    if (permission == null
        || Jenkins.getActiveInstance().hasPermission(permission)) {
      // only make DerivedPageAction visible on the side panel if user
      // has the correct permission.
      return getDerivedPageManager().getIconFileName();
    } else {
      return null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public Object getTarget() { // StaplerProxy
    // CHECKSTYLE:OFF
    // Make sure permission redirection works, per instruction at
    // http://kohsuke.org/2011/12/08/jenkins-plugin-tip-access-control-and-visibility-in-actions/
    // CHECKSTYLE:ON
    Permission permission = getReadPermission();
    if (permission != null) {
      Jenkins.getActiveInstance().checkPermission(permission);
    }
    return this;
  }

  /**
   * A default implementation of the remote access API.
   */
  public Api getApi() {
    return new Api(this);
  }

  /**
   * @return the {@link DerivedPageManager} paired with this instance.
   */
  protected abstract DerivedPageManager getDerivedPageManager();

  /**
   * Get the permission level needed to view this Action.
   *
   * Hook for subclasses to provide a permission required for viewing the
   * managed pages this class provides, or null to not test any permission.
   * @return A permission needed to view, for example {@code Jenkins.READ}.
   */
  protected abstract Permission getReadPermission();
}
