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

import org.kohsuke.stapler.DataBoundConstructor;

import static com.google.common.base.CharMatcher.WHITESPACE;
import static com.google.common.base.CharMatcher.is;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import hudson.Extension;
import hudson.PluginWrapper;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.TaskListener;

import jenkins.model.Jenkins;

/**
 * An extension of {@link HealthCheck} that verifies that some required plugin
 * classes were loaded.  Also allow checking within a particular plugin's class
 * loader by using the 'class@plugin_name' notation.
 */
public class ClassLoaderCheck extends HealthCheck {
  private static final CharMatcher SEP = WHITESPACE.or(is(','));
  private static final Splitter SPLITTER =
      Splitter.on(SEP).trimResults().omitEmptyStrings();

  private final Iterable<String> classesToCheck;
  @VisibleForTesting Iterable<String> getClassesToCheck() {
    return this.classesToCheck;
  }

  /**
   * @param classesToCheckCsv
   *          the classes to check as string of comma or space separated.
   */
  @DataBoundConstructor
  public ClassLoaderCheck(String classesToCheckCsv) {
    this.classesToCheck = ImmutableList.copyOf(SPLITTER.split(
        checkNotNull(classesToCheckCsv)));
  }

  /**
   * @return the classes to check as a comma separated string.
   */
  public String getClassesToCheckCsv() { // for stapler, jelly
    return Joiner.on("\n").join(getClassesToCheck());
  }

  /** {@inheritDoc} */
  @Override
  public Result perform(TaskListener taskListener) {
    boolean failed = false;
    for (String classToCheck : getClassesToCheck()) {
      try {
        String[] parts = classToCheck.split("@", 2);
        if (parts.length < 2) { // plugin name not provided
          Class.forName(classToCheck);
        } else { // plugin name is provided
          String className = parts[0];
          String pluginName = parts[1];
          PluginWrapper plugin = Jenkins.getActiveInstance()
              .getPluginManager().getPlugin(pluginName);
          if (plugin == null) {
            failed = true;
            taskListener.error("Plugin not found: %s.\n", pluginName);
          } else {
            Class.forName(className, false, plugin.classLoader);
          }
        }
      } catch (ClassNotFoundException cnne) {
        failed = true;
        taskListener.error("Class not found: %s.\n", classToCheck);
      }
    }
    return failed ? Result.FAILURE : Result.SUCCESS;
  }

  /**
   * Descriptor class for this extension.
   */
  @Extension
  public static class DescriptorImpl extends Descriptor<HealthCheck> {
    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
      return Messages.ClassLoaderCheck_DisplayName();
    }
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format("ClassLoaderCheck [%s]",
        Joiner.on(",").join(this.getClassesToCheck()));
  }
}
