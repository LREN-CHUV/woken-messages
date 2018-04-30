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

package ch.chuv.lren.woken.messages.datasets

import ch.chuv.lren.woken.messages.remoting.RemoteLocation
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

case class Table(name: String, defaultGroupings: Set[String])
