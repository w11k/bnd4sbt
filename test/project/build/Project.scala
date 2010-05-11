import com.weiglewilczek.bnd4sbt.BNDPlugin
import sbt._

class TestProject(info: ProjectInfo) extends ParentProject(info: ProjectInfo) {
  
  lazy val a = project("a", "a", new A(_))
  class A(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    override def bndExportPackage = Set("a;version=1.0")
  }

  lazy val b = project("b", "b", new B(_), a)
  class B(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    lazy val osgiCore = "org.osgi" % "org.osgi.core" % "4.2.0" % "provided"
    override def bndBundleActivator = Some("b.internal.Activator")
  }
}
