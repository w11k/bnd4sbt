import sbt._

class Project(info: ProjectInfo) extends PluginProject(info) {

  // Compiler options
  override def compileOptions = Unchecked :: Nil

  // Repositories
  def aquteRepo = "aQute Maven Repository" at "http://www.aqute.biz/repo"
  lazy val aquteModuleConfig = ModuleConfiguration("biz.aQute", aquteRepo)

  // Dependencies
  lazy val bnd     = "biz.aQute"               % "bndlib"      % "0.0.384"
  lazy val specs   = "org.scala-tools.testing" % "specs"       % "1.6.2.1" % "test"
  lazy val mockito = "org.mockito"             % "mockito-all" % "1.8.4"   % "test"

  // Publishing
  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2" / ".credentials" / ".scala-tools.org", log)
}
