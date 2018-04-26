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

package ch.chuv.lren.woken.messages.query.filters

import ch.chuv.lren.woken.utils.JsonEnums
import spray.json._

trait QueryFiltersProtocol extends DefaultJsonProtocol with JsonEnums {

  implicit val InputTypeJsonFormat: JsonFormat[InputType.Value] = jsonEnum(InputType)
  implicit val OperatorJsonFormat: JsonFormat[Operator.Value]   = jsonEnum(Operator)

  implicit object SingleFilterRuleJsonFormat extends JsonFormat[SingleFilterRule] {
    val caseClassFormat: JsonFormat[SingleFilterRule] = jsonFormat6(SingleFilterRule)

    override def write(obj: SingleFilterRule): JsValue = caseClassFormat.write(obj)

    override def read(json: JsValue): SingleFilterRule = json.asJsObject.fields("value") match {
      case s: JsNumber =>
        val adapted =
          json.asJsObject.fields.updated("value", JsArray(JsString(s.toString)))
        caseClassFormat.read(JsObject(adapted))
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
