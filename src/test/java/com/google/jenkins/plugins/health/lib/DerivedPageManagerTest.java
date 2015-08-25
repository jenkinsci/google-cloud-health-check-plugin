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

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;

import static com.google.jenkins.plugins.health.lib.DerivedPageZoneTest.NamedDerivedPageZone;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import hudson.ExtensionList;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;


/**
 * Unit test for {@link DerivedPageManager}.
 */
public class DerivedPageManagerTest {
  static class FakeComponent implements DerivedPageComponent<
      DerivedPageComponentReport<String>> {

    private final Integer value;

    FakeComponent(Integer value) {
      this.value = value;
    }
    @Override
    public DerivedPageComponentReport<String> performDerivation() {
      return null;
    }

    public Integer getValue() {
      return value;
    }
  }
  static class MockDerivedPageManager
      extends DerivedPageManager<NamedDerivedPageZone> {
    public MockDerivedPageManager() throws IOException {
      super(NamedDerivedPageZone.class);
    }

    @Override
    protected String getConfigFileName() {
      return "myTestingFilename.xml";
    }

    @Override
    public String getUrlName() {
      return "fooConsole";
    }

    @Override
    public String getDisplayName() {
      return "myDisplayName";
    }
  }

  @Rule public JenkinsRule jenkins = new JenkinsRule();
  MockDerivedPageManager underTest;

  @Before
  public void setup() throws IOException {
    underTest = new MockDerivedPageManager();
    // The mock cannot be made an extension as a real class would.
    ExtensionList<MockDerivedPageManager> extensionList =
        Jenkins.getInstance().getExtensionList(MockDerivedPageManager.class);
    // Fake the extension setup so it doesn't register in all the other tests.
    if (extensionList.isEmpty()) {
      extensionList.add(extensionList.size(), underTest);
    }
  }

  @Test
  public void construct_no_xml() {
    // without a file, this shouldn't show anything
    assertThat(underTest.getDerivedPages(), empty());
  }

  @Test
  public void construct_from_xml() throws IOException {
    // without a file, this shouldn't show anything
    assertThat(underTest.getDerivedPages(), empty());

    // create some configuration and save to a file.
    FakeComponent fakeComponent = new FakeComponent(2);
    underTest.setDerivedPages(ImmutableList.of(new NamedDerivedPageZone(
        "some-zone",
        ImmutableList.<DerivedPageComponent<
            DerivedPageComponentReport<String>>>of(fakeComponent))));
    underTest.save();

    underTest = new MockDerivedPageManager();
    assertEquals(1, underTest.getDerivedPages().size());
    assertEquals(1, underTest.getZone("some-zone").getComponents().size());
    assertThat(underTest.getZone("some-zone").getComponents().get(0),
        instanceOf(FakeComponent.class));
    FakeComponent extracted =
        (FakeComponent) underTest.getZone("some-zone").getComponents().get(0);
    assertEquals(2, extracted.getValue().intValue());
  }

  @Test
  public void getters() {
    MockDerivedPageManager underTest = Iterables.getOnlyElement(
            jenkins.getInstance().getExtensionList(
                MockDerivedPageManager.class));
    assertNotNull(underTest.getUrlName());
    assertNotNull(underTest.getIconFileName());
    assertEquals("myDisplayName",
        underTest.getDisplayName());
  }

  @Test
  public void doConfigSubmit() throws Exception {
    StaplerRequest mockRequest = mock(StaplerRequest.class);
    JSONObject data = new JSONObject();
    JSONObject zoneData = new JSONObject();
    data.put("derivedPages", zoneData);
    when(mockRequest.getSubmittedForm()).thenReturn(data);
    List<NamedDerivedPageZone> zones = ImmutableList.of(
        new NamedDerivedPageZone("standard", ImmutableList.<
            DerivedPageComponent<DerivedPageComponentReport<String>>> of()));
    when(mockRequest.bindJSONToList(NamedDerivedPageZone.class, zoneData)).
        thenReturn(zones);

    underTest.doConfigSubmit(mockRequest);
    assertEquals(1, underTest.getDerivedPages().size());
    assertThat(underTest.getZone("standard").getComponents(), empty());
  }
}
