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

package eu.hbp.mip.woken.meta

import eu.hbp.mip.woken.JsonUtils
import org.scalatest.{ Matchers, WordSpec }
import spray.json._
import VariableMetaDataProtocol._
import eu.hbp.mip.woken.meta.{ EnumeratedValue => EV }

class VariableMetaDataTest extends WordSpec with Matchers with JsonUtils {

  "VariableMetaData" should {

    "be serializable to and from Json" in {

      val expectedJson = loadJson("/meta/apoe4-variable.json")

      val meta = VariableMetaData(
        code = "apoe4",
        label = "ApoE4",
        `type` = "polynominal",
        sqlType = Some("int"),
        description = Some("Apolipoprotein E (APOE) e4 allele"),
        methodology = Some("mip-cde"),
        units = None,
        enumerations = Some(List(EV("0", "0"), EV("1", "1"), EV("2", "2")))
      )

      meta.toJson shouldBe expectedJson

    }

    "be read from Json" in {

      val json = loadJson("/meta/apoe4-variable.json")

      val expectedMeta = VariableMetaData(
        code = "apoe4",
        label = "ApoE4",
        `type` = "polynominal",
        sqlType = Some("int"),
        description = Some("Apolipoprotein E (APOE) e4 allele"),
        methodology = Some("mip-cde"),
        units = None,
        enumerations = Some(List(EV("0", "0"), EV("1", "1"), EV("2", "2")))
      )

      json.convertTo[VariableMetaData] shouldBe expectedMeta

    }
  }

}
