/*
 * Copyright 2010-2011 Weigle Wilczek GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weiglewilczek.bnd4sbt

import org.specs.Specification
import org.specs.mock.Mockito
import sbt.{ DefaultProject, Path => SbtPath }
import scala.collection.immutable.Set

class BNDPluginPropertiesSpec extends Specification with Mockito {

  "Calling bndBundleSymbolicName" should {

    "return organization.name if organization does not end with a sequence name begins with" in {
      new BNDPluginProperties {
        override val project = mock[DefaultProject]
        project.organization returns "organization"
        project.name returns "name"
        bndBundleSymbolicName mustEqual "organization.name"
      }
    }

    "return a.b.c.d if organization is a.b.c and name is c.d" in {
      new BNDPluginProperties {
        override val project = mock[DefaultProject]
        project.organization returns "a.b.c"
        project.name returns "c.d"
        bndBundleSymbolicName mustEqual "a.b.c.d"
      }
    }

    "return a.b.c.d if organization is a.b.c and name is c-d" in {
      new BNDPluginProperties {
        override val project = mock[DefaultProject]
        project.organization returns "a.b.c"
        project.name returns "c-d"
        bndBundleSymbolicName mustEqual "a.b.c.d"
      }
    }

    "return a.b.c.d.e if organization is a.b.c.d and name is c-d.e" in {
      new BNDPluginProperties {
        override val project = mock[DefaultProject]
        project.organization returns "a.b.c.d"
        project.name returns "c-d.e"
        bndBundleSymbolicName mustEqual "a.b.c.d.e"
      }
    }

    "return a.b.c.b.c if organization is a-b and name is c.b-c" in {
      new BNDPluginProperties {
        override val project = mock[DefaultProject]
        project.organization returns "a-b"
        project.name returns "c.b-c"
        bndBundleSymbolicName mustEqual "a.b.c.b.c"
      }
    }
  }

  "Calling resourcesToBeIncluded" should {

    "return the result of bndIncludeResource for bndEmbedDependencies false" in {
      /*new BNDPluginProperties {
        override val project = mock[DefaultProject]
        project.mainResourcesPath returns SbtPath.userHome
        project.publicClasspath returns SbtPath.userHome
        resourcesToBeIncluded mustEqual Set(SbtPath.userHome.absolutePath)
      }*/
    }

    "return the result of bndIncludeResource plus the public classpath for bndEmbedDependencies true" in {
      /*new BNDPluginProperties {
        override val project = mock[DefaultProject]
        override val bndEmbedDependencies = true
        val tmpDir = SbtPath.fileProperty("java.io.tmpdir")
        val userHome = SbtPath.userHome
        project.mainResourcesPath returns userHome
        project.publicClasspath returns tmpDir
        resourcesToBeIncluded mustEqual Set(userHome.relativePath, tmpDir.relativePath)
      }*/
    }
  }

  "Calling bundleClasspath" should {

    """return "." for bndEmbedDependencies false""" in {
      new BNDPluginProperties {
        override val project = mock[DefaultProject]
        bundleClasspath mustEqual Set(".")
      }
    }

    """return "." plus the public classpath for bndEmbedDependencies true""" in {
      /*new BNDPluginProperties {
        override val project = mock[DefaultProject]
        override val bndEmbedDependencies = true
        val userHome = SbtPath.userHome
        project.publicClasspath returns userHome
        bundleClasspath mustEqual Set(".", userHome.relativePath)
      }*/
    }
  }
}
