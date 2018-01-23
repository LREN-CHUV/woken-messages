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

package eu.hbp.mip.woken.messages.query.filters

/*
 * Filters as defined by http://querybuilder.js.org/
 */

/** List of operations supported by a filter */
object Operator extends Enumeration {
  type Operator = Value
  val equal: Value          = Value("equal")
  val notEqual: Value       = Value("not_equal")
  val less: Value           = Value("less")
  val greater: Value        = Value("greater")
  val lessOrEqual: Value    = Value("less_or_equal")
  val greaterOrEqual: Value = Value("greater_or_equal")
  val in: Value             = Value("in")
  val notIn: Value          = Value("not_in")
  val between: Value        = Value("between")
  val notBetween: Value     = Value("not_between")
  val beginsWith: Value     = Value("begins_with")
  val notBeginsWith: Value  = Value("not_begins_with")
  val contains: Value       = Value("contains")
  val notContains: Value    = Value("not_contains")
  val endsWith: Value       = Value("ends_with")
  val notEndsWith: Value    = Value("not_ends_with")
  val isEmpty: Value        = Value("is_empty")
  val isNotEmpty: Value     = Value("is_not_empty")
  val isNull: Value         = Value("is_null")
  val isNotNull: Value      = Value("is_not_null")
}

object Condition extends Enumeration {
  type Operators = Value
  val and: Value = Value("AND")
  val or: Value  = Value("OR")
}

object InputType extends Enumeration {
  type InputType = Value
  val text: Value     = Value("text")
  val number: Value   = Value("number")
  val select: Value   = Value("select")
  val textarea: Value = Value("textarea")
  val radio: Value    = Value("radio")
  val checkbox: Value = Value("checkbox")
}

trait FilterRule

case class SingleFilterRule(
    id: String,
    field: String,
    `type`: String,
    input: InputType.Value,
    operator: Operator.Value,
    value: List[String]
) extends FilterRule

case class CompoundFilterRule(
    condition: Condition.Value,
    rules: List[FilterRule]
) extends FilterRule
