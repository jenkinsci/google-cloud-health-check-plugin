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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.sf.json.JSONObject;


/**
 * Unit test for {@link HealthCheckManager}.
 */
public class HealthCheckManagerTest {
  @Rule public JenkinsRule jenkins = new JenkinsRule();
  HealthCheckManager underTest;

  @Before
  public void setup() throws IOException {
    underTest = new HealthCheckManager();
  }

  @Test
  public void extension_registered() {
    List<HealthCheckManager> extensions =
        jenkins.getInstance().getExtensionList(HealthCheckManager.class);
    assertEquals(1, extensions.size());
  }

  @Test
  public void construct_no_xml() {
    // without a file, this shouldn't show anything
    assertThat(underTest.getDerivedPages(), empty());
  }

  @Test
  public void construct_from_xml() throws IOException {
    // without a file, this shouldn't show anything
    assertThat(underTest.getDerivedPages(), empty());

    // create some configuration and save to a file.
    ExecutorCheck healthCheck = new ExecutorCheck(2, "");
    underTest.setDerivedPages(ImmutableList.of(new HealthZone(
        "some-zone",
        ImmutableList.<HealthCheck>of(healthCheck))));
    underTest.save();

    underTest = new HealthCheckManager();
    assertEquals(1, underTest.getDerivedPages().size());
    assertEquals(1,
        underTest.getZone("some-zone").getComponents().size());
    assertThat(underTest.getZone("some-zone").getComponents().get(0),
        instanceOf(ExecutorCheck.class));
  }

  @Test
  public void getters() {
    HealthCheckManager underTest = Iterables.getOnlyElement(
            jenkins.getInstance().getExtensionList(HealthCheckManager.class));
    assertNotNull(underTest.getUrlName());
    assertNotNull(underTest.getIconFileName());
    assertEquals(Messages.HealthCheckManager_DisplayName(),
        underTest.getDisplayName());
    assertEquals(Messages.HealthCheckManager_Description(),
        underTest.getDescription());
    assertSame(HealthCheck.all(),
        HealthCheckManager.getHealthCheckDescriptors());
  }

  @Test
  public void doConfigSubmit() throws Exception {
    StaplerRequest mockRequest = mock(StaplerRequest.class);
    JSONObject data = new JSONObject();
    JSONObject zoneData = new JSONObject();
    data.put("derivedPages", zoneData);
    when(mockRequest.getSubmittedForm()).thenReturn(data);
    List<HealthZone> zones = ImmutableList.of(
        new HealthZone("standard", ImmutableList.<HealthCheck> of()));
    when(mockRequest.bindJSONToList(HealthZone.class, zoneData)).
        thenReturn(zones);

    underTest.doConfigSubmit(mockRequest);
    assertEquals(1, underTest.getDerivedPages().size());
    assertThat(underTest.getZone("standard").getComponents(),
        empty());
  }
}
