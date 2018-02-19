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

package ch.chuv.lren.woken.messages.validation

import spray.json._

trait ValidationProtocol extends DefaultJsonProtocol {

  implicit val MatrixProtocol: JsonFormat[Matrix] = jsonFormat2(Matrix)

  private implicit val BinaryClassificationScoreProtocol: JsonFormat[BinaryClassificationScore] =
    jsonFormat6(BinaryClassificationScore)
  private implicit val PolynomialClassificationScoreProtocol
    : JsonFormat[PolynomialClassificationScore] =
    jsonFormat6(PolynomialClassificationScore)
  private implicit val RegressionScoreProtocol: JsonFormat[RegressionScore] = jsonFormat5(
    RegressionScore
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  implicit object VariableScoreJsonFormat extends JsonFormat[VariableScore] {
    def write(s: VariableScore): JsObject =
      JsObject((s match {
        case b: BinaryClassificationScore     => b.toJson
        case p: PolynomialClassificationScore => p.toJson
        case r: RegressionScore               => r.toJson
      }).asJsObject.fields + ("type" -> JsString(s.getClass.getSimpleName)))

    def read(value: JsValue): VariableScore =
      // If you need to read, you will need something in the
      // JSON that will tell you which subclass to use
      value.asJsObject.fields("type") match {
        case JsString("BinaryClassificationScore") => value.convertTo[BinaryClassificationScore]
        case JsString("PolynomialClassificationScore") =>
          value.convertTo[PolynomialClassificationScore]
        case JsString("RegressionScore") => value.convertTo[RegressionScore]
        case s                           => throw new IllegalArgumentException(s"Unknown type of Score: $s")
      }
  }

  implicit val KFoldCrossValidationScoreProtocol: JsonFormat[KFoldCrossValidationScore] =
    jsonFormat2(KFoldCrossValidationScore)

}
