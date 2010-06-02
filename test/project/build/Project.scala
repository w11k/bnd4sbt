import com.weiglewilczek.bnd4sbt.BNDPlugin
import sbt._

class TestProject(info: ProjectInfo) extends ParentProject(info: ProjectInfo) {

  lazy val a = project("a", "a", new A(_))
  class A(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    import com.weiglewilczek.bnd4sbt.ExecutionEnvironments._
    override def bndBundleLicense = Some("Eclipse Public License v1.0")
    override def bndExportPackage = Set("a;version=1.0")
    override def bndBundleRequiredExecutionEnvironment = Set(Java5, Java6)
    override def bndDynamicImportPackage = Set("x.y.z")
    override def bndVersionPolicy = Some("[$(@),$(@)]")
    override def bndNoUses = true
  }

  lazy val b = project("b", "b", new B(_), a)
  class B(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    lazy val osgiCore = "org.osgi" % "org.osgi.core" % "4.2.0" % "provided"
    override def bndBundleActivator = Some("b.internal.Activator")
  }

  lazy val c = project("c", "c", new C(_))
  class C(info: ProjectInfo) extends DefaultProject(info) {
    lazy val commonsIO = "commons-io" % "commons-io" % "1.4"
  }

  lazy val d = project("d", "d", new D(_), c)
  class D(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    lazy val osgiCore = "org.osgi" % "org.osgi.core" % "4.2.0" % "provided"
    lazy val commonsLogging = "commons-logging" % "commons-logging-api" % "1.1"
    override def bndBundleActivator = Some("d.internal.Activator")
    override def bndEmbedDependencies = true
  }
}
