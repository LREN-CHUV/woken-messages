package ch.chuv.lren.woken

import org.apache.logging.log4j.core.config.plugins.util.PluginManager

import scala.collection.JavaConverters._

package object errors {

  def reportErrorsToBugsnag(): Unit =
    PluginManager.addPackages(List("ch.chuv.lren.woken.errors").asJavaCollection)
}
