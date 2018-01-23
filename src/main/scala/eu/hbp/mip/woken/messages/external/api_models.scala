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

import eu.hbp.mip.woken.messages.queryFilters.FilterRule
import eu.hbp.mip.woken.messages.{ RemoteMessage, queryFilters }
import io.swagger.annotations.ApiModel
import spray.json.JsValue

case class CodeValue(code: String, value: String) {
  def toTuple: (String, String) = (code, value)
}
object CodeValue {
  def fromTuple(t: (String, String)) = CodeValue(t._1, t._2)
}

/**
  * An algorithm
  *
  * @param code Code identifying the algorithm
  * @param parameters List of parameters to pass to the algorithm
  */
@ApiModel(
  description = "Specification for the execution of an algorithm"
)
case class AlgorithmSpec(
    code: String,
    parameters: List[CodeValue]
) {
  @transient lazy val parametersAsMap: Map[String, String] = parameters.map(_.toTuple).toMap
}

/**
  * Id of a user
  *
  * @param code Unique user ID
  */
@ApiModel(
  description = "Id of a user"
)
case class UserId(
    code: String
)

/**
  * Id of a dataset
  *
  * @param code Unique dataset code, used to select
  */
@ApiModel(
  description = "Id of a dataset"
)
case class DatasetId(
    code: String
)

/**
  * Id of a variable
  *
  * @param code Unique dataset code, used to request
  */
@ApiModel(
  description = "Id of a variable"
)
case class VariableId(
    code: String
)

/** Specification of a cross-validation method to use in an experiment.
  *
  * Cross-validation re-uses always the training dataset used during training, splitting it further into subsets
  * of training and testing datasets.
  *
  * @param code Code identifying the validation
  * @param parameters List of parameters to pass to the validation
  */
case class ValidationSpec(
    code: String,
    parameters: List[CodeValue]
) {
  @transient lazy val parametersAsMap: Map[String, String] = parameters.map(_.toTuple).toMap
}

object ExecutionStyle extends Enumeration {
  type ExecutionStyle = Value

  /** Apply a step onto each remote node, in parallel */
  val map: Value = Value("map")

  /** Wait for the results from the previous map step, then execute a step locally */
  val reduce: Value = Value("reduce")

  /** Perform a computation on one node, then move the intermediate results to the next node and repeat */
  val stream: Value = Value("stream")

  /** Gather results into a list */
  val gather: Value = Value("gather")
}

sealed trait StepInput

case class PreviousResults(fromStep: String) extends StepInput

case class SelectDataset(selectionType: String) extends StepInput

case object SelectDataset {
  val selectTrainingDataset   = SelectDataset("training")
  val selectTestingDataset    = SelectDataset("testing")
  val selectValidationDataset = SelectDataset("validation")
}

sealed trait Operation

case object Fold                     extends Operation
case class Compute(stepName: String) extends Operation

case class ExecutionStep(name: String,
                         execution: ExecutionStyle.Value,
                         input: StepInput,
                         operation: Operation)

case class ExecutionPlan(steps: List[ExecutionStep])

object ExecutionPlan {
  val scatterGather = ExecutionPlan(
    List(
      ExecutionStep(name = "scatter",
                    execution = ExecutionStyle.map,
                    input = SelectDataset.selectTrainingDataset,
                    operation = Compute("compute")),
      ExecutionStep(name = "gather",
                    execution = ExecutionStyle.gather,
                    input = PreviousResults(fromStep = "scatter"),
                    operation = Fold)
    )
  )
  val mapReduce = ExecutionPlan(
    List(
      ExecutionStep(name = "map",
                    execution = ExecutionStyle.map,
                    input = SelectDataset.selectTrainingDataset,
                    operation = Compute("compute-local")),
      ExecutionStep(name = "reduce",
                    execution = ExecutionStyle.reduce,
                    input = PreviousResults(fromStep = "map"),
                    operation = Compute("compute-global"))
    )
  )
  val streaming = ExecutionPlan(
    List(
      ExecutionStep(name = "stream",
                    execution = ExecutionStyle.stream,
                    input = SelectDataset.selectTrainingDataset,
                    operation = Compute("compute-partial")),
      ExecutionStep(name = "reduce",
                    execution = ExecutionStyle.reduce,
                    input = PreviousResults(fromStep = "stream"),
                    operation = Compute("compute-global"))
    )
  )
}

/** Request the list of methods available */
case object MethodsQuery extends RemoteMessage

/** Response to MethodsQuery, lists the methods available
  *
  * @param methods Contains the list of methods serialized as a Json object
  */
case class MethodsResponse(
    methods: String
)

/** A query for data mining or more complex operations on data */
abstract class Query() extends RemoteMessage {

  /** User issuing the query */
  def user: UserId

  /** List of variables ( aka dependent features ) */
  def variables: List[VariableId]

  /** List of covariables (aka independent features ) */
  def covariables: List[VariableId]

  /** List of groupings */
  // TODO: the concept of groupings is hazy, like a SQL group by but not only that, for example in R there are variants such as a:b and a*b
  // We may need to move away from it and be able to express full R formula (see https://stat.ethz.ch/R-manual/R-devel/library/stats/html/formula.html)
  def grouping: List[VariableId]

  /** Filters to apply on the data. Currently, a SQL where clause */
  def filters: Option[FilterRule]
}

/**
  *  Data mining query executing a single algorithm
  *
  * @param user User issuing the query
  * @param variables List of variables ( aka dependent features )
  * @param covariables List of covariables (aka independent features )
  * @param grouping List of groupings
  * @param filters Filters to apply on the data. Currently, a SQL where clause
  * @param datasets Selection of the datasets to query
  * @param algorithm Algorithm to execute, with parameters defined
  */
case class MiningQuery(
    user: UserId,
    variables: List[VariableId],
    covariables: List[VariableId],
    grouping: List[VariableId],
    filters: Option[FilterRule],
    datasets: Set[DatasetId],
    algorithm: AlgorithmSpec
) extends Query

/**
  *  Experiment query using one or more algorithms on the same dataset and with an optional validation step
  *
  * @param user User issuing the query
  * @param variables List of variables ( aka dependent features )
  * @param covariables List of covariables (aka independent features )
  * @param grouping List of groupings
  * @param filters Filters to apply on the data. Currently, a SQL where clause
  * @param trainingDatasets Set of datasets used for training
  * @param testingDatasets Set of datasets used for testing. Ignored for cross-validation methods
  * @param algorithms List of algorithms to execute, with parameters defined
  * @param validationDatasets List of datasets used for validation. Ignored for cross-validation methods
  * @param validations List of validations to apply
  * @param executionPlan Execution plan
  */
case class ExperimentQuery(
    user: UserId,
    variables: List[VariableId],
    covariables: List[VariableId],
    grouping: List[VariableId],
    filters: Option[FilterRule],
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
