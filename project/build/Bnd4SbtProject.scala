import sbt._

class Bnd4SbtProject(info: ProjectInfo) extends PluginProject(info) {

  // Module configurations
//  val aquteModuleConfig = ModuleConfiguration("biz.aQute", "aQute Maven Repository" at "http://www.aqute.biz/repo")

  // Dependencies (compile)
  val bnd = "biz.aQute" % "bndlib" % "1.43.0"

  // Dependencies (test)
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"
  val mockito = "org.mockito" % "mockito-all" % "1.8.4" % "test"

  // Publishing
  override def managedStyle = ManagedStyle.Maven
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
//  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
//  val publishTo = Resolver.file("Local Test Repository", Path fileProperty "java.io.tmpdir" asFile)
}
