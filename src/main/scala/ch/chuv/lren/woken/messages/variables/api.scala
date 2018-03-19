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

/**
  * Query the list of variables available for a dataset. It should return a Set of VariableId and GroupId
  *
  * @param datasets Set of datasets to query. If empty, all datasets available are selected
  * @param exhaustive If true, included variables must be present in all datasets selected
  */
case class VariablesForDatasetsQuery(datasets: Set[DatasetId], exhaustive: Boolean)
    extends RemoteMessage

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
case class VariablesForDatasetsResponse(variables: Set[VariableMetaData],
                                        error: Option[String] = None)
