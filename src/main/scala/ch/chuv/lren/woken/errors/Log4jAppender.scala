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

import org.apache.logging.log4j.core.Filter.Result
import org.apache.logging.log4j.{ Level, LogManager }
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.{ Configuration, Property }
import org.apache.logging.log4j.core.filter.MarkerFilter
import org.apache.logging.log4j.core.LogEvent
import org.slf4j.{ Marker, MarkerFactory }

@SuppressWarnings(Array("org.wartremover.warts.Null"))
class Log4jAppender(reporter: ErrorReporter)
    extends AbstractAppender("ErrorReport", null, null, true, Property.EMPTY_ARRAY) {

  override def append(event: LogEvent): Unit =
    Option(event.getThrown).fold {
      reporter.report(
        exceptionFrom(event),
        GenericMetadata("Error", event.getLoggerName, event.getMessage.getFormattedMessage)
      )
    } { e =>
      reporter.report(
        e,
        GenericMetadata(
          "Error",
          event.getLoggerName,
          event.getMessage.getFormattedMessage
        )
      )
    }

  private def exceptionFrom(event: LogEvent): Throwable =
    if (event.getThrown != null) event.getThrown
    else new ReportErrorException(event.getMessage.getFormattedMessage.take(100))

  private val category = LogManager.ROOT_LOGGER_NAME

  def install(configuration: Configuration): Unit = {
    val filter = MarkerFilter.createFilter("SKIP_REPORTING", Result.DENY, Result.NEUTRAL)

    this.start()
    configuration.addAppender(this)
    configuration.getLoggerConfig(category).addAppender(this, Level.ERROR, filter)
  }

}

private[this] class ReportErrorException(msg: String) extends Exception(msg, null, true, false)

object Log4jAppender {

  val SKIP_REPORTING_MARKER: Marker = MarkerFactory.getMarker("SKIP_REPORTING")

  def apply(): Log4jAppender = new Log4jAppender(BugsnagErrorReporter())

}
