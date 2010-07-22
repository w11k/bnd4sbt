/**
 * Copyright (c) 2010 WeigleWilczek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.weiglewilczek.bnd4sbt

import aQute.lib.osgi.Builder
import aQute.lib.osgi.Constants._
import java.util.Properties
import sbt.DefaultProject

/**
 * This BND plugin for SBT offers the following actions:
 * <ul>
 *   <li>bndBundle: Creates an OSGi bundle out of this project by using BND</li>
 * </ul>
 * Additionally the package action is overridden with the bndBundle action.
 */
trait BNDPlugin extends DefaultProject with BNDPluginProperties {

  /** Creates an OSGi bundle out of this project by using BND. Initialized by bndBundleAction which can be overridden in order to modify the behavior. */
  final lazy val bndBundle = bndBundleAction

  /** Creates an OSGi bundle out of this project by using BND. Attention: If you override this, you might loose the bnd4sbt functionality. */
  protected def bndBundleAction =
    task {
      try {
        createBundle()
        log info "Created OSGi bundle at %s.".format(bndOutput)
        None
      } catch {
        case e =>
          log error "Error when trying to create OSGi bundle: %s.".format(e.getMessage)
          Some(e.getMessage)
      }
    } dependsOn compile describedAs "Creates an OSGi bundle out of this project by using BND."

  /** Overrides the package action with the bndBundle action. Attention: If you override this, you might loose the bnd4sbt functionality. */
  override protected def packageAction = bndBundle

  /** This SBT project. */
  final override protected[bnd4sbt] val project = this

  private def createBundle() {
    val builder = new Builder
    builder setProperties properties
    builder setClasspath classpath
    val jar = builder.build
    jar write bndOutput.absolutePath
  }

  private def classpath = {
    val cp = bndClasspath.getFiles.toArray
    log debug "Using the following classpath for BND: %s".format(cp mkString ":")
    cp
  }

  private def properties = {
    val properties = new Properties

    // SBT packageOptions/ManifestAttributes
    for {
      o <- packageOptions; if o.isInstanceOf[ManifestAttributes]
      a <- o.asInstanceOf[ManifestAttributes].attributes
    } properties.setProperty(a._1.toString, a._2)

    // Manifest headers
    properties.setProperty(BUNDLE_SYMBOLICNAME, bndBundleSymbolicName)
    properties.setProperty(BUNDLE_VERSION, bndBundleVersion)
    properties.setProperty(BUNDLE_NAME, bndBundleName)
    for { v <- bndBundleVendor } properties.setProperty(BUNDLE_VENDOR, v)
    for { l  <- bndBundleLicense } properties.setProperty(BUNDLE_LICENSE, l)
    properties.setProperty(BUNDLE_REQUIREDEXECUTIONENVIRONMENT, bndExecutionEnvironment mkString ",")
    properties.setProperty(BUNDLE_CLASSPATH, bundleClasspath mkString ",")
    properties.setProperty(PRIVATE_PACKAGE, bndPrivatePackage mkString ",")
    properties.setProperty(EXPORT_PACKAGE, bndExportPackage mkString ",")
    properties.setProperty(IMPORT_PACKAGE, bndImportPackage mkString ",")
    properties.setProperty(DYNAMICIMPORT_PACKAGE, bndDynamicImportPackage mkString ",")
    properties.setProperty(REQUIRE_BUNDLE, bndRequireBundle mkString ",")
    for { activator <- bndBundleActivator } properties.setProperty(BUNDLE_ACTIVATOR, activator)

    // Directives
    properties.setProperty(INCLUDE_RESOURCE, resourcesToBeIncluded mkString ",")
    for { v <- bndVersionPolicy } properties.setProperty(VERSIONPOLICY, v)
    if (bndNoUses) properties.setProperty(NOUSES, "true")

    log debug "Using the following properties for BND: %s".format(properties)
    properties
  }
}
