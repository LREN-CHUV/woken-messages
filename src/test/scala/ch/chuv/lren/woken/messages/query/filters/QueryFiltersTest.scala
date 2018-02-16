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

package ch.chuv.lren.woken.messages.query.filters

import eu.hbp.mip.woken.JsonUtils
import org.scalatest.{ Matchers, WordSpec }
import queryFiltersProtocol._

class QueryFiltersTest extends WordSpec with Matchers with JsonUtils {

  "A query filter" should {
    "be read from detailed Json description" in {

      val filter = FilterRuleJsonFormat.read(loadJson("/messages/query/filters/filters.json"))

      val expected = CompoundFilterRule(
        Condition.and,
        List(
          SingleFilterRule("agegroup",
                           "agegroup",
                           "string",
                           InputType.select,
                           Operator.equal,
                           List("-50y")),
          CompoundFilterRule(
            Condition.and,
            List(
              SingleFilterRule(
                "lefttrifgtriangularpartoftheinferiorfrontalgyrus",
                "lefttrifgtriangularpartoftheinferiorfrontalgyrus",
                "double",
                InputType.number,
                Operator.equal,
                List("1")
              ),
              SingleFilterRule(
                "rightopifgopercularpartoftheinferiorfrontalgyrus",
                "rightopifgopercularpartoftheinferiorfrontalgyrus",
                "double",
                InputType.number,
                Operator.between,
                List("1", "20")
              )
            )
          )
        )
      )

      filter shouldBe expected

    }
  }

}
