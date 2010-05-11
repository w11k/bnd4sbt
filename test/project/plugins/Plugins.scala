import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  lazy val bnd4sbt = "com.weiglewilczek" % "bnd4sbt" % "0.3"
}
