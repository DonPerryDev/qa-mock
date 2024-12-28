package rest

import cats.effect.{IO, Resource}
import dto.Question
import io.circe.generic.auto.*
import io.circe.jawn
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.{Method, Request, Response, Uri}

object ClientRest {

  private val clientResource: Resource[IO, org.http4s.client.Client[IO]] =
    EmberClientBuilder.default[IO].build

  def execute(host: String, path: String, method: Method, processResponse: Response[IO] => IO[String]): IO[String] = {

    clientResource
      .use { client =>
        val url = s"$host/$path"
        Uri.fromString(url) match {
          case Right(uri) =>
            val request = Request[IO](method, uri)
            client.run(request).use(processResponse)

          case Left(error) =>
            IO(println(s"Invalid URL: $error"))
              *> IO.pure("Error")
        }
      }


  }

  private def parseQuestions(): Either[String, Seq[Question]] = {
    val questions = "[{\"question\":\"Tenés experiencia en operatoria/inversiones en\",\"answers\":[\"Ninguna\",\"Bonos\",\"Acciones\",\"Cauciones\",\"Futuros\",\"Opciones\"]},{\"question\":\"¿Qué te interesa más concretar en el mercado?\",\"answers\":[\"Invertir mis ahorros en bonos, acciones, cauciones a mediano y largo plazo con la finalidad de preservar mi capital\",\"Comprar y/o vender bonos, acciones, futuros y opciones con fines de cobertura\",\"Comprar y/o vender bonos, acciones, futuros y opciones a corto plazo\",\"Comprar y/o vender bonos, acciones, futuros y opciones en el día sin tomar posición (trading intradiario)\"]},{\"question\":\"Calificá tu situación financiera y económica actual\",\"answers\":[\"Mala\",\"Regular\",\"Buena\",\"Muy buena\",\"Óptima\"]},{\"question\":\"¿Qué porcentaje de tu capital estás dispuesto a arriesgar en inversiones u operaciones en el mercado?\",\"answers\":[\"Menos del 30%\",\"Entre el 30% y el 60%\",\"Hasta el 100%\"]},{\"question\":\"¿Cuál será su límite de fondeo de cuenta anual?\",\"answers\":[\"Menos de $10.000.000 anuales\",\"Entre $10.000.001 y $25.000.000 anuales\",\"Entre $25.000.001 y $60.000.000 anuales\",\"Más de $60.000.001 anuales\"]}]"
    jawn.decode[Seq[Question]](questions)
      .left.map(_.getMessage)
  }
}
