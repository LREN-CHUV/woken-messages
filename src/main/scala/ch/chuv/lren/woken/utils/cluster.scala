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

package ch.chuv.lren.woken.utils

import akka.actor.{
  Actor,
  Address,
  ExtendedActorSystem,
  Extension,
  ExtensionId,
  ExtensionIdProvider
}

class RemotePathExtensionImpl(system: ExtendedActorSystem) extends Extension {
  def getPath(actor: Actor): String =
    actor.self.path.toStringWithAddress(system.provider.getDefaultAddress)
}

object RemotePathExtension extends ExtensionId[RemotePathExtensionImpl] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): RemotePathExtensionImpl =
    new RemotePathExtensionImpl(system)

  override def lookup(): ExtensionId[_ <: Extension] = RemotePathExtension
}

class RemoteAddressExtensionImpl(system: ExtendedActorSystem) extends Extension {
  def getAddress: Address =
    system.provider.getDefaultAddress
}

object RemoteAddressExtension
    extends ExtensionId[RemoteAddressExtensionImpl]
    with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): RemoteAddressExtensionImpl =
    new RemoteAddressExtensionImpl(system)

  override def lookup(): ExtensionId[_ <: Extension] = RemoteAddressExtension
}
