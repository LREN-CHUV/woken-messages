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

package eu.hbp.mip.woken.messages.query

import java.time.{ LocalDateTime, OffsetDateTime, ZoneOffset }

import eu.hbp.mip.woken.messages.datasets.DatasetsProtocol
import eu.hbp.mip.woken.messages.query.filters.QueryFiltersProtocol
import eu.hbp.mip.woken.messages.variables.VariablesProtocol
import eu.hbp.mip.woken.utils.JsonEnums
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
    private val caseClassFormat                   = jsonFormat8(MiningQuery)
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
    private val caseClassFormat                       = jsonFormat11(ExperimentQuery)
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
