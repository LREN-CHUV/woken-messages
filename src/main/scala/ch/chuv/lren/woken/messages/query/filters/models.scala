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

import org.postgresql.core.Utils

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

object FilterRule {

  private val numberRegex = "[-+]?\\d+(\\.\\d+)?".r

  implicit class SqlStrings(val s: String) extends AnyVal {

    def safeValue: String =
      if (numberRegex.pattern.matcher(s).matches())
        if (s.startsWith("+"))
          s.substring(1)
        else s
      else {
        val sb = new java.lang.StringBuilder("'")
        Utils.escapeLiteral(sb, s, false).append("'").toString
      }

    def identifier: String = {
      val sb = new java.lang.StringBuilder()
      Utils.escapeIdentifier(sb, s).toString
    }
  }

  implicit class FilterRuleToSql(val rule: FilterRule) extends AnyVal {

    def withAdaptedFieldName: FilterRule = rule match {
      case c: CompoundFilterRule =>
        CompoundFilterRule(c.condition, c.rules.map(_.withAdaptedFieldName))
      case s: SingleFilterRule =>
        s.copy(field = s.field.toLowerCase().replaceAll("-", "_").replaceFirst("^(\\d)", "_$1"))
    }

    @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
    def toSqlWhere: String = rule match {
      case c: CompoundFilterRule =>
        c.rules
          .map {
            case r: SingleFilterRule                                      => r.toSqlWhere
            case sc: CompoundFilterRule if sc.rules.lengthCompare(1) <= 0 => s"${sc.toSqlWhere}"
            case sc: CompoundFilterRule                                   => s"(${sc.toSqlWhere})"
          }
          .filter(_.nonEmpty)
          .mkString(s" ${c.condition.toString} ")
      case s: SingleFilterRule =>
        s.operator match {
          case Operator.equal =>
            s.value.headOption.fold(s"${s.field.identifier} IS NULL")(
              v => s"${s.field.identifier} = ${v.safeValue}"
            )
          case Operator.notEqual =>
            s.value.headOption.fold(s"${s.field.identifier} IS NOT NULL")(
              v => s"${s.field.identifier} != ${v.safeValue}"
            )
          case Operator.less           => s"${s.field.identifier} < ${s.value.head.safeValue}"
          case Operator.greater        => s"${s.field.identifier} > ${s.value.head.safeValue}"
          case Operator.lessOrEqual    => s"${s.field.identifier} <= ${s.value.head.safeValue}"
          case Operator.greaterOrEqual => s"${s.field.identifier} >= ${s.value.head.safeValue}"
          case Operator.in =>
            s"${s.field.identifier} IN (${s.value.map(_.safeValue).mkString(",")})"
          case Operator.notIn =>
            s"${s.field.identifier} NOT IN (${s.value.map(_.safeValue).mkString(",")})"
          case Operator.between =>
            s"${s.field.identifier} BETWEEN ${s.value.head.safeValue} AND ${s.value.last.safeValue}"
          case Operator.notBetween =>
            s"${s.field.identifier} NOT BETWEEN ${s.value.head.safeValue} AND ${s.value.last.safeValue}"
          case Operator.beginsWith =>
            s"${s.field.identifier} LIKE ${(s.value.head + "%").safeValue}"
          case Operator.notBeginsWith =>
            s"${s.field.identifier} NOT LIKE ${(s.value.head + "%").safeValue}"
          case Operator.contains =>
            s"${s.field.identifier} LIKE ${("%" + s.value.head + "%").safeValue}"
          case Operator.notContains =>
            s"${s.field.identifier} NOT LIKE ${("%" + s.value.head + "%").safeValue}"
          case Operator.endsWith => s"${s.field.identifier} LIKE ${("%" + s.value.head).safeValue}"
          case Operator.notEndsWith =>
            s"${s.field.identifier} NOT LIKE ${("%" + s.value.head).safeValue}"
          case Operator.isEmpty    => s"'COALESCE(${s.field.identifier}, '') = ''"
          case Operator.isNotEmpty => s"'COALESCE(${s.field.identifier}, '') != ''"
          case Operator.isNull     => s"${s.field.identifier} IS NULL"
          case Operator.isNotNull  => s"${s.field.identifier} IS NOT NULL"
        }
    }
  }

  // Java API
  def sqlWhere(rule: FilterRule): String = rule.toSqlWhere

}
