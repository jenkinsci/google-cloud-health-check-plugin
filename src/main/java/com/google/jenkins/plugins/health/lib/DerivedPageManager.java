/*
 * Copyright 2014 Google Inc. All Rights Reserved.
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

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.servlet.ServletException;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import hudson.XmlFile;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import hudson.model.Saveable;
import hudson.util.HttpResponses;

import jenkins.model.Jenkins;

import net.sf.json.JSONObject;

/**
 * Manages {@link DerivedPageComponent} configurations.
 *
 * TODO(nghia): Because {@link DerivedPageZone}s have URLs based on their names,
 * we need to make sure that names are different from one another otherwise some
 * {@link Zone}s will share URLs leading to undefined/undesirable
 * behaviors. We need to add more UI validation code here.
 * @param <Zone> Subtype of DerivedPageZone which stores the configuration of
 * an individual named page instance.
 */
public abstract class DerivedPageManager<Zone extends DerivedPageZone>
    extends ManagementLink implements Saveable {
  /**
   * The configured {@link Zone}s.
   */
  private final Map<String, Zone> derivedPages;
  private final Class<Zone> type;

  /**
   * Load an instance of {@link DerivedPageManager} from Jenkins configuration
   * from disk if the configuration file (by {@link #getConfigFile()}) exists.
   *
   * Otherwise create a new instance.
   */
  public DerivedPageManager(Class<Zone> type) throws IOException {
    this.derivedPages = Maps.newHashMap();
    XmlFile xml = getConfigFile();
    if (xml.exists()) {
      xml.unmarshal(this);
    }
    this.type = type;
  }

  /** {@inheritDoc} */
  @Override
  public void save() throws IOException {
    Jenkins.getActiveInstance().checkPermission(Jenkins.ADMINISTER);
    getConfigFile().write(this);
  }

  /**
   * @return the configuration file that stores derived page configurations.
   */
  private XmlFile getConfigFile() {
    return new XmlFile(
        new File(Jenkins.getActiveInstance().getRootDir(),
            getConfigFileName()));
  }

  /**
   * Return the filename in which to store the configuration serializing a
   * (singleton) subclass instance.
   */
  protected abstract String getConfigFileName();

  /** {@inheritDoc} */
  @Override
  public String getIconFileName() {
    return "monitor.png";
  }

  /**
   * @return the list of {@link DerivedPageComponent} configured.
   */
  public List<Zone> getDerivedPages() {
    return ImmutableList.copyOf(derivedPages.values());
  }

  /**
   * @param zoneName the name of the {@link Zone} to look up.
   * @return the {@link Zone} for the given name, null if none.
   */
  @Nullable public Zone getZone(String zoneName) {
    return derivedPages.get(zoneName);
  }

  @VisibleForTesting
  protected void setDerivedPages(List<Zone> zones) {
    this.derivedPages.clear();
    for (Zone zone: zones) {
      derivedPages.put(zone.getName(), zone);
    }
  }

  /**
   * Handles form submission.
   *
   * @param req the request.
   * @return the response.
   * @throws ServletException if something goes wrong.
   * @throws IOException      if something goes wrong.
   */
  @SuppressWarnings("unused") // by stapler
  public HttpResponse doConfigSubmit(StaplerRequest req)
      throws ServletException, IOException {
    Jenkins.getActiveInstance().checkPermission(Hudson.ADMINISTER);
    JSONObject data = req.getSubmittedForm();
    List<Zone> derivedPages =
        req.bindJSONToList(type, data.get("derivedPages"));
    setDerivedPages(derivedPages);
    save();
    return HttpResponses.redirectToContextRoot();
  }
}
