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
import org.scalatest.{ Matchers, WordSpec }
import spray.json.JsString
import queryProtocol._

class ExecutionPlanTest extends WordSpec with Matchers with JsonUtils {

  "An execution plan" should {

    "materialise from a template" in {

      ExecutionPlanFormat.read(JsString("scatter-gather")) shouldBe ExecutionPlan.scatterGather

      ExecutionPlanFormat.read(JsString("map-reduce")) shouldBe ExecutionPlan.mapReduce

      ExecutionPlanFormat.read(JsString("streaming")) shouldBe ExecutionPlan.streaming

    }

    "be read from detailed Json description" in {

      val plan = ExecutionPlanFormat.read(loadJson("/messages/query/custom-execution-plan.json"))

      val expected = ExecutionPlan(
        List(
          ExecutionStep(name = "map",
                        execution = ExecutionStyle.map,
                        input = SelectDataset.selectTrainingDataset,
                        operation = Compute("compute-1")),
          ExecutionStep(name = "reduce",
                        execution = ExecutionStyle.reduce,
                        input = PreviousResults(fromStep = "map"),
                        operation = Compute("compute-2"))
        )
      )

      plan shouldBe expected

    }

  }

}
