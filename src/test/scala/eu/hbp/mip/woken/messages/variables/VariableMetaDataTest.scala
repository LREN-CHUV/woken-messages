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

package eu.hbp.mip.woken.messages.variables

import eu.hbp.mip.woken.JsonUtils
import org.scalatest.{ Matchers, WordSpec }
import spray.json._
import variablesProtocol._
import eu.hbp.mip.woken.messages.variables.{ EnumeratedValue => EV }

class VariableMetaDataTest extends WordSpec with Matchers with JsonUtils {

  val apoe4Meta = VariableMetaData(
    code = "apoe4",
    label = "ApoE4",
    `type` = "polynominal",
    sqlType = Some("int"),
    description = Some("Apolipoprotein E (APOE) e4 allele"),
    methodology = Some("mip-cde"),
    units = None,
    enumerations = Some(List(EV("0", "0"), EV("1", "1"), EV("2", "2"))),
    datasets = Set()
  )

  val apoe4Json: JsValue = loadJson("/messages/variables/apoe4-variable.json")

  "VariableMetaData" should {

    "be serializable to Json" in {
      apoe4Meta.toJson shouldBe apoe4Json
    }

    "be deserializable from Json" in {
      apoe4Json.convertTo[VariableMetaData] shouldBe apoe4Meta
    }
  }

}
