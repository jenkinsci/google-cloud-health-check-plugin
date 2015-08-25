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

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.jenkins.plugins.health.lib.DerivedPageComponent;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.TaskListener;

import hudson.util.StreamTaskListener;
import jenkins.model.Jenkins;

/**
 * A {@link hudson.model.Describable} {@link hudson.ExtensionPoint} to perform
 * health checks. Subclasses need to implement the
 * {@link #perform(TaskListener taskListener)} method.
 */
public abstract class HealthCheck
    extends AbstractDescribableImpl<HealthCheck>
    implements DerivedPageComponent<HealthCheckReport>, ExtensionPoint {

  /**
   * @return {@link Descriptor}s of all {@link HealthCheck} extensions.
   */
  public static List<Descriptor<HealthCheck>> all() {
    return Jenkins.getActiveInstance().getDescriptorList(HealthCheck.class);
  }

  /**
   * Subclass needs to implement its own health check logic here.
   * @param taskListener the logger to capture logs from performing the checks.
   * @return generally, this should return {@link Result.SUCCESS} in case of
   *         a successful health-check and {@link Result.FAILURE} otherwise.
   */
  public abstract Result perform(TaskListener taskListener);

  @Override
  public HealthCheckReport performDerivation() {
    ByteArrayOutputStream message = new ByteArrayOutputStream();
    StreamTaskListener listener =
        new StreamTaskListener(message, Charsets.UTF_8);
    Result result = perform(listener);
    listener.closeQuietly();
    return new HealthCheckReport(result,
        new String(message.toByteArray(), Charsets.UTF_8));
  }
}
