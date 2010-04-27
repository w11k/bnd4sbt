/**
 * Copyright (c) 2010 WeigleWilczek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.weiglewilczek.bnd4sbt

import aQute.lib.osgi.Builder
import java.util.Properties
import sbt.BasicScalaProject

trait BNDPlugin extends BasicScalaProject with BNDPluginProperties {

  /** Creates an OSGi bundle out of this project by using BND. */
  lazy val bndBundle =
    task {
      try {
        createBundle()
        log info "Created OSGi bundle at %s.".format(bndOutput)
        None
      } catch { case e =>
        log error "Error when trying to create OSGi bundle: %s.".format(e.getMessage)
        Some(e.getMessage)
      }
    } dependsOn test describedAs "Creates an OSGi bundle out of this project by using BND."

  private def createBundle() {
    val builder = new Builder
    builder setProperties properties
    builder setClasspath Array(bndClasspath.absolutePath)
    val jar = builder.build
    jar write bndOutput.absolutePath
  }

  private def properties = {
    val properties = new Properties
    properties.setProperty("Bundle-SymbolicName", bndBundleSymbolicName)
    properties.setProperty("Bundle-Version", bndBundleVersion)
    properties.setProperty("Bundle-Name", bndBundleName)
    properties.setProperty("Private-Package", bndPrivatePackage mkString ",")
    properties.setProperty("Export-Package", bndExportPackage mkString ",")
    properties.setProperty("Import-Package", bndImportPackage mkString ",")
    properties
  }
}
