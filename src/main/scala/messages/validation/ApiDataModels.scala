package eu.hbp.mip.messages.validation

case class ValidationQuery(
  fold: String,
  model: String,
  data: List[String]
)

case class ValidationResult(
  fold: String,
  variableType: String,
  outputData: List[String]
)

case class ValidationError(
  message: String
)
