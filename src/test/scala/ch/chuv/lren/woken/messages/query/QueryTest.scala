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

import ch.chuv.lren.woken.JsonUtils
import ch.chuv.lren.woken.messages.datasets.DatasetId
import ch.chuv.lren.woken.messages.variables.VariableId
import org.scalatest.{ Matchers, WordSpec }
import queryProtocol._

class QueryTest extends WordSpec with Matchers with JsonUtils {

  "Woken query models" should {

    "read specs for algorithms from json" in {
      val jsonAst   = loadJson("/messages/query/algorithm.json").asJsObject
      val algorithm = jsonAst.convertTo[AlgorithmSpec]

      val expected = AlgorithmSpec("knn", List(CodeValue("k", "5")))

      algorithm shouldBe expected
    }

    "read specs for validations from json" in {
      val jsonAst    = loadJson("/messages/query/validation.json").asJsObject
      val validation = jsonAst.convertTo[ValidationSpec]

      val expected = ValidationSpec("kfold", List(CodeValue("k", "2")))

      validation shouldBe expected
    }
  }

  "Woken query API" should {

    "read a mining query from json" in {
      val jsonAst     = loadJson("/messages/query/mining_query.json").asJsObject
      val miningQuery = jsonAst.convertTo[MiningQuery]

      val expected = MiningQuery(
        user = UserId("user1"),
        variables = List(VariableId("LeftAmygdala")),
        covariables = List(VariableId("AGE")),
        grouping = List(VariableId("COLPROT")),
        filters = None,
        targetTable = None,
        datasets = Set(),
        algorithm = AlgorithmSpec("knn", List(CodeValue("k", "5"))),
        executionPlan = None
      )

      miningQuery shouldBe expected
    }

    "read an experiment query from json" in {
      val jsonAst         = loadJson("/messages/query/experiment_query.json").asJsObject
      val experimentQuery = jsonAst.convertTo[ExperimentQuery]

      val expected = ExperimentQuery(
        user = UserId("user1"),
        variables = List(VariableId("LeftAmygdala")),
        covariables = List(VariableId("AGE")),
        grouping = List(VariableId("COLPROT")),
        filters = None,
        targetTable = None,
        trainingDatasets = Set("research", "clinical1", "clinical2").map(DatasetId),
        testingDatasets = Set(),
        validationDatasets = Set(),
        algorithms = List(AlgorithmSpec("linearRegression", List())),
        validations = List(ValidationSpec("kfold", List(CodeValue("k", "2")))),
        executionPlan = Some(ExecutionPlan.scatterGather)
      )

      experimentQuery shouldBe expected

    }
  }

}
