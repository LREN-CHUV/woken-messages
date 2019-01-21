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
import org.apache.logging.log4j.core.config.{ AppenderRef, Configuration, LoggerConfig }
import org.apache.logging.log4j.core.filter.LevelRangeFilter
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.{ Filter, LogEvent, LoggerContext }
import Log4jAppender._

class Log4jAppender(reporter: ErrorReporter, layout: PatternLayout)
    extends AbstractAppender("ErrorReport", filter, layout) {
  override def append(event: LogEvent): Unit =
    Option(event.getThrown).fold {
      reporter.report(
        new Exception("No exeception"),
        GenericMetadata("Error", event.getLoggerFqcn, event.getMessage.getFormattedMessage)
      )
    } { e =>
      reporter.report(
        e,
        GenericMetadata(
          "Error",
          event.getLoggerFqcn,
          event.getMessage.getFormattedMessage
        )
      )
    }
}

object Log4jAppender {

  private val filter: Filter =
    LevelRangeFilter.createFilter(Level.ERROR, Level.FATAL, Result.ACCEPT, Result.DENY)

  def alsoReportErrorsTo(reporter: ErrorReporter): Unit = {
    val ctx: LoggerContext    = LogManager.getContext(false).asInstanceOf[LoggerContext]
    val config: Configuration = ctx.getConfiguration
    val layout: PatternLayout = PatternLayout
      .newBuilder()
      .withPattern(PatternLayout.SIMPLE_CONVERSION_PATTERN)
      .withConfiguration(config)
      .build()
    val appender: Log4jAppender = new Log4jAppender(reporter, layout)
    appender.start()
    config.addAppender(appender)
    val ref: AppenderRef                 = AppenderRef.createAppenderRef("File", null, null)
    val appenderRefs: Array[AppenderRef] = Array[AppenderRef](ref)
    val loggerConfig: LoggerConfig = LoggerConfig.createLogger(false,
                                                               Level.INFO,
                                                               "org.apache.logging.log4j",
                                                               "true",
                                                               appenderRefs,
                                                               null,
                                                               config,
                                                               null)
    loggerConfig.addAppender(appender, null, null)
    config.addLogger("org.apache.logging.log4j", loggerConfig)

  }

}
