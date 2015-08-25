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

package com.google.jenkins.plugins.health.HealthZone

import jenkins.model.Jenkins
import com.google.jenkins.plugins.health.HealthCheck

def f=namespace(lib.FormTagLib)
def l=namespace(lib.LayoutTagLib)
def st=namespace("jelly:stapler")

f.entry(title:_("Zone name"), field:"name") {
   f.textbox(clazz:"required")
}

f.block {
    f.hetero_list(addCaption:_("Add Health Check"),
                  deleteCaption:_("Delete Check"),
                  hasHeader:true,
                  name:"healthChecks",
                  items:(instance == null) ? [] : instance.components,
                  descriptors:HealthCheck.all())
}

