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

package ch.chuv.lren.woken.messages.query.filters

import eu.hbp.mip.woken.utils.JsonEnums
import spray.json._

trait QueryFiltersProtocol extends DefaultJsonProtocol with JsonEnums {

  implicit val InputTypeJsonFormat: JsonFormat[InputType.Value] = jsonEnum(InputType)
  implicit val OperatorJsonFormat: JsonFormat[Operator.Value]   = jsonEnum(Operator)

  implicit object SingleFilterRuleJsonFormat extends JsonFormat[SingleFilterRule] {
    val caseClassFormat: JsonFormat[SingleFilterRule] = jsonFormat6(SingleFilterRule)

    override def write(obj: SingleFilterRule): JsValue = caseClassFormat.write(obj)

    override def read(json: JsValue): SingleFilterRule = json.asJsObject.fields("value") match {
      case s: JsString =>
        val adapted = json.asJsObject.fields.updated("value", JsArray(s))
        caseClassFormat.read(JsObject(adapted))
      case _ => caseClassFormat.read(json)
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  implicit object FilterRuleJsonFormat extends RootJsonFormat[FilterRule] {
    override def write(obj: FilterRule): JsValue = obj match {
      case s: SingleFilterRule => s.toJson
      case c: CompoundFilterRule =>
        JsObject(
          "condition" -> JsString(c.condition.toString),
          "rules"     -> JsArray(c.rules.map(r => write(r)).toVector)
        )
    }

    override def read(json: JsValue): FilterRule = json match {
      case JsObject(fields) if fields contains "id" =>
        json.convertTo[SingleFilterRule]
      case JsObject(fields) if fields contains "rules" =>
        val JsString(condition) = fields("condition")
        CompoundFilterRule(
          Condition.withName(condition),
          fields("rules").asInstanceOf[JsArray].elements.map(r => read(r)).toList
        )
    }
  }

}
