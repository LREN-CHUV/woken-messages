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

package eu.hbp.mip.woken.messages.variables

import eu.hbp.mip.woken.messages.datasets.{ DatasetId, DatasetsProtocol }
import spray.json._

// Get target variable's meta data
trait VariablesProtocol extends DefaultJsonProtocol {
  this: DatasetsProtocol =>

  implicit val VariableIdJsonFormat: JsonFormat[VariableId] = jsonFormat1(VariableId)

  implicit val GroupIdJsonFormat: JsonFormat[GroupId] = jsonFormat1(GroupId)

  implicit object FeatureIdentifierJsonFormat extends JsonFormat[FeatureIdentifier] {
    override def read(json: JsValue): FeatureIdentifier =
      if (json.asJsObject.fields.contains("code"))
        VariableIdJsonFormat.read(json)
      else
        GroupIdJsonFormat.read(json)

    override def write(obj: FeatureIdentifier): JsValue = obj match {
      case v: VariableId => VariableIdJsonFormat.write(v)
      case g: GroupId    => GroupIdJsonFormat.write(g)
    }
  }

  implicit object EnumeratedValueFormat extends RootJsonFormat[EnumeratedValue] {
    private val numPattern = "([0-9]+)".r

    private def numbersHaveNoQuotes(v: String): JsValue = v match {
      case numPattern(n) => JsNumber(n.toInt)
      case _             => JsString(v)
    }

    def write(item: EnumeratedValue): JsObject =
      JsObject(
        "code"  -> numbersHaveNoQuotes(item.code),
        "label" -> numbersHaveNoQuotes(item.label)
      )

    def read(json: JsValue): EnumeratedValue = {
      val jsObject = json.asJsObject
      jsObject.getFields("code", "label") match {
        case Seq(JsString(code), JsString(label)) => EnumeratedValue(code, label)
        case Seq(code, label)                     => EnumeratedValue(code.toString, label.toString)
        case _ =>
          deserializationError(
            s"Cannot deserialize EnumeratedValue: invalid input. Raw input: $json"
          )
      }

    }
  }

  implicit object VariableMetaDataFormat extends RootJsonFormat[VariableMetaData] {
    // Some fields are optional so we produce a list of options and
    // then flatten it to only write the fields that were Some(..)
    def write(item: VariableMetaData): JsObject =
      JsObject(
        List[Option[(String, JsValue)]](
          item.sqlType.map("sql_type"          -> _.toJson),
          item.description.map("description"   -> _.toJson),
          item.methodology.map("methodology"   -> _.toJson),
          item.units.map("units"               -> _.toJson),
          item.enumerations.map("enumerations" -> _.toJson),
          Some("code"                          -> item.code.toJson),
          Some("label"                         -> item.label.toJson),
          Some("type"                          -> item.`type`.toJson),
          Some("datasets"                      -> item.datasets.toJson)
        ).flatten: _*
      )

    // We use the "standard" getFields method to extract the mandatory fields.
    // For optional fields we extract them directly from the fields map using get,
    // which already handles the option wrapping for us so all we have to do is map the option
    def read(json: JsValue): VariableMetaData = {
      val jsObject = json.asJsObject

      jsObject.getFields("code", "label", "type") match {
        case Seq(code, label, t) =>
          VariableMetaData(
            code.convertTo[String],
            label.convertTo[String],
            t.convertTo[String],
            jsObject.fields.get("sql_type").map(_.convertTo[String]),
            jsObject.fields.get("description").map(_.convertTo[String]),
            jsObject.fields.get("methodology").map(_.convertTo[String]),
            jsObject.fields.get("units").map(_.convertTo[String]),
            jsObject.fields.get("enumerations").map(_.convertTo[List[EnumeratedValue]]),
            jsObject.fields.get("datasets").map(_.convertTo[Set[DatasetId]]).getOrElse(Set())
          )
        case _ =>
          deserializationError(
            s"Cannot deserialize VariableMetaData: invalid input. Raw input: $json"
          )
      }
    }
  }

  implicit val VariablesForDatasetQueryFormat: RootJsonFormat[VariablesForDatasetQuery] =
    jsonFormat2(
      VariablesForDatasetQuery
    )

  implicit val VariablesForDatasetResponseFormat: RootJsonFormat[VariablesForDatasetResponse] =
    jsonFormat1(VariablesForDatasetResponse)
}
