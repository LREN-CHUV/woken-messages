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

package ch.chuv.lren.woken.errors

import java.lang.Thread.UncaughtExceptionHandler

import cats.data.NonEmptyList
import ch.chuv.lren.woken.messages.query.QueryResult
import ch.chuv.lren.woken.messages.query.queryProtocol._
import ch.chuv.lren.woken.messages.validation.validationProtocol._
import com.bugsnag.{ Bugsnag, Report }
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.Seq
import spray.json._

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
case class BugsnagErrorReporter(config: Config) extends ErrorReporter with LazyLogging {

  private val apiKey       = config.getString("bugsnag.apiKey")
  private val releaseStage = config.getString("bugsnag.releaseStage")

  private val appName   = config.getString("app.name")
  private val appType   = config.getString("app.type")
  private val version   = config.getString("app.version")
  private val buildDate = config.getString("app.buildDate")
  private val client = {
    val sendUncaughtExceptions = false // we are doing this ourselves
    val bugSnag                = new Bugsnag(apiKey, sendUncaughtExceptions)
    bugSnag.setAppVersion(version)
    bugSnag.setAppType(appType)
    bugSnag.setReleaseStage(releaseStage)
    bugSnag
  }

  // use this instance as uncaught exception handler
  private val self = this
  private val selfAsUncaughtExceptionHandler = new UncaughtExceptionHandler {
    override def uncaughtException(t: Thread, e: Throwable): Unit = self.report(e)
  }
  Thread.setDefaultUncaughtExceptionHandler(selfAsUncaughtExceptionHandler)

  private val dcCluster  = config.getString("clustering.cluster.name")
  private val dcSeedIP   = config.getString("clustering.seed-ip")
  private val dcSeedPort = config.getString("clustering.seed-port")
  private val dcLocation = config.getString("datacenter.location")
  private val dcHost     = config.getString("datacenter.host")
  private val dcOrchestration = {
    val co = config.getString("datacenter.containerOrchestration")
    if (co == "mesos" && config.getString("datacenter.mesos.containerName").isEmpty)
      "docker-compose"
    else
      co
  }

  private val mesosContainerName = config.getString("datacenter.mesos.containerName")
  private val mesosDockerImage   = config.getString("datacenter.mesos.dockerImage")
  private val mesosResourceCpu   = config.getString("datacenter.mesos.resourceCpu")
  private val mesosResourceMem   = config.getString("datacenter.mesos.resourceMem")
  private val mesosLabels        = config.getString("datacenter.mesos.labels")

  override def report(t: Throwable, meta: ErrorMetadata*): Unit = {
    val report: Report = client.buildReport(t)

    // General metadata
    report.addToTab("App", "Name", appName)
    report.addToTab("App", "BuildDate", buildDate)

    // Datacenter metadata
    report.addToTab("Datacenter", "Location", dcLocation)
    report.addToTab("Datacenter", "Host", dcHost)
    report.addToTab("Datacenter", "Cluster", dcCluster)
    report.addToTab("Datacenter", "SeedIP", dcSeedIP)
    report.addToTab("Datacenter", "SeedPort", dcSeedPort)
    report.addToTab("Datacenter", "ContainerOrchestration", dcOrchestration)

    // Mesos metadata
    if (dcOrchestration == "mesos") {
      report.addToTab("Mesos", "ContainerName", mesosContainerName)
      report.addToTab("Mesos", "DockerImage", mesosDockerImage)
      report.addToTab("Mesos", "Labels", mesosLabels)
      report.addToTab("Mesos", "ResourceCPU", mesosResourceCpu)
      report.addToTab("Mesos", "ResourceMemory", mesosResourceMem)
    }

    // Specific metadata
    meta foreach {
      case UserMetadata(userId) => report.setUserId(userId)
      case QueryError(result)   => addQuery(report, result)
      case x: ValidationError   => addValidation(report, x)
      case x: ScoringError      => addScoring(report, x)
      case x: RequestMetadata   => addRequest(report, x)
      case x: GenericMetadata   => addOther(report, x)
    }

    // In case we're running in an env without api key (local or testing), just print the messages for debugging
    if (apiKey.isEmpty) {
      logger.error(s"[Bugsnag - local / testing] Error report: $report")
      report.getException.printStackTrace()
    } else {
      val _ = client.notify(report)
    }
  }

  private def addOther(r: Report, meta: GenericMetadata) =
    r.addToTab(meta.group, meta.key, meta.value)

  private def addQuery(r: Report, result: QueryResult) = {
    result.jobId.foreach(jobId => r.addToTab("Query", "JobId", jobId))
    result.query.foreach(query => {
      r.addToTab("Query", "User", query.user.code)
      r.addToTab("Query", "Variables", query.variables.mkString(", "))
      r.addToTab("Query", "Covariables", query.covariables.mkString(", "))
      r.addToTab("Query", "CovariablesMustExist", query.covariablesMustExist)
      if (query.grouping.nonEmpty)
        r.addToTab("Query", "Grouping", query.grouping.mkString(", "))
      query.filters.map(filters => r.addToTab("Query", "Filters", filters.toSqlWhere))
      r.addToTab("Query", "QueryAsJson", query.toJson)
    })
    r.addToTab("Response", "Node", result.node)
    r.addToTab("Response", "Timestamp", result.timestamp.toString)
    if (result.feedback.nonEmpty)
      r.addToTab("Response",
                 "Feedback",
                 result.feedback.map[String, List[String]](f => s"${f.severity} ${f.msg}"))
    r.addToTab("Response", "DataProvenance", result.dataProvenance.map(_.code).mkString(", "))
    r.addToTab("Response", "Type", result.`type`)
    result.algorithm.foreach(algorithm => r.addToTab("Query", "Algorithm", algorithm))
    result.data.foreach(data => r.addToTab("Query", "Data", data.compactPrint))
    result.error.foreach(error => r.addToTab("Query", "Error", error))
    r
  }

  private def addValidation(r: Report, error: ValidationError) = {
    val v = error.validation
    r.addToTab("Validation", "Fold", v.fold)
    r.addToTab("Validation", "Variable", v.varInfo)
    r.addToTab("Validation", "PFA", v.pfaModel.compactPrint)

    error.result.foreach { res =>
      r.addToTab("Result", "Fold", res.fold)
      r.addToTab("Result", "Variable", res.varInfo)
      res.result.fold(
        err => r.addToTab("Result", "Error", err),
        okRes => r.addToTab("Result", "Validation", okRes.map(_.compactPrint).mkString("\n"))
      )
    }
    r
  }

  private def addScoring(r: Report, error: ScoringError) = {
    val v = error.scoring
    r.addToTab("Scoring", "Variable", v.targetMetaData)
    r.addToTab("Scoring", "AlgorithmOutput", toJsonStr(v.algorithmOutput))
    r.addToTab("Scoring", "GroundTruth", toJsonStr(v.groundTruth))

    error.result.foreach { res =>
      res.result.fold(
        err => r.addToTab("Result", "Error", err),
        okRes => r.addToTab("Result", "Scores", okRes.toJson.compactPrint)
      )
    }
    r
  }

  private def toJsonStr(values: NonEmptyList[JsValue]): String =
    JsArray(values.toList.toVector).compactPrint

  private def addRequest(r: Report, meta: RequestMetadata) = {
    r.addToTab("Request", "RequestId", meta.requestId)
    r.addToTab("Request", "Method", meta.method)
    r.addToTab("Request", "Uri", meta.uri)
    r.addToTab("Request", "Headers", headersAsString(meta.headers))
  }

  private def headersAsString(headers: Seq[(String, String)]): String =
    headers
      .map {
        case (key, value) => s"$key: $value"
      }
      .mkString("\n")
}
