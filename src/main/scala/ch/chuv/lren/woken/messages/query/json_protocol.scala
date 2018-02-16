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

import java.time.{ LocalDateTime, OffsetDateTime, ZoneOffset }

import ch.chuv.lren.woken.messages.datasets.DatasetsProtocol
import ch.chuv.lren.woken.messages.query.filters.QueryFiltersProtocol
import ch.chuv.lren.woken.messages.variables.VariablesProtocol
import ch.chuv.lren.woken.utils.JsonEnums
import spray.json._

trait QueryProtocol extends DefaultJsonProtocol with JsonEnums {
  this: DatasetsProtocol with VariablesProtocol with QueryFiltersProtocol =>

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

  implicit val UserIdJsonFormat: JsonFormat[UserId] = jsonFormat1(UserId)

  implicit val AlgorithmSpecJsonFormat: JsonFormat[AlgorithmSpec] = jsonFormat2(AlgorithmSpec)

  implicit val ValidationSpecJsonFormat: JsonFormat[ValidationSpec] = jsonFormat2(ValidationSpec)

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  implicit object StepInputFormat extends JsonFormat[StepInput] {
    def write(obj: StepInput): JsValue = obj match {
      case PreviousResults(fromStep)    => JsObject("previousResults" -> JsString(fromStep))
      case SelectDataset(selectionType) => JsObject("selectDataset"   -> JsString(selectionType))
    }

    def read(json: JsValue): StepInput = json match {
      case JsObject(fields) if fields.contains("previousResults") =>
        PreviousResults(fields.getOrElse("previousResults", "?").asInstanceOf[JsString].value)
      case JsObject(fields) if fields.contains("selectDataset") =>
        SelectDataset(fields.getOrElse("selectDataset", "?").asInstanceOf[JsString].value)
      case other => deserializationError(s"Cannot deserialise StepInput object $other")
    }

  }

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  implicit object OperationFormat extends JsonFormat[Operation] {
    def write(obj: Operation): JsValue = obj match {
      case Fold              => JsString("fold")
      case Compute(stepName) => JsObject("compute" -> JsString(stepName))
    }

    def read(json: JsValue): Operation = json match {
      case JsString("fold") => Fold
      case JsObject(fields) if fields.contains("compute") =>
        Compute(fields.getOrElse("compute", "?").asInstanceOf[JsString].value)
      case other => deserializationError(s"Cannot deserialise Operation object $other")
    }

  }

  implicit val ExecutionTemplateFormat: JsonFormat[ExecutionStyle.Value] = jsonEnum(ExecutionStyle)
  implicit val ExecutionStepFormat: JsonFormat[ExecutionStep]            = jsonFormat4(ExecutionStep)

  implicit object ExecutionPlanFormat extends RootJsonFormat[ExecutionPlan] {
    private val caseClassFormat: RootJsonFormat[ExecutionPlan] = jsonFormat1(ExecutionPlan.apply)
    def write(obj: ExecutionPlan): JsValue                     = caseClassFormat.write(obj)

    def read(json: JsValue): ExecutionPlan = json match {
      case JsString("scatter-gather") => ExecutionPlan.scatterGather
      case JsString("map-reduce")     => ExecutionPlan.mapReduce
      case JsString("streaming")      => ExecutionPlan.streaming
      case js                         => caseClassFormat.read(js)
    }
  }

  implicit object MiningQueryJsonFormat extends RootJsonFormat[MiningQuery] {
    private val caseClassFormat                   = jsonFormat9(MiningQuery)
    override def write(obj: MiningQuery): JsValue = caseClassFormat.write(obj)

    override def read(json: JsValue): MiningQuery = {
      val fields = json.asJsObject.fields.filter {
        case ("filters", JsString("")) => false; case _ => true
      }
      val defaultFields = Map("datasets" -> JsArray())
      caseClassFormat.read(JsObject(fields.withDefault(defaultFields)))
    }
  }

  implicit object ExperimentQueryJsonFormat extends RootJsonFormat[ExperimentQuery] {
    private val caseClassFormat                       = jsonFormat12(ExperimentQuery)
    override def write(obj: ExperimentQuery): JsValue = caseClassFormat.write(obj)

    override def read(json: JsValue): ExperimentQuery = {
      val fields = json.asJsObject.fields.filter {
        case ("filters", JsString("")) => false; case _ => true
      }
      val defaultFields = Map(
        "trainingDatasets"   -> JsArray(),
        "testingDatasets"    -> JsArray(),
        "validationDatasets" -> JsArray()
      )
      caseClassFormat.read(JsObject(fields.withDefault(defaultFields)))
    }
  }

  implicit val QueryResultJsonFormat: RootJsonFormat[QueryResult] = jsonFormat7(QueryResult)

}
