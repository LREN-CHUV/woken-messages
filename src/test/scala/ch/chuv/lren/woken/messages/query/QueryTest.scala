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

import java.time.{ OffsetDateTime, ZoneOffset }

import ch.chuv.lren.woken.JsonUtils
import ch.chuv.lren.woken.messages.datasets.DatasetId
import ch.chuv.lren.woken.messages.variables.VariableId
import org.scalatest.{ Matchers, WordSpec }
import spray.json._
import queryProtocol._

import scala.collection.immutable.TreeSet

class QueryTest extends WordSpec with Matchers with JsonUtils {

  "Woken query models" should {

    "read specs for algorithms from json" in {
      val jsonAst   = loadJson("/messages/query/algorithm.json").asJsObject
      val algorithm = jsonAst.convertTo[AlgorithmSpec]

      val expected = AlgorithmSpec("knn", List(CodeValue("k", "5")), None)

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
        covariablesMustExist = true,
        grouping = List(VariableId("COLPROT")),
        filters = None,
        target = None,
        datasets = TreeSet(),
        algorithm = AlgorithmSpec("knn", List(CodeValue("k", "5")), None),
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
        covariablesMustExist = true,
        grouping = List(VariableId("COLPROT")),
        filters = None,
        target = None,
        trainingDatasets = TreeSet("research", "clinical1", "clinical2").map(DatasetId),
        testingDatasets = TreeSet(),
        validationDatasets = TreeSet(),
        algorithms = List(AlgorithmSpec("linearRegression", List(), None)),
        validations = List(ValidationSpec("kfold", List(CodeValue("k", "2")))),
        executionPlan = Some(ExecutionPlan.scatterGather)
      )

      experimentQuery shouldBe expected

      val expandedJson = loadJson("/messages/query/experiment_query_expanded.json").asJsObject
      experimentQuery.toJson shouldBe expandedJson
    }

    "serialize a mining query with the specs for algorithm" in {

      val miningQuery = MiningQuery(
        user = UserId("user1"),
        variables = List(VariableId("brainstem")),
        covariables = List(VariableId("leftcaudate")),
        covariablesMustExist = true,
        grouping = List(),
        filters = None,
        target = Some(Target(Some("cde_features_mixed"), None)),
        datasets = TreeSet(DatasetId("desd-synthdata"), DatasetId("qqni-synthdata")),
        algorithm = AlgorithmSpec("knn",
                                  List(),
                                  Some(
                                    ExecutionStep("map",
                                                  ExecutionStyle.map,
                                                  SelectDataset(DatasetType.training),
                                                  Compute("compute-local"))
                                  )),
        executionPlan = None
      )

      val json = miningQuery.toJson

      json.convertTo[MiningQuery] shouldBe miningQuery
    }

    "serialize a query result" in {
      val jsonAst = loadJson("/messages/query/mining_query.json").asJsObject
      val q       = jsonAst.convertTo[MiningQuery]
      val queryResult = QueryResult(
        Some("1"),
        "local",
        Set(DatasetId("setA"), DatasetId("setB")),
        List(UserWarning("Not much data"), UserInfo("250 records queried")),
        OffsetDateTime.of(2018, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC),
        Shapes.text,
        Some("fuzzy"),
        Some(JsString("Hi!")),
        None,
        Some(q)
      )

      val json = queryResult.toJson

      json.convertTo[QueryResult] shouldBe queryResult
    }

  }

}
