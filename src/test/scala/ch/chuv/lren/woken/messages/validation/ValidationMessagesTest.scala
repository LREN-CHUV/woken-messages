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

import ch.chuv.lren.woken.JsonUtils
import org.scalatest.{ Matchers, WordSpec }
import spray.json._
import validationProtocol._

class ValidationMessagesTest extends WordSpec with Matchers with JsonUtils {

  "A ScoringResult" should {
    "be serializable to Json and back" in {

      val matrix = Matrix(List("a", "b", "c"),
                          Array(
                            Array(0, 1.1, 2.0),
                            Array(1.0, 0.4, 3.0),
                            Array(0, 1.1, 2.0)
                          ))
      val score: VariableScore = BinaryClassificationScore(
        `Confusion matrix` = matrix,
        `Accuracy` = 0.3,
        `F1-score` = 0.1,
        `False positive rate` = 3,
        `Recall` = 2.4,
        `Precision` = 0.9
      )

      val scoringResult = ScoringResult(Right(score))

      val json = scoringResult.toJson

      json.convertTo[ScoringResult] shouldBe scoringResult

    }
  }
}
