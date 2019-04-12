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

package ch.chuv.lren.woken.utils

import com.typesafe.config.{ Config, ConfigFactory }
import org.scalatest.{ Matchers, WordSpec }

class ConfigurationLoaderTest extends WordSpec with Matchers {

  "Reference configuration" should {
    "be self-contained" in {
      val reference = ConfigFactory
        .parseResourcesAnySyntax("reference.conf")
        .resolve()
      reference.getConfig("akka") shouldNot be(null)
      reference.getConfig("clustering") shouldNot be(null)
    }
  }

  "Akka remoting configuration" should {

    "define remoting implementation" in {
      val remoting = ConfigFactory.load("akka-remoting.conf")
      remoting.getString("remoting.implementation") should (equal("artery") or equal("netty"))
    }
  }

  "Akka cluster configuration" should {

    "define akka.cluster" in {
      val cluster: Config = ConfigFactory
        .parseResourcesAnySyntax("akka-artery-remoting.conf")
        .withFallback(ConfigFactory.parseResourcesAnySyntax("akka-cluster.conf"))
        .withFallback(ConfigFactory.load())
        .resolve()
      cluster.getConfig("akka.cluster") shouldNot be(null)
    }
  }

  "ConfigurationLoader" should {
    "append common clustering configuration to an app configuration" in {
      val appConfig = ConfigFactory.parseString("""
          |app {
          |  prop = "value"
          |}
          |akka {
          |  cluster {
          |    roles = ["woken"]
          |  }
          |}
        """.stripMargin)
      val config    = ConfigurationLoader.appendClusterConfiguration(appConfig).resolve()

      config.getString("app.prop") shouldBe "value"
      config.getList("akka.cluster.seed-nodes").unwrapped() should contain(
        "akka.tcp://woken@127.0.0.1:8088"
      )
      config.getList("akka.cluster.roles").unwrapped() should contain("woken")
    }
  }
}
