package eu.hbp.mip.woken.messages

// Domain objects

object Ok

case class Error(message: String)

case class Validation(message: String)

// Exceptions

case object ChronosNotReachableException extends Exception("Cannot connect to Chronos")
