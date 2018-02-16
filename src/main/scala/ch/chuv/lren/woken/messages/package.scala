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

package ch.chuv.lren.woken

import ch.chuv.lren.woken.messages.datasets.DatasetsProtocol
import ch.chuv.lren.woken.messages.query.QueryProtocol
import ch.chuv.lren.woken.messages.query.filters.QueryFiltersProtocol
import ch.chuv.lren.woken.messages.remoting.RemotingProtocol
import ch.chuv.lren.woken.messages.variables.VariablesProtocol

/**
  * Defines messages exchanged between Woken and other systems (portal,...)
  *
  * Also defines messages exchanged between Woken and its other modules, e.g. Woken-validation
  */
package object messages {

  object APIJsonProtocol
      extends QueryProtocol
      with QueryFiltersProtocol
      with DatasetsProtocol
      with VariablesProtocol
      with RemotingProtocol
}
