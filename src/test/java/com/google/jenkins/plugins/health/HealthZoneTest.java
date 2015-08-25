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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.google.common.collect.ImmutableList;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.TaskListener;


/**
 * Unit test for {@link HealthZone}.
 */
public class HealthZoneTest {
  @Rule public JenkinsRule jenkins = new JenkinsRule();
  private HealthZone underTest;

  @Before
  public void setup() {
    underTest = new HealthZone("under-test", ImmutableList.<HealthCheck>of());
  }

  @Test
  public void extension_registered() {
    assertThat(HealthZone.getDescriptors(), not(empty()));
  }

  @Test
  public void getters() {
    assertThat(underTest.getComponents(), empty());
    assertEquals("under-test", underTest.getName());
    assertEquals(Messages.HealthZone_DisplayName(underTest.getName()),
        underTest.getDisplayName());
    assertNotNull(underTest.getHealthCheckManager());
  }

  @Test
  public void api_error() throws IOException {
    underTest = new HealthZone(
        "under-test", ImmutableList.<HealthCheck>of(new Pessimist()));
    HealthZone.ErrorApi api = (HealthZone.ErrorApi) underTest.getApi();
    assertNotNull(api);

    assertEquals(underTest, api.getZone());

    StaplerResponse mockResponse = mock(StaplerResponse.class);
    // try a few calls
    api.doXml((StaplerRequest) null, mockResponse,
        "xpath", "wrapper", "tree", 0);
    api.doJson((StaplerRequest) null, mockResponse);
    api.doPython((StaplerRequest) null, mockResponse);

    // verify that all these api checks result in errors
    verify(mockResponse, times(3)).sendError(
        eq(StaplerResponse.SC_SERVICE_UNAVAILABLE), isA(String.class));
  }

  @Test
  public void getCombinedResult() {
    // no check, success
    assertEquals(Result.SUCCESS, underTest.getCombinedResult());

    // all passed, success
    underTest = new HealthZone("under-test",
        ImmutableList.<HealthCheck>of(new Optimist(), new Optimist()));
    assertEquals(Result.SUCCESS, underTest.getCombinedResult());

    // all failed, fail
    underTest = new HealthZone("under-test",
        ImmutableList.<HealthCheck>of(new Pessimist(), new Pessimist()));
    assertEquals(Result.FAILURE, underTest.getCombinedResult());

    // some failed, fail
    underTest = new HealthZone("under-test",
        ImmutableList.<HealthCheck>of(new Pessimist(), new Optimist()));
    assertEquals(Result.FAILURE, underTest.getCombinedResult());
  }

  @Test
  public void api_not_error() throws IOException {
    underTest = new HealthZone(
        "under-test", ImmutableList.<HealthCheck>of(new Optimist()));
    assertThat(underTest.getApi(), not(instanceOf(HealthZone.ErrorApi.class)));
  }

  /** A {link @HealthCheck} that always fail.  */
  static class Pessimist extends HealthCheck {
    /** {@inheritDoc} */
    @Override
    public Result perform(TaskListener taskListener) {
      return Result.FAILURE;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<HealthCheck> {
      /** {@inheritDoc} */
      @Override
      public String getDisplayName() {
        return "Pessimist";
      }
    }

  }

  /** A {link @HealthCheck} that always succeed.  */
  static class Optimist extends HealthCheck {
    /** {@inheritDoc} */
    @Override
    public Result perform(TaskListener taskListener) {
      return Result.SUCCESS;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<HealthCheck> {
      /** {@inheritDoc} */
      @Override
      public String getDisplayName() {
        return "Optimist";
      }
    }

  }

}
