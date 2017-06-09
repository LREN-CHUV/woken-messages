package eu.hbp.mip.meta

case class VariableMetaData(
                     code: String,
                     label: String,
                     `type`: String,
                     methodology: Option[String],
                     units: Option[String],
                     enumerations: Option[Map[String, String]]
                   )
