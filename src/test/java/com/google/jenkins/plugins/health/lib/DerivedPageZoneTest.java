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
package com.google.jenkins.plugins.health.lib;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.google.common.collect.ImmutableList;


/**
 * Unit test for {@link DerivedPageZone}.
 */
public class DerivedPageZoneTest {
  static class NamedDerivedPageZone
      extends DerivedPageZone<DerivedPageComponent<
      DerivedPageComponentReport<String>>, DerivedPageComponentReport<String>> {
    public NamedDerivedPageZone(String name,
        List<DerivedPageComponent<
            DerivedPageComponentReport<String>>> derivedPageComponents) {
      super(name, derivedPageComponents);
    }
    @Override
    public String getDisplayName() {
      return null;
    }

    @Override
    protected String namePageComponent(
        DerivedPageComponent<DerivedPageComponentReport<String>> component) {
      return component.toString();
    }

    @Override
    protected DerivedPageComponentReport<String> derivePageComponent(
        DerivedPageComponent<DerivedPageComponentReport<String>> component) {
      return null;
    }
  }
  @Rule public JenkinsRule jenkins = new JenkinsRule();
  private DerivedPageZone<
      DerivedPageComponent<DerivedPageComponentReport<String>>,
      DerivedPageComponentReport<String>> underTest;

  @Before
  public void setup() {
    underTest = new NamedDerivedPageZone(
        "under-test", ImmutableList.<DerivedPageComponent<
        DerivedPageComponentReport<String>>>of());
  }

  @Test
  public void getters() {
    assertThat(underTest.getComponents(), empty());
    assertEquals("under-test", underTest.getName());
  }
}
