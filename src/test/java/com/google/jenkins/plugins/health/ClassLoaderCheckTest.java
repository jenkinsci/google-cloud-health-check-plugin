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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

import com.google.common.collect.Iterables;

import hudson.model.Result;
import hudson.model.TaskListener;

/**
 * Unit tests for {@link ClassLoaderCheck}.
 */
public class ClassLoaderCheckTest {
  @Rule public JenkinsRule jenkins = new JenkinsRule();
  private ClassLoaderCheck underTest;
  private ClassLoaderCheck.DescriptorImpl descriptor;
  private String classesToCheckCsv;
  private TaskListener taskListener = TaskListener.NULL;

  @Before
  public void setup() {
    classesToCheckCsv = ClassLoaderCheck.class.getName();
    this.underTest = new ClassLoaderCheck(classesToCheckCsv);
    this.descriptor = new ClassLoaderCheck.DescriptorImpl();
    assertTrue(underTest.toString().contains(ClassLoaderCheck.class.getName()));
  }

  @Test @WithoutJenkins
  public void constructor() {
    this.underTest = new ClassLoaderCheck("c1\n,,c2,,,  c3,  \nc4");
    for (String classToCheck : new String[] {"c1", "c2", "c3", "c4"}) {
      assertTrue(
          Iterables.contains(underTest.getClassesToCheck(), classToCheck));
    }
  }

  @WithoutJenkins
  @Test
  public void getClassesToCheckCsv() {
    assertEquals(classesToCheckCsv, underTest.getClassesToCheckCsv());
  }

  @WithoutJenkins
  @Test
  public void perform_success() {
    assertEquals(Result.SUCCESS, underTest.perform(taskListener));
  }

  @WithoutJenkins
  @Test
  public void perform_failure() {
    this.underTest = new ClassLoaderCheck("this.class.does.not.Exist");
    assertEquals(Result.FAILURE, underTest.perform(taskListener));
  }

  @WithoutJenkins
  @Test
  public void descriptor_displayName() {
    assertEquals(Messages.ClassLoaderCheck_DisplayName(),
        descriptor.getDisplayName());
  }

  @Test
  public void extension_registered() {
    assertNotNull(underTest.getDescriptor());
  }

}
