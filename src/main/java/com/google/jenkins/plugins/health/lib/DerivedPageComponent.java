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

/**
 * Logic to yield a {@link Report} for this component of the page.
 * @param <Report> The specific subtype of DerivedPageComponentReport to output.
 */
public interface DerivedPageComponent<
    Report extends DerivedPageComponentReport> {
  /**
   * Hook to derive the value of this particular page component.
   *
   * For examples, verify that some piece of Jenkins functionality is working
   * as a component of a health-checking application, or lookup some value for
   * a status metric to display on a status page.
   */
  Report performDerivation();
}
