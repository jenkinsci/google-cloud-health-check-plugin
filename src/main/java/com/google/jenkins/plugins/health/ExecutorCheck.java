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

import java.util.Set;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import static com.google.common.base.CharMatcher.WHITESPACE;
import static com.google.common.base.CharMatcher.is;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import hudson.Extension;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.Label;
import hudson.model.Node;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;

import jenkins.model.Jenkins;

/**
 * An extension of {@link HealthCheck} that verifies that Jenkins has more than
 * a given minimum number of executors of some given labels.
 */
public class ExecutorCheck extends HealthCheck {
  private static final CharMatcher SEP = WHITESPACE.or(is(','));
  private final int minExecutors;
  private final Set<? extends Label> labels;

  /**
   * @param minExecutors the required minimum number of executors.
   * @param labelsSsv the labels to be checked, space separated.
   */
  @DataBoundConstructor
  public ExecutorCheck(int minExecutors, String labelsSsv) {
    this.minExecutors = minExecutors;
    this.labels = ImmutableSet.copyOf(Label.parse(labelsSsv));
  }

  /**
   * @return the required minimum number of executors.
   */
  public int getMinExecutors() {
    return minExecutors;
  }

  /**
   * @return the labels to check.
   */
  public Set<? extends Label> getLabels() {
    return labels;
  }

  /**
   * @return the labels
   */
  public String getLabelsSsv() {
    return Joiner.on(" ").join(getLabels());
  }

  private Set<Node> getNodes(Label label) {
    // Label#getNodes() catches the matching nodes the first time it is called,
    // and thus won't update its results if new nodes are added/removed.
    // We work around by cloning to new Label object every time.
    Label cloned = new LabelAtom(label.getName());
    return cloned.getNodes();
  }

  /** {@inheritDoc} */
  @Override
  public Result perform(TaskListener taskListener) {
    Set<Node> toCheck = Sets.newHashSet();
    if (getLabels().isEmpty()) {
      toCheck.addAll(Jenkins.getActiveInstance().getNodes());
      // Jenkins#getNodes() excludes the master
      toCheck.add(Jenkins.getActiveInstance());
    } else {
      for (Label label : getLabels()) {
        toCheck.addAll(getNodes(label));
      }
    }

    int numExecutors = 0;
    for (Node node : toCheck) {
      Computer computer = node.toComputer();
      if ((computer == null) || computer.isOffline()) {
        continue;
      }
      numExecutors += node.getNumExecutors();
    }
    if (numExecutors < getMinExecutors()) {
      taskListener.error(Messages.ExecutorCheck_ErrorMessage(
              numExecutors, getMinExecutors(),
              Joiner.on(",").join(getLabels())));
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
      return Messages.ExecutorCheck_DisplayName();
    }

    /**
     * @return autocompletion candidates for {@link Label}s.
     */
    public AutoCompletionCandidates doAutoCompleteLabelsSsv(
        @QueryParameter String value) {
      final AutoCompletionCandidates comps = new AutoCompletionCandidates();

      for (Label label : Jenkins.getActiveInstance().getLabels()) {
        if (label.getName().trim().startsWith(value)) {
          comps.add(label.getName());
        }
      }

      return comps;
    }

  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format("ExecutorCheck [label={%s},minExecutors=%d]",
        Joiner.on(",").join(labels), minExecutors);
  }
}
