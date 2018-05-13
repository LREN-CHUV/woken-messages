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

import io.swagger.annotations.ApiModel

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

/** Specification of a cross-validation method to use in an experiment.
  *
  * Cross-validation re-uses always the training dataset used during training, splitting it further into subsets
  * of training and testing datasets.
  *
  * @param code Code identifying the validation
  * @param parameters List of parameters to pass to the validation
  */
// TODO: ValidationSpec should be a sealed trait, with classes KFoldCrossValidation(k), SiteValidation(siteName, datasets)
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
