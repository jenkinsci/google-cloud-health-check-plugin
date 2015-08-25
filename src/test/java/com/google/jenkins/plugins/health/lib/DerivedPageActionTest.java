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
package com.google.jenkins.plugins.health.lib;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.acegisecurity.Authentication;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import com.google.common.collect.ImmutableList;
import com.google.jenkins.plugins.health.lib.DerivedPageManagerTest.MockDerivedPageManager;

import hudson.ExtensionList;
import hudson.security.ACL;
import hudson.security.AccessDeniedException2;
import hudson.security.AuthorizationStrategy;
import hudson.security.Permission;
import jenkins.model.Jenkins;

/**
 * Unit tests for {@link DerivedPageAction}.
 */
public class DerivedPageActionTest {
  class MockDerivedPageAction extends DerivedPageAction {

    @Override
    protected Permission getReadPermission() {
      return Jenkins.READ;
    }

    MockDerivedPageAction() {
      super();
    }

    @Override
    public String getDisplayName() {
      return null;
    }

    @Override
    public String getUrlName() {
      return null;
    }

    @Override
    protected DerivedPageManager getDerivedPageManager() {
      return fakeManager;
    }
  }
  @Rule public JenkinsRule jenkins = new JenkinsRule();
  DerivedPageAction underTest;
  MockDerivedPageManager fakeManager;

  @Before
  public void setup() throws Exception {
    underTest = new MockDerivedPageAction();
    fakeManager = new MockDerivedPageManager();
    ExtensionList<MockDerivedPageManager> extensionList =
        Jenkins.getInstance().getExtensionList(MockDerivedPageManager.class);
    if (extensionList.isEmpty()) {
      extensionList.add(extensionList.size(), fakeManager);
    }
  }

  @Test
  public void getters_noauth() {
    assertThat(underTest.getZones(), empty());
    assertNull(underTest.getZone("not-exist"));
    assertNotNull(underTest.getIconFileName());
    assertEquals(underTest, underTest.getTarget());
    assertNotNull(underTest.getApi());
  }

  @Test(expected = AccessDeniedException2.class)
  public void getters_auth() {
    jenkins.getInstance().setAuthorizationStrategy(noneShallPass());
    assertNull(underTest.getIconFileName());

    underTest.getTarget();
  }

  /** An {@link AuthorizationStrategy} that allows no one */
  private AuthorizationStrategy noneShallPass() {
    return new AuthorizationStrategy() {
      @Override
      public List<String> getGroups() {
        return ImmutableList.of();
      }

      @Override
      public ACL getRootACL() {
        return new ACL() {
          @Override
          public boolean hasPermission(Authentication auth, Permission perm) {
            return false;
          }
        };
      }
    };
  }
}
