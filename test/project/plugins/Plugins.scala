import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  // ===================================================================================================================
  // ModuleConfigurations
  // ===================================================================================================================

  lazy val aquteModuleConfig =
    ModuleConfiguration("biz.aQute", "aQute Maven Repository" at "http://www.aqute.biz/repo")

  // ===================================================================================================================
  // Dependencies
  // ===================================================================================================================

  lazy val bnd4sbt = "com.weiglewilczek.bnd4sbt" % "bnd4sbt" % "1.0.1"
}
