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

package ch.chuv.lren.woken.messages.query

import eu.hbp.mip.woken.JsonUtils
import eu.hbp.mip.woken.messages.datasets.DatasetId
import eu.hbp.mip.woken.messages.variables.VariableId
import org.scalatest.{ Matchers, WordSpec }
import queryProtocol._

class QueryTest extends WordSpec with Matchers with JsonUtils {

  "Woken query API" should {

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
