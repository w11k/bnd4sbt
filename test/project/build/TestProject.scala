import com.weiglewilczek.bnd4sbt._
import sbt._

class TestProject(info: ProjectInfo) extends ParentProject(info: ProjectInfo) {

  lazy val a = project("a", "a", new A(_))

  class A(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    import ExecutionEnvironment._

    lazy val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"

    override def bndBundleLicense = Some("Apache 2.0 License")
    override def bndExportPackage = Seq("com.weiglewilczek.bnd4sbttest.a;version=\"1.0\"")
    override def bndExecutionEnvironment = Set(Java5, Java6)
    override def bndDynamicImportPackage = Seq("x.y.z")
    override def bndVersionPolicy = Some("[$(@),$(@)]")
    override def bndNoUses = true
    override def packageOptions =
      ManifestAttributes("Bundle-Name" -> "ILLEGAL") ::
      ManifestAttributes("Test" -> "TEST") :: // Only uppercase headers are copied to the manifest by BND!
      Nil
  }

  lazy val b = project("b", "b", new B(_), a)

  class B(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    lazy val osgiCore = "org.osgi" % "org.osgi.core" % "4.2.0" % "provided"
    override def bndBundleActivator = Some("com.weiglewilczek.bnd4sbttest.b.internal.Activator")
    override def bndFragmentHost = Some("x.y.z")
  }

  lazy val c = project("c", "c", new C(_))

  class C(info: ProjectInfo) extends DefaultProject(info) {
    lazy val commonsIO = "commons-io" % "commons-io" % "1.4"
  }

  lazy val d = project("d", "d", new D(_), c)

  class D(info: ProjectInfo) extends DefaultProject(info) with BNDPlugin {
    lazy val osgiCore = "org.osgi" % "org.osgi.core" % "4.2.0" % "provided"
    lazy val commonsLogging = "commons-logging" % "commons-logging-api" % "1.1"
    override def bndBundleActivator = Some("com.weiglewilczek.bnd4sbttest.d.internal.Activator")
    override def bndEmbedDependencies = true
  }
}
