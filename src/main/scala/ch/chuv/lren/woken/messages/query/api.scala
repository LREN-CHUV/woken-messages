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
import spray.json.{JsObject, JsValue}

// This file contains the API for messages exchanged via Akka

/** Request the list of methods available */
case object MethodsQuery extends RemoteMessage

/** Response to MethodsQuery, lists the methods available
  *
  * @param methods Contains the list of methods serialized as a Json object
  */
case class MethodsResponse(
    methods: JsObject
)

/** A query for data mining or more complex operations on data */
sealed trait Query extends RemoteMessage {

  /** User issuing the query */
  def user: UserId

  /** List of variables ( aka dependent features ) */
  def variables: List[FeatureIdentifier]

  /** List of covariables (aka independent features ) */
  def covariables: List[FeatureIdentifier]

  /** List of groupings */
  // TODO: the concept of groupings is hazy, like a SQL group by but not only that, for example in R there are variants such as a:b and a*b
  // We may need to move away from it and be able to express full R formula (see https://stat.ethz.ch/R-manual/R-devel/library/stats/html/formula.html)
  def grouping: List[FeatureIdentifier]

  /** Filters to apply on the data. Currently, a SQL where clause */
  def filters: Option[FilterRule]

  /** Name of the target table. Defaults to the settings defined in Woken configuration */
  def targetTable: Option[String]
}

/**
  *  Data mining query executing a single algorithm
  *
  * @param user User issuing the query
  * @param variables List of variables ( aka dependent features )
  * @param covariables List of covariables (aka independent features )
  * @param grouping List of groupings
  * @param filters Filters to apply on the data. Currently, a SQL where clause
  * @param targetTable Name of the target table. Defaults to the settings defined in Woken configuration
  * @param datasets Selection of the datasets to query
  * @param algorithm Algorithm to execute, with parameters defined
  */
case class MiningQuery(
    user: UserId,
    variables: List[FeatureIdentifier],
    covariables: List[FeatureIdentifier],
    grouping: List[FeatureIdentifier],
    filters: Option[FilterRule],
    targetTable: Option[String],
    datasets: Set[DatasetId],
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
  * @param targetTable Name of the target table. Defaults to the settings defined in Woken configuration
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
    grouping: List[FeatureIdentifier],
    filters: Option[FilterRule],
    targetTable: Option[String],
    trainingDatasets: Set[DatasetId],
    testingDatasets: Set[DatasetId],
    algorithms: List[AlgorithmSpec],
    validationDatasets: Set[DatasetId],
    validations: List[ValidationSpec],
    executionPlan: Option[ExecutionPlan]
) extends Query

/** Response to a query
  *
  * @param jobId Id of the job producing the result
  * @param node Node where the result was computed
  * @param timestamp Date of creation of the result
  * @param shape Shape of the result. A MIME type
  * @param algorithm Name of the algorithm that produced the result
  * @param data Contains the result serialized as a Json string, object or array.
  * The format of the result is defined by the MIME type defined in the shape field.
  * It can be for example a JSON document defining the PFA model if the shape field is 'application/pfa+json'.
  * @param error Contains the error message if the query was not successful
  */
case class QueryResult(
    jobId: String,
    node: String,
    timestamp: OffsetDateTime,
    shape: String,
    algorithm: String,
    data: Option[JsValue],
    error: Option[String]
)

/*

  Filter:
    type: object
    description: A filter in a query
    properties:
      variable:
        description: |
          Variable used to filter, only code value is sent
        '$ref': '#/definitions/VariableId'
      operator:
        description: |
          Filter operator : eq, lt, gt, gte, lte, neq, in, notin, between.
        type: string
        enum:
          - eq
          - lt
          - gt
          - gte
          - lte
          - neq
          - in
          - notin
          - between
      values:
        description: |
          List of values used to filter.
          With operators “eq”, “lt”, “gt”, “gte”, “lte”, ”neq”, the filter mode “OR” is used.
          With operator “between”, only two values are sent, they represents the range limits.
        type: array
        items:
          type: string

  Query:
    type: object
    description: |
      A query object represents a request to the CHUV API.
      This object contains all information required by the API to compute a result (dataset) and return it.
    properties:
      variables:
        description: |
          List of variables used by the request, only code values are sent
        type: array
        items:
          $ref: '#/definitions/VariableId'
      covariables:
        description: |
          List of covariables used by the request, only code values are sent.
          These variables are returned in dataset object header.
        type: array
        items:
          $ref: '#/definitions/VariableId'
      grouping:
        description: |
          List of grouping variables used by the request, only code values are sent.
        type: array
        items:
          $ref: '#/definitions/VariableId'
      filters:
        description: |
          List of filters objects used by the request.
        type: array
        items:
          $ref: '#/definitions/Filter'
      request:
        description: Plot type
        type: string

  Variable:
    type: object
    description: A variable object represents a business variable. All variable information should be stored in this object.
    properties:
      code:
        type: string
        description: |
          Unique variable code, used to request
      label:
        type: string
        description: |
          Variable label, used to display
      group:
        description: |
          Variable group (only the variable path)
        '$ref': '#/definitions/Group'
      type:
        type: string
        description: |
          I: Integer, T: Text, N: Decimal, D: Date, B: Boolean.
        enum:
          - I # Integer
          - T # Text
          - N # Decimal
          - D # Date
          - B # Boolean
      length:
        type: integer
        description: |
          For text, number of characters of value
      minValue:
        type: number
        description: |
          Minimum allowed value (for integer or numeric)
      maxValue:
        type: number
        description: |
          Maximum allowed value (for integer or numeric)
      units:
        type: string
        description: Variable unit
      isVariable:
        type: boolean
        description: Can the variable can be used as a variable
      isGrouping:
        type: boolean
        description: Can the variable can be used as a group
      isCovariable:
        type: boolean
        description: Can the variable can be used as a covariable
      isFilter:
        type: boolean
        description: Can the variable can be used as a filter
      values:
        description: |
          List of variable values (if is an enumeration variable).
        type: array
        items:
          $ref: '#/definitions/Value'
  Value:
    type: object
    description: A value object is a business variable value. All value information should be stored in this object.
    properties:
      code:
        type: string
        description: |
          Unique code of value (for variable), used to request
      label:
        type: string
        description: |
          Label of value, used to display

 */
