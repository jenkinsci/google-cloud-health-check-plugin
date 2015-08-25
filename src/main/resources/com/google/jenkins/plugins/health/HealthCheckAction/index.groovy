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

package com.google.jenkins.plugins.health.HealthCheckAction

import hudson.Functions
import jenkins.model.Jenkins

def f=namespace(lib.FormTagLib)
def l=namespace(lib.LayoutTagLib)
def st=namespace("jelly:stapler")

l.layout(norefresh:true, title:my.displayName, permission:my.CHECK) {
    st.include(it:Jenkins.getInstance(), page:"sidepanel.jelly")
    l.main_panel {
      h1 {
        img(src:"${imagesURL}/48x48/monitor.png", height:48, width:48)
        text(my.displayName)
      }
      p()

      my.zones.each { zone ->
        h2 {
          img(src:"${imagesURL}/48x48/${zone.combinedReport.result.color.image}",
              height:24, width:24)
          text(" ")
          a(href:"zone/${zone.name}") {
            text("${zone.name}")
          }
          hr()
        }
        text(zone.combinedReport.value)
      }
    }
}
