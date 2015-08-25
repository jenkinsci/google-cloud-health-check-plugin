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

import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import hudson.model.ModelObject;

/**
 * A {@link DerivedPageZone} is a named container for
 * {@link DerivedPageComponent}s.  Separating pages into different grouping
 * gives flexibility to allow for different severity levels, different
 * listeners, projects or users, etc.  For example, in a Jenkins health-testing
 * implementation, critical health metrics may require immediate actions, while
 * low severity ones may need a reaction only once a day.
 *
 * Each {@link DerivedPageZone} has a unique URL path
 * "/${DerivedPageAction.getUrlName}/zone/${zoneName}".
 * @param <Component> The type of DerivedPageComponent be that {@link Component}
 * this Zone stores.
 * @param <Report> The specific subtype of DerivedPageComponentReport that
 * {@link Component} should output.
 */
@ExportedBean
public abstract class DerivedPageZone<
    Component extends DerivedPageComponent<Report>,
    Report extends DerivedPageComponentReport>
    implements ModelObject {
  /**
   * @return the name of this zone.
   */
  public String getName() {
    return name;
  }
  private final String name;

  /**
   * @return the list of {@link DerivedPageComponent} in this
   * {@link DerivedPageZone}.
   */
  public List<Component> getComponents() {
    return ImmutableList.copyOf(components);
  }
  private final List<Component> components;

  /**
   * @param name the name of this {@link DerivedPageZone}.
   * @param components a list of {@link DerivedPageComponent}s to contain.
   */
  public DerivedPageZone(String name, List<Component> components) {
    this.name = checkNotNull(name);
    this.components = ImmutableList.copyOf(components);
  }

  /**
   * Provide the key by which a {@link Component}'s derived result will be
   * indexed and displayed to the user.  For examples, a name passed to the
   * component's constructor on page configuration, or the name of the component
   * class when there will be no reuse within a page.
   */
  protected abstract String namePageComponent(Component component);

  /**
   * Return the component's Report indicating the success of the derivation and
   * any result obtained from it.
   */
  protected abstract Report derivePageComponent(Component component);

  /**
   * @return results of {@link DerivedPageComponent}s.
   */
  @Exported
  public Map<String, Report> getReports() {
    Map<String, Report> results = Maps.newLinkedHashMap();
    for (Component component : getComponents()) {
      results.put(namePageComponent(component), derivePageComponent(component));
    }
    return results;
  }
}
