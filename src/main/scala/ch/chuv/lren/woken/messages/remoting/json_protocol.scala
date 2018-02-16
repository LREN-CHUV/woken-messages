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

package ch.chuv.lren.woken.messages.remoting

import akka.http.scaladsl.model.Uri
import spray.json.{ DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat }

trait RemotingProtocol extends DefaultJsonProtocol {

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  implicit object UriFormat extends JsonFormat[Uri] {
    override def read(json: JsValue): Uri = Uri(json.asInstanceOf[JsString].value)

    override def write(obj: Uri): JsValue = JsString(obj.toString)
  }

  implicit val BasicAuthenticationFormat: JsonFormat[BasicAuthentication] = jsonFormat2(
    BasicAuthentication
  )
  implicit val RemoteLocationFormat: RootJsonFormat[RemoteLocation] = jsonFormat2(RemoteLocation)

}
