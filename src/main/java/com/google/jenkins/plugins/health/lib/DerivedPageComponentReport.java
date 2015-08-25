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

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import static com.google.common.base.Preconditions.checkNotNull;

import hudson.model.Result;

/**
 * A tuple of a {@link Result} derivation success and {@link Value} outcome.
 * @param <Value> Result or product of the derivation, other than the indication
 * if it is successfully produced.
 */
@ExportedBean
public class DerivedPageComponentReport<Value> {
  private final Result result;
  /**
   * @return the {@link Result} of for executing the logic to obtaining the
   * {@link Value} for this component of the derived page.  Inheriting
   * implementations may establish their own convention for how to handle an
   * unsuccessful Value derivation.  Some use cases may omit the component,
   * some might invalidate the entire derived page, some might substitute a
   * default or null-indicative value, or some might ignore 'success' entirely.
   */
  @Exported
  public Result getResult() {
    return this.result;
  }

  private final Value value;
  /**
   * @return the value of the derived component.
   */
  @Exported
  public Value getValue() {
    return this.value;
  }
  /**
   * @param result {@link Result} indicating if derivation of value is
   * successful.
   * @param value {@link Value} calculated for this component of the derived
   * page.
   */
  public DerivedPageComponentReport(Result result, Value value) {
    this.result = checkNotNull(result);
    this.value = checkNotNull(value);
  }
}
