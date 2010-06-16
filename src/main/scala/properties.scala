/**
 * Copyright (c) 2010 WeigleWilczek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.weiglewilczek.bnd4sbt

import sbt.{ DefaultProject, MavenStyleScalaPaths, PathFinder }
import scala.collection.immutable.Set

/**
 * Execution environments available for bnd4sbt. As Scala relies on Java 5, only Java 5 and later are supported.
 */
object ExecutionEnvironments extends Enumeration {
  val Java5 = Value("J2SE-1.5")
  val Java6 = Value("JavaSE-1.6")
}

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

  /** The value for Bundle-Version. Defaults to this project's version. */
  protected def bndBundleVersion = project.version.toString

  /** The value for Bundle-Vendor, wrapped in an Option. Defaults to None, i.e. no vendor is defined. */
  protected def bndBundleVendor: Option[String] = None

  /** The value for Bundle-License, wrapped in an Option. Defaults to None, i.e. no license is defined. */
  protected def bndBundleLicense: Option[String] = None

  /** The value for Bundle-RequiredExecutionEnvironment. Defaults to empty set, i.e. no execution environments are defined. */
  protected def bndExecutionEnvironment = Set[ExecutionEnvironments.Value]()

  /** The value for Private-Package. Defaults to "*", i.e. contains everything. */
  protected def bndPrivatePackage = Set("*")

  /** The value for Export-Package. Defaults to empty set, i.e. nothing is exported. */
  protected def bndExportPackage = Set[String]()

  /** The value for Import-Package. Defaults to "*", i.e. everything is imported. */
  protected def bndImportPackage = Set("*")

  /** The value for Dynamic-ImportPackage. Defaults to empty set, i.e. nothing is imported dynamically. */
  protected def bndDynamicImportPackage = Set[String]()

  /** The value for Bundle-Actiavtor, wrapped in an Option. Defaults to None, i.e. no activator is defined. */
  protected def bndBundleActivator: Option[String] = None

  /** The value for Include-Resource. Defaults to the main resources. */
  protected def bndIncludeResource = Set(project.mainResourcesPath.absolutePath)

  /** Should the dependencies be embedded? Defaults to false. */
  protected def bndEmbedDependencies = false

  /** The value for the versionpolicy directive, wrapped in an Option. Defaults to None, i.e. no version policy is defined. */
  protected def bndVersionPolicy: Option[String] = None

  /** Should the nouses directive be applied? Defaults to false. */
  protected def bndNoUses = false

  /** The fileName as part of BNDPlugin.bndOutput. Defaults to this project's defaultJarName. ATTENTION: Better not change this, but artifactBaseName! */
  protected def bndFileName = project.defaultJarName

  /** The output path used by BND. Defaults to the outputPath of this project plus the value of BNDPlugin.bndFileName. ATTENTION: Better not change this, but the appropriate SBT default properties! */
  protected def bndOutput = project.outputPath / bndFileName

  /** The classpath used by BND. Attention: Don't mistake this for the Bundle-Classpath! Defaults to the mainCompilePath of this project. */
  protected def bndClasspath: PathFinder = project.mainCompilePath

  private[bnd4sbt] def bundleClasspath =
    if (bndEmbedDependencies) Set(".") ++ (project.publicClasspath.get filter { !_.isDirectory } map { _.name })
    else Set(".")

  private[bnd4sbt] def resourcesToBeIncluded = {
    val classpathResources = project.publicClasspath.get filter { _ != project.mainCompilePath } map { _.absolutePath }
    val resourceResources = project.dependencies flatMap { 
      _ match {
        case d: MavenStyleScalaPaths => Some(d.mainResourcesPath.absolutePath)
        case _ => None
      }
    }
    if (bndEmbedDependencies) bndIncludeResource ++ classpathResources ++ resourceResources
    else bndIncludeResource
  }
}

/**
 * Gives access to a SBT project.
 */
private[bnd4sbt] trait ProjectAccessor {

  /** The SBT project. */
  protected val project: DefaultProject
}
