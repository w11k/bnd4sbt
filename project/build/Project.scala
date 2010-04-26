import sbt._

class Bnd4SbtPluginProject(info: ProjectInfo) extends PluginProject(info) {

  lazy val aquteRepo = "aQute Repository" at "http://www.aqute.biz/repo"

  lazy val bnd = "biz.aQute" % "bndlib" % "0.0.384"
}
