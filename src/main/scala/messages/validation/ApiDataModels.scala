package eu.hbp.mip.messages.validation

import eu.hbp.mip.meta.VariableMetaData

case class ValidationQuery(
  fold: String,
  model: String,
  data: List[String],
  varInfo: VariableMetaData
)

case class ValidationResult(
  fold: String,
  varInfo: VariableMetaData,
  outputData: List[String]
)

case class ValidationError(
  message: String
)
