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

  implicit val UserIdJsonFormat: JsonFormat[UserId] = jsonFormat1(UserId)

  implicit val DatasetIdJsonFormat: JsonFormat[DatasetId] = jsonFormat1(DatasetId)

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

  implicit val FilterJsonFormat: JsonFormat[Filter]               = jsonFormat3(Filter)
  implicit val MiningQueryJsonFormat: RootJsonFormat[MiningQuery] = jsonFormat7(MiningQuery)
  implicit val ExperimentQueryJsonFormat: RootJsonFormat[ExperimentQuery] = jsonFormat11(
    ExperimentQuery
  )

  implicit val QueryResultJsonFormat: RootJsonFormat[QueryResult] = jsonFormat7(QueryResult)

}
