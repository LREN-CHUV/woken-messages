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


case class CodeValue(code: String, value: String) {
  def toTuple: (String, String) = (code, value)
}
object CodeValue {
  def fromTuple(t: (String, String)) = CodeValue(t._1, t._2)
}

/**
  * Specification for the execution of an algorithm
  *
  * @param code Code identifying the algorithm
  * @param parameters List of parameters to pass to the algorithm
  */
case class AlgorithmSpec(
    code: String,
    parameters: List[CodeValue],
    step: Option[ExecutionStep]
) {
  @transient lazy val parametersAsMap: Map[String, String] = parameters.map(_.toTuple).toMap
}

/**
  * Id of a user
  *
  * @param code Unique user ID
  */
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

object DatasetType extends Enumeration {
  type DatasetType = Value

  val training: Value   = Value("training")
  val testing: Value    = Value("testing")
  val validation: Value = Value("validation")
}

case class SelectDataset(selectionType: DatasetType.Value) extends StepInput

sealed trait Operation

case object Fold                     extends Operation
case class Compute(stepName: String) extends Operation

case class ExecutionStep(name: String,
                         execution: ExecutionStyle.Value,
                         input: StepInput,
                         operation: Operation)

case class ExecutionPlan(steps: List[ExecutionStep])

import DatasetType._

// TODO: execution plan describes a high-level workflow. Re-build it upon existing workflow DSL in Scala
object ExecutionPlan {
  val scatterGather = ExecutionPlan(
    List(
      ExecutionStep(name = "scatter",
                    execution = ExecutionStyle.map,
                    input = SelectDataset(training),
                    operation = Compute("compute")),
      ExecutionStep(name = "gather",
                    execution = ExecutionStyle.gather,
                    input = PreviousResults(fromStep = "scatter"),
                    operation = Fold)
      // TODO: ExecutionStep(name="remote-validate", execution = ExecutionStyle.map, input = SelectDataset.selectValidationDataset, operation=Validate())
    )
  )
  val mapReduce = ExecutionPlan(
    List(
      ExecutionStep(name = "map",
                    execution = ExecutionStyle.map,
                    input = SelectDataset(training),
                    operation = Compute("compute-local")),
      ExecutionStep(name = "reduce",
                    execution = ExecutionStyle.reduce,
                    input = PreviousResults(fromStep = "map"),
                    operation = Compute("compute-global"))
      // TODO: ExecutionStep(name="remote-validate", execution = ExecutionStyle.map, input = SelectDataset.selectValidationDataset, operation=Validate())
    )
  )
  val streaming = ExecutionPlan(
    List(
      ExecutionStep(name = "stream",
                    execution = ExecutionStyle.stream,
                    input = SelectDataset(training),
                    operation = Compute("compute-partial")),
      ExecutionStep(name = "reduce",
                    execution = ExecutionStyle.reduce,
                    input = PreviousResults(fromStep = "stream"),
                    operation = Compute("compute-global"))
      // TODO: ExecutionStep(name="remote-validate", execution = ExecutionStyle.map, input = SelectDataset.selectValidationDataset, operation=Validate())
    )
  )
}
