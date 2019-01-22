package ch.chuv.lren.woken.errors

import org.apache.logging.log4j.core.config.{ ConfigurationSource, Order }
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.xml.{ XmlConfiguration, XmlConfigurationFactory }
import org.apache.logging.log4j.core.LoggerContext

/**
  * Initialise by adding the following line at the beginning of your program:
  *
  * PluginManager.addPackages(JavaConverters.asJavaCollection(Array("ch.chuv.lren.woken.errors")))
  */
@Plugin(name = "ReportingXMLConfigurationFactory", category = "ConfigurationFactory")
@Order(10)
class ReportingXMLConfigurationFactory extends XmlConfigurationFactory {

  /**
    * Valid file extensions for XML files.
    */
  val SUFFIXES: Array[String] = Array[String](".xml", "*")

  /**
    * Return the Configuration.
    *
    * @param source The InputSource.
    * @return The Configuration.
    */
  override def getConfiguration(loggerContext: LoggerContext, source: ConfigurationSource) =
    new ReportingXMLConfiguration(loggerContext, source)

  /**
    * Returns the file suffixes for XML files.
    *
    * @return An array of File extensions.
    */
  override def getSupportedTypes: Array[String] = SUFFIXES
}

class ReportingXMLConfiguration(val loggerContext: LoggerContext,
                                val configSource: ConfigurationSource)
    extends XmlConfiguration(loggerContext, configSource) {
  override protected def doConfigure(): Unit = {
    super.doConfigure()
    println("Add error reporter appender for log4j")
    val appender = Log4jAppender()
    appender.install(this)
  }
}
