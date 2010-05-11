package b.internal

import a.A
import org.osgi.framework.{ BundleActivator, BundleContext }

class Activator extends BundleActivator {

  override def start(context: BundleContext) {
    println("B is started! A=%s" format A.name)
  }

  override def stop(context: BundleContext) {
    println("B is stopped!")
  }
}
