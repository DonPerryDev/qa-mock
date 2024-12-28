import cats.effect.{IO, IOApp}
import usecase.GetQuestions
import cats.implicits._

object Http4sEmberClientExample extends IOApp.Simple {
  val run: IO[Unit] = {

    val useCases = Map("GetQuestions" -> GetQuestions.execute)

    useCases
      .toList
      .traverse { case (name, useCase) =>
        useCase.attempt.map(result => name -> result)
      }
      .map(_.toMap)
      .flatMap(IO.println(_))
  }

}