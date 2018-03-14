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

package ch.chuv.lren.woken.messages.variables

import ch.chuv.lren.woken.messages.RemoteMessage
import ch.chuv.lren.woken.messages.datasets.DatasetId
import io.swagger.annotations.ApiModel

sealed trait FeatureIdentifier

/**
  * Id of a variable
  *
  * @param code Unique variable code, used to request
  */
@ApiModel(
  description = "Id of a variable"
)
case class VariableId(
    code: String
) extends FeatureIdentifier

/**
  * Id of a group
  *
  * @param path part of the path identifying this group relative to its parent
  */
@ApiModel(
  description = "Id of a group"
)
case class GroupId(path: List[PathSegment]) extends FeatureIdentifier {
  def parent: Option[GroupId] =
    if (path.lengthCompare(1) > 0)
      Some(GroupId(path.take(path.size - 1)))
    else
      None
}

case class EnumeratedValue(code: String, label: String)

object VariableType extends Enumeration {
  type VariableType = Value

  /** Integer value */
  val integer: Value = Value("integer")

  /** Real value */
  val real: Value = Value("real")

  /** Boolean value (true, false) */
  val boolean: Value = Value("boolean")

  /** Date value, */
  val date: Value = Value("date")

  /** A value selected from a list (enumeration) */
  val polynominal: Value = Value("polynominal")

  /** A value with one state or another */
  val binominal: Value = Value("binominal")

  /** Text value */
  val text: Value = Value("text")

  /** An image */
  val image: Value = Value("image")
}

object SqlType extends Enumeration {
  type SqlType = Value

  /** Integer value */
  val int: Value = Value("int")

  /** Numeric (decimal) value */
  val numeric: Value = Value("numeric")

  /** Char value, can be a fixed length string */
  val char: Value = Value("char")

  /** Varchar value, for variable length string */
  val varchar: Value = Value("varchar")
}

import VariableType.VariableType
import SqlType.SqlType

/**
  * Metadata describing a variable
  *
  * @param code Code of the variable. Should be unique
  * @param label Label used when displaying the variable on the screen
  * @param `type` Type of the variable
  * @param sqlType Sql type of the variable
  * @param methodology Methodology used to acquire the variable
  * @param description Description of the variable
  * @param units Units
  * @param enumerations List of valid values for enumerations
  * @param length Maximum length for textual variables
  * @param minValue Minimum value for numerical variables
  * @param maxValue Maximum value for numerical variables
  * @param datasets List of datasets where this variable appears
  */
case class VariableMetaData(
    code: String,
    label: String,
    `type`: VariableType,
    sqlType: Option[SqlType],
    methodology: Option[String],
    description: Option[String],
    units: Option[String],
    enumerations: Option[List[EnumeratedValue]],
    length: Option[Int],
    minValue: Option[Double],
    maxValue: Option[Double],
    datasets: Set[DatasetId]
) {
  def toId: VariableId = VariableId(code)
}

/**
  * Metadata describing a group
  *
  * @param code Identifier for the group
  * @param description Human readable description of the group
  * @param label Label used to display the group in the user interface
  * @param groups List of sub-groups
  * @param variables List of variables contained in the group
  */
case class GroupMetaData(
    code: String,
    description: Option[String],
    label: String,
    parent: List[PathSegment],
    groups: List[GroupMetaData],
    variables: List[VariableMetaData]
) {
  def toId: GroupId = GroupId(parent :+ code)
}

/**
  * Query the list of variables available for a dataset. It should return a Set of VariableId and GroupId
  *
  * @param datasets Set of datasets to query. If empty, all datasets available are selected
  * @param includeNulls If true, include variables that contain only null values in all datasets selected
  */
case class VariablesForDatasetsQuery(datasets: Set[DatasetId], includeNulls: Boolean)
    extends RemoteMessage

case class VariablesForDatasetsResponse(variables: Set[VariableMetaData])
