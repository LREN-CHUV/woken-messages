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

package eu.hbp.mip.woken.messages.datasets

import eu.hbp.mip.woken.messages.RemoteMessage
import eu.hbp.mip.woken.messages.remoting.RemoteLocation
import io.swagger.annotations.ApiModel

/**
  * Id of a dataset
  *
  * @param code Unique dataset code
  */
@ApiModel(
  description = "Id of a dataset"
)
case class DatasetId(
    code: String
)

object AnonymisationLevel extends Enumeration {
  type AnonymisationLevel = Value
  val Identifying, Depersonalised, Anonymised = Value
}

import AnonymisationLevel.AnonymisationLevel

case class Dataset(dataset: DatasetId,
                   label: String,
                   description: String,
                   tables: List[String],
                   anonymisationLevel: AnonymisationLevel,
                   location: Option[RemoteLocation]) {

  def withoutAuthenticationDetails: Dataset =
    copy(location = location.map(_.copy(credentials = None)))

}

/**
  * Should return a list of Dataset
  */
object DatasetsQuery extends RemoteMessage

case class DatasetsResponse(datasets: Set[Dataset])
