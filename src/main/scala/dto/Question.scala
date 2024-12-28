package dto

import io.circe.generic.auto._


case class Question(question: String, answers: List[String])
