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

package eu.hbp.mip.woken.messages.external

import java.time.{ LocalDateTime, OffsetDateTime, ZoneOffset }

import spray.json._

object ExternalAPIProtocol extends DefaultJsonProtocol {

  implicit object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {
    override def write(x: OffsetDateTime): JsNumber = {
      require(x ne null)
      JsNumber(x.toEpochSecond)
    }
    override def read(value: JsValue): OffsetDateTime = value match {
      case JsNumber(x) =>
        OffsetDateTime.of(LocalDateTime.ofEpochSecond(x.toLong, 0, ZoneOffset.UTC), ZoneOffset.UTC)
      case unknown =>
        deserializationError(s"Expected OffsetDateTime as JsNumber, but got $unknown")
    }
  }

  implicit val CodeValueJsonFormat: JsonFormat[CodeValue] = jsonFormat2(CodeValue.apply)

  implicit val VariableIdJsonFormat: JsonFormat[VariableId] = jsonFormat1(VariableId)

  implicit val AlgorithmSpecJsonFormat: JsonFormat[AlgorithmSpec] = jsonFormat2(AlgorithmSpec)

  implicit val ValidationSpecJsonFormat: JsonFormat[ValidationSpec] = jsonFormat2(ValidationSpec)

  def jsonEnum[T <: Enumeration](enu: T): JsonFormat[T#Value] = new JsonFormat[T#Value] {
    def write(obj: T#Value) = JsString(obj.toString)

    def read(json: JsValue): enu.Value = json match {
      case JsString(txt) => enu.withName(txt)
      case something =>
        deserializationError(s"Expected a value from enum $enu instead of $something")
    }
  }

  implicit val OperatorsJsonFormat: JsonFormat[Operators.Value] = jsonEnum(Operators)

  implicit val FilterJsonFormat: JsonFormat[Filter]               = jsonFormat3(Filter)
  implicit val SimpleQueryJsonFormat: RootJsonFormat[MiningQuery] = jsonFormat5(MiningQuery)
  implicit val ExperimentQueryJsonFormat: RootJsonFormat[ExperimentQuery] = jsonFormat6(
    ExperimentQuery
  )

  implicit val QueryResultJsonFormat: JsonFormat[QueryResult] = jsonFormat7(QueryResult)

}
