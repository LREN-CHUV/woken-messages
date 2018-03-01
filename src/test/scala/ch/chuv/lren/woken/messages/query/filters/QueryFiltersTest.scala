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
import FilterRule._
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

  "SqlStrings" should {

    "produce safe numerical output" in {
      "1".safeValue shouldBe "1"
      "-2".safeValue shouldBe "-2"
      "+3".safeValue shouldBe "3"
      "1.23".safeValue shouldBe "1.23"
      "-1.23".safeValue shouldBe "-1.23"
    }

    "wrap strings into quotes" in {
      "a".safeValue shouldBe "'a'"
      "3e".safeValue shouldBe "'3e'"
      "a'b'c".safeValue shouldBe "'a''b''c'"
    }

    "prevent SQL injection from values [security]" in {
      "Bobby'; DROP DATABASE; --".safeValue shouldBe "'Bobby''; DROP DATABASE; --'"
      "10; DROP DATABASE; --".safeValue shouldBe "'10; DROP DATABASE; --'"
      "' + (SELECT TOP 1 password FROM users ) + '".safeValue shouldBe "''' + (SELECT TOP 1 password FROM users ) + '''"
    }

    "quote identifiers" in {
      "a".identifier shouldBe """"a""""
      "3".identifier shouldBe """"3""""
      "a c".identifier shouldBe """"a c""""
    }

    "prevent SQL injection from identifiers [security]" in {
      """Bob"; DROP DATABASE --""".identifier shouldBe """"Bob""; DROP DATABASE --""""
    }
  }

  "FilterRuleToSql" should {

    "generate the where clause for a simple filter" in {
      val simpleFilter = SingleFilterRule("col1",
                                          "col1",
                                          "string",
                                          InputType.number,
                                          Operator.greaterOrEqual,
                                          List("10.5"))
      simpleFilter.toSqlWhere shouldBe """"col1" >= 10.5"""
    }

    "generate the where clause for a more complex filter" in {
      val left = SingleFilterRule("col1",
                                  "col1",
                                  "string",
                                  InputType.number,
                                  Operator.greaterOrEqual,
                                  List("10.5"))

      val right = SingleFilterRule("col2",
                                   "col2",
                                   "string",
                                   InputType.text,
                                   Operator.beginsWith,
                                   List("beginning"))

      val compoundFilter = CompoundFilterRule(Condition.and, List(left, right))
      compoundFilter.toSqlWhere shouldBe """"col1" >= 10.5 AND "col2" LIKE 'beginning%'""".stripMargin
    }

    // try to find some tricky filters, filters with bad values that may be injected by an attacker
  }

}
