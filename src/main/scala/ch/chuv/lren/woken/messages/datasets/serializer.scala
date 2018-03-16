package ch.chuv.lren.woken.messages.datasets

import akka.serialization.Serializer
import spray.json._
import datasetsProtocol._

// Message serializers for Akka

class DatasetsQuerySerializer extends Serializer {

  override def identifier: Int = 34543534

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val query        = o.asInstanceOf[DatasetsQuery]
    val bytes: Array[Byte] = query.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[DatasetsQuery].asInstanceOf[AnyRef]

}

class DatasetsResponseSerializer extends Serializer {

  override def identifier: Int = 75457545

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def toBinary(o: AnyRef): Array[Byte] = {
    val result        = o.asInstanceOf[DatasetsResponse]
    val bytes: Array[Byte] = result.toJson.compactPrint.getBytes
    bytes
  }

  override def includeManifest: Boolean = false

  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf"))
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    new String(bytes).parseJson.convertTo[DatasetsResponse].asInstanceOf[AnyRef]

}
