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

package ch.chuv.lren.woken

import eu.hbp.mip.woken.messages.datasets.DatasetsProtocol
import eu.hbp.mip.woken.messages.query.QueryProtocol
import eu.hbp.mip.woken.messages.query.filters.QueryFiltersProtocol
import eu.hbp.mip.woken.messages.remoting.RemotingProtocol
import eu.hbp.mip.woken.messages.variables.VariablesProtocol

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
