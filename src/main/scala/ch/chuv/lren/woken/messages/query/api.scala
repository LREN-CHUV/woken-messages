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

import java.time.OffsetDateTime

import ch.chuv.lren.woken.messages.RemoteMessage
import ch.chuv.lren.woken.messages.datasets.DatasetId
import ch.chuv.lren.woken.messages.query.filters.FilterRule
import ch.chuv.lren.woken.messages.variables.FeatureIdentifier
import spray.json.{ JsObject, JsValue }

import scala.collection.immutable.SortedSet

// This file contains the API defined by messages exchanged via Akka

/** Request the list of methods available */
case object MethodsQuery extends RemoteMessage

/** Response to MethodsQuery, lists the methods available
  *
  * @param methods Contains the list of methods serialized as a Json object
  */
case class MethodsResponse(
    methods: JsObject
)

case class TargetTable(
    tableName: Option[String],
    schema: Option[String]
)

/** A query for data mining or more complex operations on data */
sealed trait Query extends RemoteMessage {

  /** User issuing the query */
  def user: UserId

  /** List of variables ( aka dependent features ) */
  def variables: List[FeatureIdentifier]

  /** List of covariables (aka independent features ) */
  def covariables: List[FeatureIdentifier]

  /** True if all covariables must exist in the node where the computation is performed */
  def covariablesMustExist: Boolean

  /** List of groupings */
  // TODO: the concept of groupings is hazy, like a SQL group by but not only that, for example in R there are variants such as a:b and a*b
  // We may need to move away from it and be able to express full R formula (see https://stat.ethz.ch/R-manual/R-devel/library/stats/html/formula.html)
  def grouping: List[FeatureIdentifier]

  /** Filters to apply on the data. Currently, a SQL where clause */
  def filters: Option[FilterRule]

  /** Name of the target table. Defaults to the settings defined in Woken configuration */
  def targetTable: Option[TargetTable]
}

/**
  *  Data mining query executing a single algorithm
  *
  * @param user User issuing the query
  * @param variables List of variables ( aka dependent features )
  * @param covariables List of covariables (aka independent features )
  * @param grouping List of groupings
  * @param filters Filters to apply on the data. Currently, a SQL where clause
  * @param targetTable Name of the target db. Defaults to the settings defined in Woken configuration
  * @param datasets Selection of the datasets to query
  * @param algorithm Algorithm to execute, with parameters defined
  */
// Use SortedSet to simplify serialization
case class MiningQuery(
    user: UserId,
    variables: List[FeatureIdentifier],
    covariables: List[FeatureIdentifier],
    covariablesMustExist: Boolean,
    grouping: List[FeatureIdentifier],
    filters: Option[FilterRule],
    targetTable: Option[TargetTable],
    datasets: SortedSet[DatasetId],
    algorithm: AlgorithmSpec,
    executionPlan: Option[ExecutionPlan]
) extends Query

/**
  *  Experiment query using one or more algorithms on the same dataset and with an optional validation step
  *
  * @param user User issuing the query
  * @param variables List of variables ( aka dependent features )
  * @param covariables List of covariables (aka independent features )
  * @param grouping List of groupings
  * @param filters Filters to apply on the data. Currently, a SQL where clause
  * @param targetTable Name of the target db. Defaults to the settings defined in Woken configuration
  * @param trainingDatasets Set of datasets used for training
  * @param testingDatasets Set of datasets used for testing. Ignored for cross-validation methods
  * @param algorithms List of algorithms to execute, with parameters defined
  * @param validationDatasets List of datasets used for validation. Ignored for cross-validation methods
  * @param validations List of validations to apply
  * @param executionPlan Execution plan
  */
case class ExperimentQuery(
    user: UserId,
    variables: List[FeatureIdentifier],
    covariables: List[FeatureIdentifier],
    covariablesMustExist: Boolean,
    grouping: List[FeatureIdentifier],
    filters: Option[FilterRule],
    targetTable: Option[TargetTable],
    trainingDatasets: SortedSet[DatasetId],
    testingDatasets: SortedSet[DatasetId],
    algorithms: List[AlgorithmSpec],
    validationDatasets: SortedSet[DatasetId],
    validations: List[ValidationSpec],
    executionPlan: Option[ExecutionPlan]
) extends Query

/** Response to a query
  *
  * @param jobId Id of the job producing the result
  * @param node Node where the result was computed
  * @param dataProvenance Datasets used for the production of the main result (for example, training data used by the ML algorithm, ignoring test data)
  * @param feedback Feedback messages returned to the user
  * @param timestamp Date of creation of the result
  * @param `type` Shape of the result. A MIME type
  * @param algorithm Name of the algorithm that produced the result
  * @param data Contains the result serialized as a Json string, object or array.
  * The format of the result is defined by the MIME type defined in the shape field.
  * It can be for example a JSON document defining the PFA model if the shape field is 'application/pfa+json'.
  * @param error Contains the error message if the query was not successful
  * @param query The query that produced this result
  *
  */
// TODO: Query and jobId in the QueryResult should be mandatory
case class QueryResult(
    jobId: Option[String],
    node: String,
    dataProvenance: DataProvenance,
    feedback: UserFeedbacks,
    timestamp: OffsetDateTime,
    `type`: Shapes.Shape,
    algorithm: Option[String],
    data: Option[JsValue],
    error: Option[String],
    query: Option[Query]
)
