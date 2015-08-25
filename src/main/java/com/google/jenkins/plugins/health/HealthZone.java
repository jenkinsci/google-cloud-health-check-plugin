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
import java.util.Map;
import javax.annotation.Nullable;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.ExportedBean;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.jenkins.plugins.health.lib.DerivedPageZone;

import hudson.Extension;
import hudson.model.Api;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Result;

import jenkins.model.Jenkins;

/**
 * A {@link HealthZone} is a named container for {@link HealthCheck}s.  The main
 * use case for separating the checks into different grouping is to allow for
 * different severity levels, different listeners, etc...  For examples,
 * critical health metrics may require immediate actions, while low severity
 * ones may need a reaction only once a day.
 *
 * Each {@link HealthZone} has a unique URL "/health/zone/${zoneName}".
 */
@ExportedBean
public class HealthZone extends DerivedPageZone<HealthCheck, HealthCheckReport>
    implements Describable<HealthZone> {
  /**
   * @param name the name of this {@link HealthZone}.
   * @param healthChecks a list of {@link HealthCheck}s to contain.
   */
  @DataBoundConstructor
  public HealthZone(String name, List<HealthCheck> healthChecks) {
    super(name, healthChecks);
  }

  /** {@inheritDoc} */
  @Override
  public String getDisplayName() {
    return Messages.HealthZone_DisplayName(getName());
  }

  public Api getApi() { // for remote access API
    HealthCheckReport report = getCombinedReport();
    if (report.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
      return new Api(this);
    } else {
      return new ErrorApi(this, report);
    }
  }

  /**
   * An implementation of {@link Api} that can give a better error message for
   * our use case.
   */
  public static class ErrorApi extends Api {
    private final HealthCheckReport report;

    /**
     * @param zone the {@link HealthZone} in bad state.
     * @param report the {@link HealthCheckReport} detailing the errors.
     */
    public ErrorApi(HealthZone zone, HealthCheckReport report) {
      super(zone);
      this.report = checkNotNull(report);
    }

    @VisibleForTesting HealthZone getZone() {
      return (HealthZone) this.bean;
    }

    private String getErrorMessage() {
      return Messages.HealthZone_BadStatus(
          getZone().getName(), report.getResult(), report.getValue());
    }

    private void sendError(StaplerResponse response) throws IOException {
      response.sendError(StaplerResponse.SC_SERVICE_UNAVAILABLE,
          getErrorMessage());
    }

    /** {@inheritDoc} */
    @Override
    public void doXml(StaplerRequest req, StaplerResponse rsp,
        @QueryParameter String xpath,
        @QueryParameter String wrapper,
        @QueryParameter String tree,
        @QueryParameter int depth) throws IOException {
      sendError(rsp);
    }

    /** {@inheritDoc} */
    @Override
    public void doJson(StaplerRequest req, StaplerResponse rsp)
        throws IOException {
      sendError(rsp);
    }

    /** {@inheritDoc} */
    @Override
    public void doPython(StaplerRequest req, StaplerResponse rsp)
        throws IOException {
      sendError(rsp);
    }
  }

  @VisibleForTesting HealthCheckManager getHealthCheckManager() {
    return Iterables.getOnlyElement(
        Jenkins.getActiveInstance().getExtensionList(HealthCheckManager.class));
  }

  protected String namePageComponent(HealthCheck component) {
    return component.toString();
  }

  /**
   * {@inheritDoc}
   *
   * In this implementation the success indicator is the value, and debugging
   * information is appended as the report's value.
   */
  @Override
  protected HealthCheckReport derivePageComponent(HealthCheck check) {
    return check.performDerivation();
  }

  /**
   * @return the result combined from individual {@link HealthCheck} results.
   */
  public HealthCheckReport getCombinedReport() {
    Result combined = Result.SUCCESS;
    StringBuilder log = new StringBuilder();
    for (Map.Entry<String, HealthCheckReport> entry : getReports().entrySet()) {
      String check = entry.getKey();
      HealthCheckReport report = entry.getValue();
      combined = combined.combine(report.getResult());
      if (report.getResult().isWorseThan(Result.SUCCESS)) {
        log.append(String.format(
            "[%s]: '%s' due to:%n%s%n", report.getResult(), check,
            report.getValue()));
      }
    }
    return new HealthCheckReport(combined, log.toString());
  }

  public Result getCombinedResult() {
    return getCombinedReport().getResult();
  }

  /**
   * {@link Descriptor} of {@link HealthZone}.
   */
  @Extension
  public static class DescriptorImpl extends Descriptor<HealthZone> {
    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
      return Messages.HealthZone_DescriptorDisplayName();
    }
  }

  /**
   * @return the list of {@link Descriptor} for {@link HealthZone}s.
   */
  public static List<Descriptor<HealthZone>> getDescriptors() {
    return Jenkins.getActiveInstance().getDescriptorList(HealthZone.class);
  }

  @Override @Nullable
  public Descriptor<HealthZone> getDescriptor() {
    return Jenkins.getActiveInstance().getDescriptorOrDie(getClass());
  }
}
