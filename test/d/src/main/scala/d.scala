package com.weiglewilczek.bnd4sbttest.d.internal

import com.weiglewilczek.bnd4sbttest.c.C
import org.apache.commons.logging.LogFactory
import org.osgi.framework.{ BundleActivator, BundleContext }

class Activator extends BundleActivator {

  override def start(context: BundleContext) {
    println("D is started! C=%s" format C.name)
  }

  override def stop(context: BundleContext) {
    println("D is stopped!")
  }

  private val log = LogFactory.getLog(classOf[Activator])
}
