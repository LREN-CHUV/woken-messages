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

import spray.json._

object ExternalAPIProtocol extends DefaultJsonProtocol {

  case class CodeValue(code: String, value: String) {
    def toTuple: (String, String) = (code, value)
  }
  object CodeValue {
    def fromTuple(t: (String, String)) = CodeValue(t._1, t._2)
  }

  implicit val CodeValueJsonFormat: JsonFormat[CodeValue] = jsonFormat2(CodeValue.apply)

  implicit val VariableIdJsonFormat: JsonFormat[VariableId] = jsonFormat1(VariableId)

  implicit object AlgorithmJsonFormat extends JsonFormat[Algorithm] {
    def write(a: Algorithm): JsValue =
      JsObject(
        List[Option[(String, JsValue)]](
          Some("code"       -> JsString(a.code)),
          a.name.map("name" -> JsString(_)),
          Some("parameters" -> a.parameters.map(CodeValue.fromTuple).toJson)
        ).flatten: _*
      )

    def read(value: JsValue): Algorithm = {
      val parameters = value.asJsObject
        .fields("parameters")
        .convertTo[List[CodeValue]]
        .map(_.toTuple)
        .toMap
      Algorithm(value.asJsObject.fields("code").convertTo[String],
                value.asJsObject.fields.get("name").map(_.convertTo[String]),
                parameters)
    }
  }

  implicit object ValidationJsonFormat extends JsonFormat[Validation] {
    def write(v: Validation): JsValue =
      JsObject(
        List[Option[(String, JsValue)]](
          Some("code"       -> JsString(v.code)),
          v.name.map("name" -> JsString(_)),
          Some("parameters" -> v.parameters.map(CodeValue.fromTuple).toJson)
        ).flatten: _*
      )

    def read(value: JsValue): Validation = {
      val parameters = value.asJsObject
        .fields("parameters")
        .convertTo[List[CodeValue]]
        .map(_.toTuple)
        .toMap
      Validation(value.asJsObject.fields("code").convertTo[String],
                 value.asJsObject.fields.get("name").map(_.convertTo[String]),
                 parameters)
    }
  }

  def jsonEnum[T <: Enumeration](enu: T): JsonFormat[T#Value] = new JsonFormat[T#Value] {
    def write(obj: T#Value) = JsString(obj.toString)

    def read(json: JsValue): enu.Value = json match {
      case JsString(txt) => enu.withName(txt)
      case something =>
        deserializationError(s"Expected a value from enum $enu instead of $something")
    }
  }

  implicit val OperatorsJsonFormat: JsonFormat[Operators.Value] = jsonEnum(Operators)

}
