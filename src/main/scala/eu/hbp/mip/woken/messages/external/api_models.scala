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

import java.time.OffsetDateTime

import eu.hbp.mip.woken.messages.RemoteMessage
import io.swagger.annotations.ApiModel

/** An algorithm */
@ApiModel(
  description = "An algorithm to execute"
)
case class Algorithm(
    /** Code identifying the algorithm */
    code: String,
    /** Optional readable name for the algorithm */
    name: Option[String],
    /** List of parameters to pass to the algorithm */
    parameters: Map[String, String] = Map.empty
)

/** Id of a variable */
@ApiModel(
  description = "Id of a variable"
)
case class VariableId(
    /** Unique variable code, used to request */
    code: String
)

/** Definition of a validation method used in an experiment */
case class Validation(
    /** Code identifying the validation */
    code: String,
    /** Optional readable name for the validation */
    name: Option[String],
    /** List of parameters to pass to the validation */
    parameters: Map[String, String]
)

/** List of operations supported by a filter */
object Operators extends Enumeration {
  type Operators = Value
  val eq: Value      = Value("eq")
  val lt: Value      = Value("lt")
  val gt: Value      = Value("gt")
  val lte: Value     = Value("lte")
  val gte: Value     = Value("gte")
  val neq: Value     = Value("neq")
  val in: Value      = Value("in")
  val notin: Value   = Value("notin")
  val between: Value = Value("between")
}

case class Filter(
    variable: VariableId,
    operator: Operators.Operators,
    values: List[String]
)

/** Request the list of methods available */
case object MethodsQuery extends RemoteMessage

/** Response to MethodsQuery, lists the methods available */
case class MethodsResponse(
    /** Contains the list of methods serialized as a Json object*/
    methods: String
)

/** A query for data mining or more complex operations on data */
abstract class Query() extends RemoteMessage {

  /** List of variables ( aka dependent features ) */
  def variables: List[VariableId]

  /** List of covariables (aka independent features ) */
  def covariables: List[VariableId]

  /** List of groupings */
  // TODO: the concept of groupings is hazy, like a SQL group by but not only that, for example in R there are variants such as a:b and a*b
  // We may need to move away from it and be able to express full R formula (see https://stat.ethz.ch/R-manual/R-devel/library/stats/html/formula.html)
  def grouping: List[VariableId]

  /** Filters to apply on the data. Currently, a SQL where clause */
  // TODO: filters should be a structured parameter
  def filters: String
}

/** Data mining query executing a single algorithm */
case class MiningQuery(
    variables: List[VariableId],
    covariables: List[VariableId],
    grouping: List[VariableId],
    filters: String,
    algorithm: Algorithm
) extends Query

/** Experiment query using one or more algorithms on the same dataset and with an optional validation step */
case class ExperimentQuery(
    variables: List[VariableId],
    covariables: List[VariableId],
    grouping: List[VariableId],
    filters: String,
    algorithms: List[Algorithm],
    validations: List[Validation]
) extends Query

/** Response to a query */
case class QueryResult(
    /** Id of the job producing the result */
    jobId: String,
    /** Node where the result was computed */
    node: String,
    /** Date of creation of the result */
    timestamp: OffsetDateTime,
    /** Shape of the result. A MIME type */
    shape: String,
    /** Name of the function that produced the result */
    // TODO: rename to algorithm?
    function: String,
    /** Contains the result serialized as a string.
      * The format of the string is defined by the MIME type defined in the shape field.
      * It can be for example a JSON document defining the PFA model if the shape field is 'application/pfa+json'.
      */
    data: Option[String] = None,
    /** Contains the error message if the query was not successful */
    error: Option[String] = None
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
