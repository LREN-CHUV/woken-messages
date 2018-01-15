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

import eu.hbp.mip.woken.JsonUtils
import org.scalatest.{ Matchers, WordSpec }
import spray.json.JsString

class ExecutionPlanTest extends WordSpec with Matchers with JsonUtils {
  import ExternalAPIProtocol._

  "An execution plan" should {

    "materialise from a template" in {

      ExecutionPlanFormat.read(JsString("scatter-gather")) shouldBe ExecutionPlan.scatterGather

      ExecutionPlanFormat.read(JsString("map-reduce")) shouldBe ExecutionPlan.mapReduce

      ExecutionPlanFormat.read(JsString("streaming")) shouldBe ExecutionPlan.streaming

    }

    "be read from detailed Json description" in {

      val plan = ExecutionPlanFormat.read(loadJson("/messages/external/custom-execution-plan.json"))

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
