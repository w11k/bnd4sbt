import sbt._

class Project(info: ProjectInfo) extends PluginProject(info) {

  // Module configurations
  def aquteRepo = "aQute Maven Repository" at "http://www.aqute.biz/repo"
  val aquteModuleConfig = ModuleConfiguration("biz.aQute", aquteRepo)

  // Dependencies (compile)
  val bnd = "biz.aQute" % "bndlib" % "0.0.384"

  // Dependencies (test)
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"
  val mockito = "org.mockito" % "mockito-all" % "1.8.4" % "test"

  // Publishing
  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
}
