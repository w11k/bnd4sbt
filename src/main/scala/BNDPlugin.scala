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

import aQute.lib.osgi.Builder
import aQute.lib.osgi.Constants._
import java.util.Properties
import sbt.{ DefaultProject, Path }

/**
 * <p>This plug-in for <a href="code.google.com/p/simple-build-tool">SBT</a> lets you create OSGi bundles
 * from your SBT projects by employing <a href="www.aqute.biz/Code/Bnd">BND</a>.</p>
 * <p>It offers the <code>bndBundle</code> action and overrides the <code>package</code> action.</p>
 */
trait BNDPlugin extends DefaultProject with BNDPluginProperties {

  /**
   * Creates an OSGi bundle from this project by using BND.
   * Initialized by <code>bndBundleAction</code> which could be overridden in order to modify the behavior.
   */
  final lazy val bndBundle = bndBundleAction

  /**
   * Creates an OSGi bundle manifest for this project by using BND.
   * Initialized by <code>bndManifestAction</code> which could be overridden in order to modify the behavior.
   */
  final lazy val bndManifest = bndManifestAction

  /**
   * Creates an OSGi bundle from this project by using BND.
   * <b>Attention</b>: If you override this, you might loose the desired functionality!
   */
  protected def bndBundleAction =
    task {
      bndCreateAction("OSGi bundle", bndOutput, createBundle)
    } dependsOn compile describedAs "Creates an OSGi bundle out of this project by using BND."

  /**
   * Creates an OSGi bundle manifest for this project by using BND.
   * <b>Attention</b>: If you override this, you might loose the desired functionality!
   */
  protected def bndManifestAction =
    task {
      bndCreateAction("OSGi bundle manifest", bndManifestOutput, createManifest)
    } describedAs "Creates an OSGi bundle manifest from this project by using BND."

  private def bndCreateAction(createdProductName: String, createdProductOutput: Path, createOperation: () => Unit) =
    try {
      createOperation()
      log info "Created %s as %s.".format(createdProductName, createdProductOutput)
      None
    }
    catch {
      case e =>
        log error "Error when trying to create %s: %s.".format(createdProductName, e.getMessage)
        Some(e.getMessage)
    }

  /**
   * Overrides the <code>package</code> action with the <code>bndBundle</code> action.
   * <b>Attention</b>: If you override this, you might loose the desired functionality!
   */
  override protected def packageAction = bndBundle

  /**
   * This SBT project.
   */
  override protected[bnd4sbt] final val project = this

  private lazy val classpath = {
    val cp = bndClasspath.getFiles.toArray
    log debug "Using the following classpath for BND: %s".format(cp mkString ":")
    cp
  }

  private lazy val properties = {
    val properties = new Properties

    // SBT packageOptions/ManifestAttributes
    for (
      o <- packageOptions if o.isInstanceOf[ManifestAttributes];
      a <- o.asInstanceOf[ManifestAttributes].attributes
    ) { properties.setProperty(a._1.toString, a._2) }

    // Manifest headers
    properties.setProperty(BUNDLE_SYMBOLICNAME, bndBundleSymbolicName)
    properties.setProperty(BUNDLE_VERSION, bndBundleVersion)
    for ( f <- bndFragmentHost ) { properties.setProperty(FRAGMENT_HOST, f) }
    properties.setProperty(BUNDLE_NAME, bndBundleName)
    for ( v <- bndBundleVendor ) { properties.setProperty(BUNDLE_VENDOR, v) }
    for ( l  <- bndBundleLicense ) { properties.setProperty(BUNDLE_LICENSE, l) }
    properties.setProperty(BUNDLE_REQUIREDEXECUTIONENVIRONMENT, bndExecutionEnvironment mkString ",")
    properties.setProperty(BUNDLE_CLASSPATH, bundleClasspath mkString ",")
    properties.setProperty(PRIVATE_PACKAGE, bndPrivatePackage mkString ",")
    properties.setProperty(EXPORT_PACKAGE, bndExportPackage mkString ",")
    properties.setProperty(IMPORT_PACKAGE, bndImportPackage mkString ",")
    properties.setProperty(DYNAMICIMPORT_PACKAGE, bndDynamicImportPackage mkString ",")
    properties.setProperty(REQUIRE_BUNDLE, bndRequireBundle mkString ",")
    for ( activator <- bndBundleActivator ) { properties.setProperty(BUNDLE_ACTIVATOR, activator) }

    // Directives
    properties.setProperty(INCLUDE_RESOURCE, resourcesToBeIncluded mkString ",")
    for ( v <- bndVersionPolicy ) { properties.setProperty(VERSIONPOLICY, v) }
    if (bndNoUses) properties.setProperty(NOUSES, "true")

    log debug "Using the following properties for BND: %s".format(properties)
    properties
  }

  private def createBundle() {
    createJar write bndOutput.absolutePath
  }

  private def createManifest() {
    import java.io._
    import sbt.FileUtilities._

    createDirectory(bndManifestOutput.asFile.getParentFile, project.log)
    val out = new FileOutputStream(bndManifestOutput.absolutePath)
    try {
      createJar.getManifest write out
    }
    finally {
      out.close()
    }
  }

  private def createJar = {
    val builder = new Builder
    builder setClasspath classpath
    builder setProperties properties

    builder.build
  }
}

