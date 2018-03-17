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

case class Matrix(
    labels: List[String],
    values: Array[Array[Double]]
) {
  assert(values.length == labels.length)

  override def equals(obj: scala.Any): Boolean = obj match {
    case Matrix(l, v) if l.equals(labels) && v.length == values.length =>
      v.deep == values.deep
    case _ => false

  }
}

sealed trait Score
sealed trait VariableScore extends Score

case class BinaryClassificationScore(
    `Confusion matrix`: Matrix,
    `Accuracy`: Double,
    `Recall`: Double,
    `Precision`: Double,
    `F1-score`: Double,
    `False positive rate`: Double
) extends VariableScore

case class PolynomialClassificationScore(
    `Confusion matrix`: Matrix,
    `Accuracy`: Double,
    `Weighted recall`: Double,
    `Weighted precision`: Double,
    `Weighted F1-score`: Double,
    `Weighted false positive rate`: Double
) extends VariableScore

case class RegressionScore(
    `MSE`: Double,
    `RMSE`: Double,
    `R-squared`: Double,
    `MAE`: Double,
    `Explained variance`: Double
) extends VariableScore

case class KFoldCrossValidationScore(
    average: VariableScore,
    folds: Map[Int, VariableScore]
) extends Score
