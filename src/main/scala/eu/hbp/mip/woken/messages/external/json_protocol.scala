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

  implicit val CodeValueJsonFormat: JsonFormat[CodeValue] = jsonFormat2(CodeValue.apply)

  implicit val VariableIdJsonFormat: JsonFormat[VariableId] = jsonFormat1(VariableId)

  implicit val AlgorithmSpecJsonFormat: JsonFormat[AlgorithmSpec] = jsonFormat2(AlgorithmSpec.apply)

  implicit val AlgorithmJsonFormat: JsonFormat[Algorithm] = jsonFormat3(Algorithm.apply)

  implicit val ValidationSpecJsonFormat: JsonFormat[ValidationSpec] = jsonFormat2(
    ValidationSpec.apply
  )

  implicit val ValidationJsonFormat: JsonFormat[Validation] = jsonFormat3(Validation.apply)

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
