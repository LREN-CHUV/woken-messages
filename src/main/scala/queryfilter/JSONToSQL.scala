package queryfilter

import spray.json.{JsArray, JsNumber, JsObject, JsString, JsNumber}

/**
  * Simple parser for JSON produced by QueryBuilder JS
  *
  * TODO To be tested
  */
object Parser {
  val ruleOperators = Map(
    "equal" -> "=",
    "not_equal" -> "!=",
    "in" -> "IN",
    "not_in" -> "NOT IN",
    "less" -> "<",
    "greater" -> ">",
    "between" -> "BETWEEN",
    "not_between" -> "NOT BETWEEN"
  )
  def parseRules(rule: JsObject): String = {
    rule match {
      // Group
      case _ if rule.fields.contains("rules") => {
        val operator = rule.fields.get("operator").get.convertTo[JsString]
        String.format("(%s)", rule.fields.get("rules").get.convertTo[JsArray].elements.map(r => parseRules(r.asJsObject)).mkString(" " + operator + " "))
      }
      // End rule
      case _ =>  {
        val field: String = rule.fields.get("field").get match {
          case s: JsString => s.toString()
          case n: JsNumber => n.toString()
          case a: JsArray if a.elements.length == 2 => a.elements(0) + " AND " + a.elements(1)
          case _ => throw new Exception("Invalid JSON query!")
        }
        val operator = rule.fields.get("operator").get.convertTo[JsString]
        val value = rule.fields.get("value").get.convertTo[JsString]

        field + " " + operator + " " + value
      }
    }
  }
}
