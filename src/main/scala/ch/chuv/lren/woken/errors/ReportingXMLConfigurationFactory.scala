/*
 * Copyright (C) 2017  LREN CHUV for Human Brain Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.chuv.lren.woken.errors

import org.apache.logging.log4j.core.config.{ ConfigurationSource, Order }
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.xml.{ XmlConfiguration, XmlConfigurationFactory }
import org.apache.logging.log4j.core.LoggerContext

/**
  * Initialise by adding the following line at the beginning of your program:
  *
  * errors.reportErrorsToBugsnag()
  *
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
