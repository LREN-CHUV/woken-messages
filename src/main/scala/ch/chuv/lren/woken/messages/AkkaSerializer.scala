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

package ch.chuv.lren.woken.messages

import ch.chuv.lren.woken.messages.datasets.{
  DatasetsQuery,
  DatasetsResponse,
  TablesQuery,
  TablesResponse
}
import ch.chuv.lren.woken.messages.query._
import ch.chuv.lren.woken.messages.validation.{
  ScoringQuery,
  ScoringResult,
  ValidationQuery,
  ValidationResult
}
import akka.serialization.Serializer
import spray.json._
import APIJsonProtocol._
import ch.chuv.lren.woken.messages.variables.{
  VariablesForDatasetsQuery,
  VariablesForDatasetsResponse
}

/**
  * Serializer for all messages of this API exchanged with Akka
  */
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class AkkaSerializer extends Serializer {

  override def identifier: Int = 34543534

  override def toBinary(o: AnyRef): Array[Byte] = {
    val json = o match {
      case p: Ping              => p.toJson
      case p: Pong              => p.toJson
      case p: ComponentQuery    => p.toJson
      case p: ComponentResponse => p.toJson
      case p: VersionQuery      => p.toJson
      case p: VersionResponse   => p.toJson
      case q: DatasetsQuery     => q.toJson
      case r: DatasetsResponse  => r.toJson
      case q: TablesQuery       => q.toJson
      case r: TablesResponse    => r.toJson
      case MethodsQuery         => JsString("")
      case r: MethodsResponse   => r.toJson
      case q: MiningQuery       => q.toJson
      case q: ExperimentQuery   => q.toJson
      case r: QueryResult       => r.toJson
      case q: ValidationQuery   => q.toJson
      case r: ValidationResult  => r.toJson
      case q: ScoringQuery      => q.toJson
      case r: ScoringResult     => r.toJson

      case q: VariablesForDatasetsQuery    => q.toJson
      case r: VariablesForDatasetsResponse => r.toJson
      case _ =>
        throw new IllegalArgumentException(
          s"Serializer does not support object of class ${o.getClass}"
        )
    }
    json.compactPrint.getBytes
  }

  override def includeManifest: Boolean = true

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val json = new String(bytes).parseJson

    val pingClass              = classOf[Ping]
    val pongClass              = classOf[Pong]
    val componentQueryClass    = classOf[ComponentQuery]
    val componentResponseClass = classOf[ComponentResponse]
    val versionQueryClass      = classOf[VersionQuery]
    val versionResponseClass   = classOf[VersionResponse]
    val datasetsQueryClass     = classOf[DatasetsQuery]
    val datasetsResponseClass  = classOf[DatasetsResponse]
    val tablesQueryClass       = classOf[TablesQuery]
    val tablesResponseClass    = classOf[TablesResponse]
    val methodsQueryClass      = MethodsQuery.getClass
    val methodsResponseClass   = classOf[MethodsResponse]
    val miningQueryClass       = classOf[MiningQuery]
    val experimentQueryClass   = classOf[ExperimentQuery]
    val queryResultClass       = classOf[QueryResult]
    val validationQueryClass   = classOf[ValidationQuery]
    val validationResultClass  = classOf[ValidationResult]
    val scoringQueryClass      = classOf[ScoringQuery]
    val scoringResultClass     = classOf[ScoringResult]

    val variablesForDatasetsQueryClass  = classOf[VariablesForDatasetsQuery]
    val variablesForDatasetsResultClass = classOf[VariablesForDatasetsResponse]

    val result: AnyRef = manifest.getOrElse(classOf[Unit]) match {
      case `pingClass`              => json.convertTo[Ping]
      case `pongClass`              => json.convertTo[Pong]
      case `componentQueryClass`    => json.convertTo[ComponentQuery]
      case `componentResponseClass` => json.convertTo[ComponentResponse]
      case `versionQueryClass`      => json.convertTo[VersionQuery]
      case `versionResponseClass`   => json.convertTo[VersionResponse]
      case `datasetsQueryClass`     => json.convertTo[DatasetsQuery]
      case `datasetsResponseClass`  => json.convertTo[DatasetsResponse]
      case `tablesQueryClass`       => json.convertTo[TablesQuery]
      case `tablesResponseClass`    => json.convertTo[TablesResponse]
      case `methodsQueryClass`      => MethodsQuery
      case `methodsResponseClass`   => json.convertTo[MethodsResponse]
      case `miningQueryClass`       => json.convertTo[MiningQuery]
      case `experimentQueryClass`   => json.convertTo[ExperimentQuery]
      case `queryResultClass`       => json.convertTo[QueryResult]
      case `validationQueryClass`   => json.convertTo[ValidationQuery]
      case `validationResultClass`  => json.convertTo[ValidationResult]
      case `scoringQueryClass`      => json.convertTo[ScoringQuery]
      case `scoringResultClass`     => json.convertTo[ScoringResult]

      case `variablesForDatasetsQueryClass`  => json.convertTo[VariablesForDatasetsQuery]
      case `variablesForDatasetsResultClass` => json.convertTo[VariablesForDatasetsResponse]

      case c =>
        throw new IllegalArgumentException(
          s"Deserializer does not support object of class ${c.getName}"
        )

    }
    result
  }

}
