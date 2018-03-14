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

package ch.chuv.lren.woken.messages.variables

import ch.chuv.lren.woken.messages.datasets.{ DatasetId, DatasetsProtocol }
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

  implicit val SummaryStatisticsJsonFormat: JsonFormat[SummaryStatistics] = jsonFormat2(
    SummaryStatistics
  )
  implicit val LocationStatisticsJsonFormat: JsonFormat[LocationStatistics] = jsonFormat2(
    LocationStatistics
  )
  implicit val DispersionStatisticsJsonFormat: JsonFormat[DispersionStatistics] = jsonFormat3(
    DispersionStatistics
  )

  implicit val VariableTypeFormat: JsonFormat[VariableType.Value] = jsonEnum(VariableType)
  implicit val SqlTypeFormat: JsonFormat[SqlType.Value]           = jsonEnum(SqlType)

  import VariableType._
  import SqlType.SqlType

  implicit object VariableMetaDataFormat extends RootJsonFormat[VariableMetaData] {
    // Some fields are optional so we produce a list of options and
    // then flatten it to only write the fields that were Some(..)
    def write(item: VariableMetaData): JsObject = {
      val datasets = if (item.datasets.nonEmpty) Some(item.datasets) else None
      JsObject(
        List[Option[(String, JsValue)]](
          item.sqlType.map("sql_type"          -> _.toJson),
          item.description.map("description"   -> _.toJson),
          item.methodology.map("methodology"   -> _.toJson),
          item.units.map("units"               -> _.toJson),
          item.enumerations.map("enumerations" -> _.toJson),
          item.length.map("length"             -> _.toJson),
          item.minValue.map { m =>
            "minValue" -> (if (item.`type` == integer) JsNumber(m.toInt) else JsNumber(m))
          },
          item.minValue.map { m =>
            "maxValue" -> (if (item.`type` == integer) JsNumber(m.toInt) else JsNumber(m))
          },
          item.maxValue.map("maxValue"                   -> _.toJson),
          item.summaryStatistics.map("summaryStatistics" -> _.toJson),
          datasets.map("datasets"                        -> _.toJson),
          Some("code"                                    -> item.code.toJson),
          Some("label"                                   -> item.label.toJson),
          Some("type"                                    -> item.`type`.toJson)
        ).flatten: _*
      )
    }

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
            enumerations =
              jsObject.fields.get("enumerations").map(_.convertTo[List[EnumeratedValue]]),
            length = jsObject.fields.get("length").map(_.convertTo[Int]),
            minValue = jsObject.fields.get("minValue").map(_.convertTo[Double]),
            maxValue = jsObject.fields.get("maxValue").map(_.convertTo[Double]),
            summaryStatistics =
              jsObject.fields.get("summaryStatistics").map(_.convertTo[SummaryStatistics]),
            datasets =
              jsObject.fields.get("datasets").map(_.convertTo[Set[DatasetId]]).getOrElse(Set())
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
        def defineParent(parent: List[PathSegment])(gm: GroupMetaData): GroupMetaData =
          gm.copy(parent = parent, groups = gm.groups.map(defineParent(parent :+ gm.code)))
        val meta = GroupMetaData(code = this.code,
                                 description = this.description,
                                 label = this.label,
                                 groups = this.groups.map(_.toMeta),
                                 variables = this.variables,
                                 parent = Nil)
        defineParent(Nil)(meta)
      }
    }

    private val defaultFields = Map(
      "variables" -> JsArray(),
      "groups"    -> JsArray()
    )

    implicit object groupFormat extends RootJsonFormat[Group] {
      implicit val caseClassFormat: JsonFormat[Group] = lazyFormat(
        jsonFormat(Group, "code", "description", "label", "groups", "variables")
      )
      override def read(json: JsValue): Group =
        caseClassFormat.read(JsObject(json.asJsObject.fields.withDefault(defaultFields)))

      override def write(obj: Group): JsValue = {
        val fields: Map[String, JsValue] = Map(
          "code"  -> JsString(obj.code),
          "label" -> JsString(obj.label)
        )
        val fields2 = obj.description.fold(fields)(d => fields + ("description" -> JsString(d)))
        val fields3 =
          if (obj.groups.isEmpty)
            fields2 + ("variables" -> obj.variables.toJson)
          else if (obj.variables.isEmpty)
            fields2 + ("groups" -> JsArray(obj.groups.map(write).toVector))
          else
            fields2 + ("variables" -> obj.variables.toJson) + ("groups" -> JsArray(
              obj.groups.map(write).toVector
            ))
        JsObject(fields3)
      }
    }

    private def toGroup(gm: GroupMetaData): Group =
      Group(code = gm.code,
            description = gm.description,
            label = gm.label,
            groups = gm.groups.map(toGroup),
            variables = gm.variables)

    override def write(gm: GroupMetaData): JsValue = {
      val group = toGroup(gm)
      groupFormat.write(group)
    }

    @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
    def injectDefaultFields(group: JsValue): JsValue = {
      val fields = group.asJsObject.fields
      if (fields.contains("groups") || fields.contains("variables")) {
        val fieldsWithDefaults = fields.withDefault(defaultFields)
        val subGroups =
          fieldsWithDefaults("groups").asInstanceOf[JsArray].elements.map(injectDefaultFields)
        val variables =
          fieldsWithDefaults("variables").asInstanceOf[JsArray].elements.map(injectDefaultFields)
        val updatedFields =
          fieldsWithDefaults
            .updated("groups", JsArray(subGroups))
            .updated("variables", JsArray(variables))
        JsObject(updatedFields)
      } else group
    }

    override def read(json: JsValue): GroupMetaData =
      groupFormat.read(injectDefaultFields(json)).toMeta

  }

  implicit val VariablesForDatasetsQueryFormat: RootJsonFormat[VariablesForDatasetsQuery] =
    jsonFormat2(VariablesForDatasetsQuery)

  implicit val VariablesForDatasetsResponseFormat: RootJsonFormat[VariablesForDatasetsResponse] =
    jsonFormat1(VariablesForDatasetsResponse)
}
