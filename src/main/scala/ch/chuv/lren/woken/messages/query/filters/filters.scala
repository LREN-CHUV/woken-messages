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
