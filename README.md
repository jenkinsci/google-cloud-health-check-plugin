Google Health Check Plugin
====================

This plugin provides simple health checks and extension points for additional health checks.

Usage
===
From the Manage Jenkins page, go to Manage Health Checks. Add a Health Zone to create a logical group of health checks. Within a Health Zone, add a Health Check for each health condition to be tracked. As of this writing, the plugin includes two health check types:
* Minimum number of executors - fails if insufficient executors are available. Optionally allows labels to be specified, in which case only executors with at least one of those labels will count toward the minimum.
* Classes loaded - fails if the specified classes were not loaded by the Jenkins class loader.
The HealthCheck extension point provides an opportunity for plugin developers to create additional types of health checks.

After health checks are configured, the Health Check Summary page in Jenkins' left navigation menu shows a summary of the current health zones; viewing each zone allows verification of each health check's current state.

Development
===========

How to build
--------------

	mvn clean verify

Creates the plugin HPI package for use with Jenkins.


License
-------

	(The Apache v2 License)

    Copyright 2015 Google Inc. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
