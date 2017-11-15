/*
 * Copyright 2017 LREN CHUV
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

package eu.hbp.mip.woken.utils

import akka.actor.{ Actor, Address, ExtendedActorSystem, Extension, ExtensionKey }

class RemotePathExtension(system: ExtendedActorSystem) extends Extension {
  def getPath(actor: Actor): String =
    actor.self.path.toStringWithAddress(system.provider.getDefaultAddress)
}

object RemotePathExtension extends ExtensionKey[RemotePathExtension]

class RemoteAddressExtension(system: ExtendedActorSystem) extends Extension {
  def getAddress: Address = system.provider.getDefaultAddress
}

object RemoteAddressExtension extends ExtensionKey[RemoteAddressExtension]
