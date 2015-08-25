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

import com.google.jenkins.plugins.health.lib.DerivedPageComponentReport;

import hudson.model.Result;

/**
 * A tuple of a {@link Result} object and a log stream as a {@link String}.
 */
@ExportedBean
public class HealthCheckReport extends DerivedPageComponentReport<String> {
  /** {inheritDoc} */
  public HealthCheckReport(Result result, String value) {
    super(result, value);
  }
}
