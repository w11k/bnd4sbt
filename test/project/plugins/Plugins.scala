import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  lazy val aquteRepo = "aQute Maven Repository" at "http://www.aqute.biz/repo"
  lazy val bnd4sbt = "com.weiglewilczek" % "bnd4sbt" % "0.4"
}
