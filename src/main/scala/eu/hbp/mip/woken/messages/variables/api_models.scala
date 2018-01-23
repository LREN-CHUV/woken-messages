/*
 * Copyright 2017 Human Brain Project MIP by LREN CHUV
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hbp.mip.woken.messages.variables

import eu.hbp.mip.woken.messages.RemoteMessage
import eu.hbp.mip.woken.messages.datasets.DatasetId
import io.swagger.annotations.ApiModel

trait FeatureIdentifier

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
  * @param parent Parent group, or None
  * @param pathSegment part of the path identifying this group relative to its parent
  */
@ApiModel(
  description = "Id of a group"
)
case class GroupId(
    parent: Option[GroupId],
    pathSegment: String
) extends FeatureIdentifier

case class EnumeratedValue(code: String, label: String)

// TODO: use an enumeration for the type and sqlType of a variable
/**
  * Metadata describing a variable
  *
  * @param code Code of the variable. Should be unique
  * @param label Label used when displaying the variable on the screen
  * @param `type` Type of the variable
  * @param sqlType Sql type of the variable
  * @param description Description of the variable
  * @param methodology Methodology used to acquire the variable
  * @param units Units
  * @param enumerations List of valid values for enumerations
  * @param datasets List of datasets where this variable appears
  */
case class VariableMetaData(
    code: String,
    label: String,
    `type`: String,
    sqlType: Option[String],
    description: Option[String],
    methodology: Option[String],
    units: Option[String],
    enumerations: Option[List[EnumeratedValue]],
    datasets: Set[DatasetId]
)

/**
  * Query the list of variables available for a dataset. It should return a Set of VariableId and GroupId
  *
  * @param dataset Dataset to query
  * @param includeNulls If true, include variables that contain only null values
  */
case class VariablesForDatasetQuery(dataset: DatasetId, includeNulls: Boolean) extends RemoteMessage

case class VariablesForDatasetResponse(variables: Set[FeatureIdentifier])
