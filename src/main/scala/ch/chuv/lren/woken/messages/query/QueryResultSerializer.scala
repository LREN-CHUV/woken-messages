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

package ch.chuv.lren.woken.messages.query

import akka.serialization.Serializer
import spray.json._
import queryProtocol._

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
