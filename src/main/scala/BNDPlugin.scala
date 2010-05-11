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
  lazy val bndBundle = bndBundleAction

  /** Creates an OSGi bundle out of this project by using BND. Override to modify the behavior of the bndBundle action. */
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

  /** Overrides the package action with the bndBundle action. */
  override protected def packageAction = bndBundle

  /** This SBT project. */
  override protected val project = this

  private def createBundle() {
    val builder = new Builder
    builder setProperties properties
    builder setClasspath Array(bndClasspath.absolutePath)
    val jar = builder.build
    jar write bndOutput.absolutePath
  }

  private def properties = {
    val properties = new Properties
    properties.setProperty(BUNDLE_SYMBOLICNAME, bndBundleSymbolicName)
    properties.setProperty(BUNDLE_VERSION, bndBundleVersion)
    properties.setProperty(BUNDLE_NAME, bndBundleName)
    properties.setProperty(PRIVATE_PACKAGE, bndPrivatePackage mkString ",")
    properties.setProperty(EXPORT_PACKAGE, bndExportPackage mkString ",")
    properties.setProperty(IMPORT_PACKAGE, bndImportPackage mkString ",")
    for { activator <- bndBundleActivator } properties.setProperty(BUNDLE_ACTIVATOR, activator)
    properties.setProperty(INCLUDE_RESOURCE, bndIncludeResource mkString ",")
    log debug "Using the following properties for BND: %s".format(properties)
    properties
  }
}
