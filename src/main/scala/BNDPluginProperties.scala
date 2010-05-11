/**
 * Copyright (c) 2010 WeigleWilczek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.weiglewilczek.bnd4sbt

import sbt.{ MavenStyleScalaPaths, Path }
import scala.collection.Set

/**
 * Properties for BND with sensible defaults. 
 */
private[bnd4sbt] trait BNDPluginProperties extends ProjectAccessor {

  /**
   * The value for Bundle-SymbolicName. Defaults to projectOrganization.projectName with duplicate subsequences
   * removed, e.g. "a.b.c" + "c-d" => "a.b.c.d". Recognized namespace separators are "." and "-".
   */
  protected def bndBundleSymbolicName: String = {
    def split(s: String) = s split """\.|-""" match {
      case a if a.size > 1 => a
      case _ => s split "-"
    }
    val organization = split(project.organization).toList
    val name = split(project.name).toList
    def concat(nameTaken: List[String], nameDropped: List[String]): List[String] = nameTaken match {
      case Nil => organization ::: nameDropped
      case _ if organization endsWith nameTaken => organization ::: nameDropped
      case _ => concat(nameTaken.init, nameTaken.last :: nameDropped)
    }
    concat(name, Nil) mkString "."
  }

  /** The value for Bundle-Name. Defaults to BNDPlugin.bndBundleSymbolicName. */
  protected def bndBundleName: String = bndBundleSymbolicName

  /** The value for Bundle-Version. Defaults to projectVersion. */
  protected def bndBundleVersion: String = project.version.toString

  /** The value for Private-Package. Defaults to "*", i.e. contains everything. */
  protected def bndPrivatePackage: Set[String] = Set("*")

  /** The value for Export-Package. Defaults to empty set, i.e. nothing is exported. */
  protected def bndExportPackage: Set[String] = Set.empty

  /** The value for Import-Package. Defaults to "*", i.e. everything is imported. */
  protected def bndImportPackage: Set[String] = Set("*")

  /** The value for Bundle-Activtor, wrapped in an Option. Defaults to None. */
  protected def bndBundleActivator: Option[String] = None

  /** The value for Include-Resource. Defaults to the main resources. */
  protected def bndIncludeResource: Set[String] = Set(project.mainResourcesPath.relativePath)

  /** The classpath used by BND. Defaults to the mainCompilePath of this project. */
  protected def bndClasspath: Path = project.mainCompilePath

  /** The output path used by BND. Defaults to the outputPath of this project plus the value of BNDPlugin.bndFileName. */
  protected def bndOutput: Path = project.outputPath / bndFileName

  /** The fileName as part of BNDPlugin.bndOutput. Defaults to projectName-projectVersion.jar. */
  protected def bndFileName: String = "%s-%s.jar".format(project.name, project.version)
}

/**
 * Gives access to a SBT project.
 */
private[bnd4sbt] trait ProjectAccessor {

  /** The SBT project. */
  protected val project: MavenStyleScalaPaths
}
