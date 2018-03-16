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

package ch.chuv.lren.woken.messages.query

import akka.serialization.Serializer
import spray.json._
import queryProtocol._

// Message serializers for Akka

class MethodsQuerySerializer extends Serializer {

  override def identifier: Int = 83561456

  override def toBinary(o: AnyRef): Array[Byte] = "".getBytes

  override def includeManifest: Boolean = false

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = MethodsQuery

}

class MethodsResponseSerializer extends Serializer {

  override def identifier: Int = 30496704

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val queryResult        = o.asInstanceOf[MethodsResponse]
    val bytes: Array[Byte] = queryResult.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[MethodsResponse].asInstanceOf[AnyRef]

}

class MiningQuerySerializer extends Serializer {

  override def identifier: Int = 20576335

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val query              = o.asInstanceOf[MiningQuery]
    val bytes: Array[Byte] = query.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[MiningQuery].asInstanceOf[AnyRef]

}

class ExperimentQuerySerializer extends Serializer {

  override def identifier: Int = 86570432

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val query              = o.asInstanceOf[ExperimentQuery]
    val bytes: Array[Byte] = query.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[ExperimentQuery].asInstanceOf[AnyRef]

}

class QueryResultSerializer extends Serializer {

  override def identifier: Int = 76561945

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val queryResult        = o.asInstanceOf[QueryResult]
    val bytes: Array[Byte] = queryResult.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[QueryResult].asInstanceOf[AnyRef]

}
