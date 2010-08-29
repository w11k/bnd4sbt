import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

  // ===================================================================================================================
  // Repositories
  // ===================================================================================================================

  object Repositories {
    lazy val aquteRepo = "aQute Maven Repository" at "http://www.aqute.biz/repo"
  }

  // ===================================================================================================================
  // ModuleConfigurations
  // ===================================================================================================================

  lazy val aquteModuleConfig = ModuleConfiguration("biz.aQute", Repositories.aquteRepo)

  // ===================================================================================================================
  // Dependencies
  // ===================================================================================================================

  lazy val bnd4sbt = "com.weiglewilczek.bnd4sbt" % "bnd4sbt" % "1.0.0.RC7"
}
