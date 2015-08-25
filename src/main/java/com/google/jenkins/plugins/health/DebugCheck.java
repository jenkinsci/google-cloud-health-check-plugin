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
package com.google.jenkins.plugins.health;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.TaskListener;

/**
 * An extension of {@link HealthCheck} that fails health checks on demand.
 */
public class DebugCheck extends HealthCheck {

  private final int ttl;
  private int numChecks = 0;

  /**
   * @param ttl the number of health checks to pass before failing.
   */
  @DataBoundConstructor
  public DebugCheck(int ttl) {
    this.ttl = ttl;
  }

  /** {@inheritDoc} */
  @Override
  public Result perform(TaskListener taskListener) {
    numChecks++;
    if (ttl >= 0 && numChecks > ttl) {
      return Result.FAILURE;
    } else {
      return Result.SUCCESS;
    }
  }

  /** {@inheritDoc} */
  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  /**
   * Descriptor class for this extension.
   */
  @Extension
  public static class DescriptorImpl extends Descriptor<HealthCheck> {
    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
      return Messages.DebugCheck_DisplayName();
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format("DebugCheck [ttl={%d},count=%d]",
        ttl, numChecks);
  }
}
