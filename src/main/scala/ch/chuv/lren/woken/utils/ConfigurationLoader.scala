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

object ConfigurationLoader {

  /**
    * Append the common configuration for clustering
    *
    * @param appConfig Configuration specific to the application
    * @return a combined config that requires resolve()
    */
  def appendClusterConfiguration(appConfig: Config): Config = {

    val remotingConfig = ConfigFactory.load()
    val remotingImpl   = remotingConfig.getString("remoting.implementation")

    ConfigFactory
      .parseResourcesAnySyntax(s"akka-$remotingImpl-remoting.conf")
      .withFallback(ConfigFactory.parseResourcesAnySyntax("akka-cluster.conf"))
      .withFallback(appConfig)
      .withFallback(remotingConfig)
  }
}
