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

package eu.hbp.mip.woken.messages

import eu.hbp.mip.woken.messages.datasets.DatasetsProtocol
import eu.hbp.mip.woken.messages.query.filters.QueryFiltersProtocol
import eu.hbp.mip.woken.messages.remoting.RemotingProtocol
import eu.hbp.mip.woken.messages.variables.VariablesProtocol

/**
  * Defines the messages exchanged between Woken and an external system
  */
package object query {

  object queryProtocol
      extends QueryProtocol
      with QueryFiltersProtocol
      with DatasetsProtocol
      with VariablesProtocol
      with RemotingProtocol

}
