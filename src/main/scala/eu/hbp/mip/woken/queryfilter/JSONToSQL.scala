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

package eu.hbp.mip.woken.queryfilter

import spray.json.{ JsArray, JsNumber, JsObject, JsString }

/**
  * Simple parser for JSON produced by QueryBuilder JS
  *
  * TODO To be tested
  */
object Parser {
  val ruleOperators = Map(
    "equal"       -> "=",
    "not_equal"   -> "!=",
    "in"          -> "IN",
    "not_in"      -> "NOT IN",
    "less"        -> "<",
    "greater"     -> ">",
    "between"     -> "BETWEEN",
    "not_between" -> "NOT BETWEEN"
  )
  def parseRules(rule: JsObject): String = {
    import spray.json.DefaultJsonProtocol._

    rule match {
      // Group
      case _ if rule.fields.contains("rules") =>
        val operator = rule.fields("operator").toString()
        String.format("(%s)",
                      rule
                        .fields("rules")
                        .convertTo[JsArray]
                        .elements
                        .map(r => parseRules(r.asJsObject))
                        .mkString(" " + operator + " "))
      // End rule

      case _ =>
        val field: String = rule.fields("field") match {
          case s: JsString => s.toString()
          case n: JsNumber => n.toString()
          case a: JsArray if a.elements.length == 2 =>
            a.elements(0).toString() + " AND " + a.elements(1).toString()
          case _ => throw new Exception("Invalid JSON query!")
        }
        val operator = rule.fields("operator").toString()
        val value    = rule.fields("value").toString()

        field + " " + operator + " " + value
    }
  }
}
