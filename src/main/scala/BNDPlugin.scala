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
import sbt.DefaultProject
import scala.collection.Set

trait BNDPlugin extends DefaultProject {

  /** Create an OSGi bundle out of this project by using BND. */
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

  /** The value for Bundle-SymbolicName. Defaults to projectOrganization.projectName. */
  protected def bndBundleSymbolicName = organization + "." + name

  /** The value for Bundle-Name. Defaults to BNDPlugin.bndBundleSymbolicName. */
  protected def bndBundleName = bndBundleSymbolicName

  /** The value for Bundle-Version. Defaults to projectVersion . */
  protected def bndBundleVersion = version.toString

  /** The value for Private-Package. Defaults to "*", i.e. contains everything. */
  protected def bndPrivatePackage = Set("*")

  /** The value for Export-Package. Defaults to nothing being exported. */
  protected def bndExportPackage: Set[String] = Set.empty

  /** The value for Import-Package. Defaults to "*", i.e. everything is imported. */
  protected def bndImportPackage: Set[String] = Set("*")

  /** The classpath used by BND. Defaults to the mainCompilePath of this project. */
  protected def bndClasspath = mainCompilePath

  /** The output path used by BND. Defaults to the outputPath of this project plus the value of BNDPlugin.bndFileName. */
  protected def bndOutput = outputPath / bndFileName

  /** The fileName as part of BNDPlugin.bndOutput. Defaults to projectName-projectVersion.jar. */
  protected def bndFileName = "%s-%s.jar".format(name, version)

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
