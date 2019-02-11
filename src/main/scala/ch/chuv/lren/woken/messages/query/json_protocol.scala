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

import ch.chuv.lren.woken.messages.datasets.{ DatasetId, DatasetsProtocol }
import ch.chuv.lren.woken.messages.query.Shapes.Shape
import ch.chuv.lren.woken.messages.query.filters.QueryFiltersProtocol
import ch.chuv.lren.woken.messages.variables.VariablesProtocol
import ch.chuv.lren.woken.utils.JsonEnums
import spray.json._

import scala.collection.immutable.{ SortedSet, TreeSet }

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

  implicit val ExecutionStyleFormat: JsonFormat[ExecutionStyle.Value] = jsonEnum(ExecutionStyle)
  implicit val DatasetTypeFormat: JsonFormat[DatasetType.Value]       = jsonEnum(DatasetType)

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  implicit object StepInputFormat extends JsonFormat[StepInput] {
    implicit val PreviousResultsJsonFormat: JsonFormat[PreviousResults] = jsonFormat1(
      PreviousResults
    )
    implicit val SelectDatasetJsonFormat: JsonFormat[SelectDataset] = jsonFormat1(SelectDataset)

    def write(obj: StepInput): JsValue =
      JsObject((obj match {
        case p: PreviousResults => p.toJson
        case s: SelectDataset   => s.toJson
      }).asJsObject.fields + ("type" -> JsString(obj.getClass.getSimpleName)))

    def read(json: JsValue): StepInput =
      json.asJsObject.fields("type") match {
        case JsString("PreviousResults") => json.convertTo[PreviousResults]
        case JsString("SelectDataset")   => json.convertTo[SelectDataset]
        case other                       => deserializationError(s"Cannot deserialise StepInput object $other")
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

  implicit val ExecutionStepFormat: JsonFormat[ExecutionStep] = jsonFormat4(ExecutionStep)

  implicit val AlgorithmSpecJsonFormat: JsonFormat[AlgorithmSpec] = jsonFormat3(AlgorithmSpec)

  implicit val ValidationSpecJsonFormat: JsonFormat[ValidationSpec] = jsonFormat2(ValidationSpec)

  implicit object ExecutionPlanFormat extends RootJsonFormat[ExecutionPlan] {
    private val caseClassFormat: RootJsonFormat[ExecutionPlan] = jsonFormat1(ExecutionPlan.apply)
    def write(obj: ExecutionPlan): JsValue                     = caseClassFormat.write(obj)

    def read(json: JsValue): ExecutionPlan = json match {
      case JsString("scatter-gather") => ExecutionPlan.scatterGather
      case JsString("map-reduce")     => ExecutionPlan.mapReduce
      case JsString("streaming")      => ExecutionPlan.streaming
      case js: JsObject               => caseClassFormat.read(js)
      case _                          => deserializationError(s"Cannot deserialize $json into ExecutionPlan")
    }
  }

  implicit val MethodsResponseFormat: JsonFormat[MethodsResponse] = jsonFormat1(MethodsResponse)

  // Sort datasets fields
  implicit object DatasetIdOrdering extends Ordering[DatasetId] {
    override def compare(x: DatasetId, y: DatasetId): Int = x.code.compareTo(y.code)
  }

  implicit object SortedSetDatasetFormat extends RootJsonFormat[SortedSet[DatasetId]] {
    override def read(json: JsValue): SortedSet[DatasetId] =
      json.convertTo[List[DatasetId]].to[TreeSet]
    override def write(obj: SortedSet[DatasetId]): JsValue =
      obj.toList.toJson
  }

  implicit object MiningQueryJsonFormat extends RootJsonFormat[MiningQuery] {
    private val caseClassFormat                   = jsonFormat10(MiningQuery)
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
    private val caseClassFormat                       = jsonFormat13(ExperimentQuery)
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

  implicit object QueryJsonFormat extends RootJsonFormat[Query] {
    override def write(query: Query): JsValue = query match {
      case miningQuery: MiningQuery         => miningQuery.toJson
      case experimentQuery: ExperimentQuery => experimentQuery.toJson
    }

    override def read(json: JsValue): Query =
      if (json.asJsObject.fields.contains("algorithm"))
        json.convertTo[MiningQuery]
      else
        json.convertTo[ExperimentQuery]
  }

  implicit object ShapeFormat extends RootJsonFormat[Shapes.Shape] {
    override def read(json: JsValue): Shapes.Shape = json match {
      case JsString(mime) =>
        Shapes.fromString(mime).getOrElse(deserializationError(s"Unknown shape $mime"))
      case other => deserializationError(s"Cannot deserialise Shape object $other")
    }
    override def write(obj: Shapes.Shape): JsValue = JsString(obj.mime)
  }

  implicit object UserFeedbackFormat extends RootJsonFormat[UserFeedback] {
    private val caseClassInfoFormat    = jsonFormat1(UserInfo)
    private val caseClassWarningFormat = jsonFormat1(UserWarning)

    override def read(json: JsValue): UserFeedback = {
      val jsObject = json.asJsObject

      jsObject.getFields("severity") match {
        case Seq(JsString(importanceStr)) =>
          val importance = FeedbackImportance.withName(importanceStr)
          importance match {
            case FeedbackImportance.info    => caseClassInfoFormat.read(jsObject)
            case FeedbackImportance.warning => caseClassWarningFormat.read(jsObject)
          }
      }
    }

    override def write(obj: UserFeedback): JsValue = {
      val jsObj = obj match {
        case info: UserInfo       => caseClassInfoFormat.write(info).asJsObject
        case warning: UserWarning => caseClassWarningFormat.write(warning).asJsObject
      }
      jsObj.copy(fields = jsObj.fields + ("severity" -> JsString(obj.severity.toString)))
    }
  }

  implicit object QueryResultJsonFormat extends RootJsonFormat[QueryResult] {
    override def read(json: JsValue): QueryResult = {
      val jsObject = json.asJsObject

      jsObject.getFields("jobId",
                         "node",
                         "dataProvenance",
                         "feedback",
                         "timestamp",
                         "type",
                         "query") match {
        case Seq(jobId, node, datasets, feedback, timestamp, t, query) =>
          QueryResult(
            jobId = jobId.convertTo[String],
            node = node.convertTo[String],
            dataProvenance = datasets.convertTo[Set[DatasetId]],
            feedback = feedback.convertTo[List[UserFeedback]],
            timestamp = timestamp.convertTo[OffsetDateTime],
            `type` = t.convertTo[Shape],
            algorithm = jsObject.fields.get("algorithm").map(_.convertTo[String]),
            data = jsObject.fields.get("data"),
            error = jsObject.fields.get("error").map(_.convertTo[String]),
            query = query.convertTo[Query]
          )
      }
    }

    override def write(obj: QueryResult): JsValue =
      JsObject(
        List[Option[(String, JsValue)]](
          Some("jobId"                  -> obj.jobId.toJson),
          Some("node"                   -> obj.node.toJson),
          Some("dataProvenance"         -> obj.dataProvenance.toJson),
          Some("feedback"               -> obj.feedback.toJson),
          Some("timestamp"              -> obj.timestamp.toJson),
          Some("type"                   -> obj.`type`.toJson),
          obj.algorithm.map("algorithm" -> _.toJson),
          obj.data.map("data"           -> _),
          obj.error.map("error"         -> _.toJson),
          Some("query"                  -> obj.query.toJson)
        ).flatten: _*
      )

  }

}
