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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.model.AutoCompletionCandidates;
import hudson.model.Result;
import hudson.model.TaskListener;

import jenkins.model.Jenkins;

/**
 * Unit tests for {@link ExecutorCheck}.
 */
public class ExecutorCheckTest {

  @Rule public JenkinsRule jenkins = new JenkinsRule();
  private TaskListener taskListener = TaskListener.NULL;
  ExecutorCheck underTest;

  @Before
  public void setup() {
    underTest = new ExecutorCheck(jenkins.getInstance().getNumExecutors(),
        "master");
  }

  @Test
  public void getLabelsSsv() {
    assertEquals("master", underTest.getLabelsSsv());
    underTest = new ExecutorCheck(jenkins.getInstance().getNumExecutors(),
        "master slave");
    assertEquals("master slave", underTest.getLabelsSsv());
  }

  @Test
  public void constructor() {
    underTest = new ExecutorCheck(42, "l1\nl2   l3  l4");
    for (String label : new String[] { "l1", "l2", "l3", "l4" }) {
      assertTrue(underTest.getLabels().contains(
          Jenkins.getInstance().getLabel(label)));
    }
  }

  @Test
  public void extension_registered() {
    ExecutorCheck.DescriptorImpl descriptor = (ExecutorCheck.DescriptorImpl)
        underTest.getDescriptor();
    assertNotNull(descriptor);
    assertEquals(Messages.ExecutorCheck_DisplayName(),
        descriptor.getDisplayName());
  }

  @Test
  public void perform_success() {
    assertEquals(Result.SUCCESS, underTest.perform(taskListener));
    // check all labels
    underTest = new ExecutorCheck(jenkins.getInstance().getNumExecutors(), "");
    assertTrue(underTest.getLabels().isEmpty());
    assertEquals(Result.SUCCESS, underTest.perform(taskListener));
  }

  @Test
  public void perform_failure() {
    ExecutorCheck underTest = new ExecutorCheck(
        jenkins.getInstance().getNumExecutors() + 1, "master");
    assertEquals(Result.FAILURE, underTest.perform(taskListener));
    // check non-existent label
    underTest = new ExecutorCheck(1, "non-existence-label");
    assertEquals(Result.FAILURE, underTest.perform(taskListener));
    // check all labels
    underTest = new ExecutorCheck(100, "");
    assertEquals(Result.FAILURE, underTest.perform(taskListener));
  }

  @Test
  public void tostring() {
    assertTrue(underTest.toString().contains(
        ExecutorCheck.class.getSimpleName()));
  }

  @Test
  public void doAutoCompleteLabelsSsv() {
    /* this should return 'master' */
    AutoCompletionCandidates candidates
        = underTest.getDescriptor().doAutoCompleteLabelsSsv("mas");
    assertThat(candidates.getValues(), contains("master"));

    /* this should return nothing */
    candidates
        = underTest.getDescriptor().doAutoCompleteLabelsSsv("non-exist-prefix");
    assertThat(candidates.getValues(), empty());
  }
}
