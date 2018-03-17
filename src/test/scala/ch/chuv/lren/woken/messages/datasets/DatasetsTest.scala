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

package ch.chuv.lren.woken.messages.datasets

import ch.chuv.lren.woken.JsonUtils
import org.scalatest.{ Matchers, WordSpec }
import datasetsProtocol._

class DatasetsTest extends WordSpec with Matchers with JsonUtils {

  "Woken datasets API" should {

    "read a datasets query from json" in {
      val jsonAst = loadJson("/messages/datasets/datasets_query.json").asJsObject
      val query   = jsonAst.convertTo[DatasetsQuery]

      val expected = DatasetsQuery(Some("sample"))

      query shouldBe expected
    }
  }

}
