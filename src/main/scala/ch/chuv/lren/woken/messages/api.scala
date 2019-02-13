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

package ch.chuv.lren.woken.messages

// This file contains the API defined by messages exchanged via Akka

/**
  * Check liveliness of our services.
  *
  * @param role If defined, restricts the servers that can answer to those whose role match the given role
  */
case class Ping(role: Option[String]) extends RemoteMessage

case class Pong(role: Set[String])

/**
  * Request the list of components available
  *
  * @param detailed If true, the list may include sub-components
  */
case class ComponentQuery(detailed: Boolean) extends RemoteMessage

case class ComponentResponse(role: Set[String], components: Set[String])

/**
  * Request the version of a component of the software
  *
  * @param component The component requested
  */
case class VersionQuery(component: String) extends RemoteMessage

case class VersionResponse(component: String, version: String)
