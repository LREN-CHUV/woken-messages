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

package ch.chuv.lren.woken.messages.query.filters

import ch.chuv.lren.woken.JsonUtils
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
