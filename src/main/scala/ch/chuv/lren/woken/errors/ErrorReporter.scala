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

import ch.chuv.lren.woken.messages.query.QueryResult
import ch.chuv.lren.woken.messages.validation.{
  ScoringQuery,
  ScoringResult,
  ValidationQuery,
  ValidationResult
}

import scala.collection.immutable.Seq

trait ErrorReporter {
  def report(t: Throwable, meta: ErrorMetadata*): Unit
}

sealed trait ErrorMetadata

case class UserMetadata(userId: String) extends ErrorMetadata

case class QueryError(result: QueryResult) extends ErrorMetadata

case class ValidationError(validation: ValidationQuery, result: Option[ValidationResult])
    extends ErrorMetadata

case class ScoringError(scoring: ScoringQuery, result: Option[ScoringResult]) extends ErrorMetadata

case class RequestMetadata(requestId: String,
                           method: String,
                           uri: String,
                           headers: Seq[(String, String)])
    extends ErrorMetadata

case class GenericMetadata(group: String, key: String, value: String) extends ErrorMetadata
