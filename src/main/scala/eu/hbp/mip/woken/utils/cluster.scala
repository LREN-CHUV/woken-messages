package eu.hbp.mip.woken.utils

import akka.actor.{Actor, Address, ExtendedActorSystem, Extension, ExtensionKey}

class RemotePathExtension(system: ExtendedActorSystem) extends Extension {
  def getPath(actor: Actor): String =
    actor.self.path.toStringWithAddress(system.provider.getDefaultAddress)
}

object RemotePathExtension extends ExtensionKey[RemotePathExtension]

class RemoteAddressExtension(system: ExtendedActorSystem) extends Extension {
  def getAddress: Address = system.provider.getDefaultAddress
}

object RemoteAddressExtension extends ExtensionKey[RemoteAddressExtension]
