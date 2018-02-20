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
import org.scalatest._
import spray.json._
import validationProtocol._

class ScoresTest extends WordSpec with Matchers with JsonUtils {

  "A BinaryClassificationScore" should {
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

      val json = score.toJson

      json.convertTo[VariableScore] shouldBe score

    }
  }

  "A PolynomialClassificationScore" should {
    "be serializable to Json and back" in {

      val matrix = Matrix(List("a", "b", "c"),
                          Array(
                            Array(0, 1.1, 2.0),
                            Array(1.0, 0.4, 3.0),
                            Array(0, 1.1, 2.0)
                          ))
      val score: VariableScore = PolynomialClassificationScore(
        `Confusion matrix` = matrix,
        `Accuracy` = 0.3,
        `Weighted F1-score` = 0.1,
        `Weighted false positive rate` = 3,
        `Weighted recall` = 2.4,
        `Weighted precision` = 0.9
      )

      val json = score.toJson

      json.convertTo[VariableScore] shouldBe score

    }
  }

  "A RegressionScore" should {
    "be serializable to Json and back" in {

      val score: VariableScore = RegressionScore(
        `MSE` = 0.3,
        `RMSE` = 0.1,
        `R-squared` = 3,
        `MAE` = 2.4,
        `Explained variance` = 0.9
      )

      val json = score.toJson

      json.convertTo[VariableScore] shouldBe score

    }
  }

  "A KFoldCrossValidationScore" should {
    "be serializable to Json and back" in {

      val avgScore: VariableScore = RegressionScore(
        `MSE` = 0.3,
        `RMSE` = 0.1,
        `R-squared` = 3,
        `MAE` = 2.4,
        `Explained variance` = 0.9
      )

      val f0Score: VariableScore = RegressionScore(
        `MSE` = 0.2,
        `RMSE` = 0.1,
        `R-squared` = 4,
        `MAE` = 2.2,
        `Explained variance` = 0.9
      )

      val f1Score: VariableScore = RegressionScore(
        `MSE` = 0.5,
        `RMSE` = 0.2,
        `R-squared` = 2,
        `MAE` = 2.6,
        `Explained variance` = 0.8
      )

      val xScore: Score = KFoldCrossValidationScore(
        average = avgScore,
        folds = Map(
          0 -> f0Score,
          1 -> f1Score
        )
      )

      val json = xScore.toJson

      json.convertTo[Score] shouldBe xScore

    }
  }

}
