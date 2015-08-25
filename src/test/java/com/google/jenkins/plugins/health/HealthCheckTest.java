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
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

/**
 * Unit tests for {@link HealthCheck}.
 */
public class HealthCheckTest {

  @Rule public JenkinsRule jenkins = new JenkinsRule();
  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test @WithoutJenkins
  public void all_noJenkins() {
    thrown.expect(IllegalStateException.class);
    assertEquals(0, HealthCheck.all().size());
  }

  @Test
  public void all() {
    assertTrue(0 < HealthCheck.all().size());
  }

}
