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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hudson.model.Result;

/**
 * Unit test for {@link HealthCheckReport}.
 */
public class HealthCheckReportTest {

  @Test
  public void getters() {
    HealthCheckReport report =
        new HealthCheckReport(Result.SUCCESS, "congratulations");
    assertEquals(Result.SUCCESS, report.getResult());
    assertEquals("congratulations", report.getValue());
  }

}
