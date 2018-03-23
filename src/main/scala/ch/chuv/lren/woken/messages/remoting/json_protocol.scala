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

package ch.chuv.lren.woken.messages.remoting

import akka.http.scaladsl.model.Uri
import spray.json._

trait RemotingProtocol extends DefaultJsonProtocol {

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  implicit object UriFormat extends JsonFormat[Uri] {
    override def read(json: JsValue): Uri = Uri(json.asInstanceOf[JsString].value)

    override def write(obj: Uri): JsValue = JsString(obj.toString)
  }

  implicit val BasicAuthenticationFormat: JsonFormat[BasicAuthentication] = jsonFormat2(
    BasicAuthentication
  )
  implicit object RemoteLocationFormat extends RootJsonFormat[RemoteLocation] {

    override def write(rl: RemoteLocation): JsValue =
      JsObject(
        List[Option[(String, JsValue)]](
          Some("url"                       -> JsString(rl.url.toString)),
          rl.credentials.map("credentials" -> _.toJson)
        ).flatten: _*
      )

    override def read(json: JsValue): RemoteLocation = {
      val jsObject = json.asJsObject
      jsObject.getFields("url") match {
        case Seq(url) =>
          RemoteLocation(
            url = Uri(url.convertTo[String]),
            credentials = jsObject.fields.get("credentials").map(_.convertTo[BasicAuthentication])
          )
        case _ =>
          deserializationError(
            s"Cannot deserialize RemoteLocation: invalid input. Raw input: $json"
          )
      }
    }

  }

}
