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

import eu.hbp.mip.woken.messages.datasets.{DatasetId, DatasetsProtocol}
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

  implicit val VariableTypeFormat: JsonFormat[VariableType.Value] = jsonEnum(VariableType)
  implicit val SqlTypeFormat: JsonFormat[SqlType.Value] = jsonEnum(SqlType)

  import VariableType.VariableType
  import SqlType.SqlType

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
            code = code.convertTo[String],
            label = label.convertTo[String],
            `type` = t.convertTo[VariableType],
            sqlType = jsObject.fields.get("sql_type").map(_.convertTo[SqlType]),
            description = jsObject.fields.get("description").map(_.convertTo[String]),
            methodology = jsObject.fields.get("methodology").map(_.convertTo[String]),
            units = jsObject.fields.get("units").map(_.convertTo[String]),
            enumerations = jsObject.fields.get("enumerations").map(_.convertTo[List[EnumeratedValue]]),
            datasets = jsObject.fields.get("datasets").map(_.convertTo[Set[DatasetId]]).getOrElse(Set())
          )
        case _ =>
          deserializationError(
            s"Cannot deserialize VariableMetaData: invalid input. Raw input: $json"
          )
      }
    }
  }

  implicit object GroupMetaDataFormat extends RootJsonFormat[GroupMetaData] {
    case class Group(
                              code: String,
                              description: Option[String],
                              label: String,
                              groups: List[Group],
                              variables: List[VariableMetaData]
                            ) {
      def toMeta: GroupMetaData = {
        def defineParent(parent: List[PathSegment])(gm: GroupMetaData): GroupMetaData = {
          gm.copy(parent = parent, groups = gm.groups.map(defineParent(parent :+ gm.code)))
        }
        val meta = GroupMetaData(code = this.code, description = this.description, label = this.label, groups = this.groups.map(_.toMeta), variables = this.variables, parent = Nil)
        defineParent(Nil)(meta)
      }
    }

    implicit val caseClassFormat: JsonFormat[Group] = lazyFormat(jsonFormat(Group, "code", "description", "label", "groups", "variables"))

    private def toGroup(gm: GroupMetaData): Group =
      Group(code = gm.code, description = gm.description, label = gm.label, groups = gm.groups.map(toGroup), variables = gm.variables)

    override def write(gm: GroupMetaData): JsValue = {
      val group = toGroup(gm)
      caseClassFormat.write(group)
    }

    override def read(json: JsValue): GroupMetaData = {
      val defaultFields = Map(
        "variables" -> JsArray(),
        "groups" -> JsArray()
      )
      def injectDefaultFields(group: JsValue): JsObject = {
        val fields = group.asJsObject.fields
        val updated = fields.withDefault(defaultFields)
        val subGroups = fields("groups").asInstanceOf[JsArray].elements.map(injectDefaultFields)
        JsObject(updated.updated("groups", JsArray(subGroups)))
      }
      caseClassFormat.read(injectDefaultFields(json)).toMeta
    }

  }

  implicit val VariablesForDatasetQueryFormat: RootJsonFormat[VariablesForDatasetQuery] =
    jsonFormat2(
      VariablesForDatasetQuery
    )

  implicit val VariablesForDatasetResponseFormat: RootJsonFormat[VariablesForDatasetResponse] =
    jsonFormat1(VariablesForDatasetResponse)
}
