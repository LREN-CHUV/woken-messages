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

import akka.http.scaladsl.model.Uri
import ch.chuv.lren.woken.JsonUtils
import ch.chuv.lren.woken.messages.remoting.RemoteLocation
import org.scalatest.{ Matchers, WordSpec }
import spray.json._
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

  val tableA = TableId("features", "table_a")
  val dataset = Dataset(
    DatasetId("test"),
    "Test",
    "Test dataset",
    tables = List(tableA),
    anonymisationLevel = AnonymisationLevel.Identifying,
    location = Some(RemoteLocation(Uri("http://remote"), None))
  )

  "Datasets" should {

    "be serializable to Json and back" in {
      dataset.toJson.convertTo[Dataset] shouldBe dataset
    }
  }

}
