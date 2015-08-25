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

package com.google.jenkins.plugins.health.HealthCheckManager

import jenkins.model.Jenkins
import com.google.jenkins.plugins.health.HealthZone

def f=namespace(lib.FormTagLib)
def l=namespace(lib.LayoutTagLib)
def st=namespace("jelly:stapler")
def d=namespace("jelly:define")

l.layout(norefresh:true, title:my.displayName, permission:Jenkins.ADMINISTER) {
    st.include(it:Jenkins.getInstance(), page:"sidepanel.jelly")
    l.main_panel {
      h1 {
        img(src:"${imagesURL}/48x48/monitor.png", height:48,width:48)
        text(my.displayName)
      }

      f.form(method:"post", name:"config", action:"configSubmit") {
        f.block {
          f.hetero_list(addCaption:_("Add Zone"),
                        deleteCaption:_("Delete Zone"),
                        items:my.derivedPages,
                        name:"derivedPages",
                        hasHeader:true,
                        descriptors:HealthZone.getDescriptors())
        }
        f.block {
          f.submit(value:_("Save"))
        }
      }

    }
}
