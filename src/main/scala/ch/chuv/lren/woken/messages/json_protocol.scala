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

import spray.json._

// Get target variable's meta data
trait MessagesProtocol extends DefaultJsonProtocol {

  implicit val PingProtocol: JsonFormat[Ping] = jsonFormat1(Ping)
  implicit val PongProtocol: JsonFormat[Pong] = jsonFormat1(Pong)

  implicit val ComponentRequestProtocol: JsonFormat[ComponentQuery] = jsonFormat1(
    ComponentQuery
  )
  implicit val ComponentResponseProtocol: JsonFormat[ComponentResponse] = jsonFormat2(
    ComponentResponse
  )

  implicit val VersionRequestProtocol: JsonFormat[VersionQuery]     = jsonFormat1(VersionQuery)
  implicit val VersionResponseProtocol: JsonFormat[VersionResponse] = jsonFormat2(VersionResponse)

}
