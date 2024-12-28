package usecase

import cats.effect.IO
import dto.Question
import io.circe.generic.auto.*
import io.circe.jawn
import org.http4s.Method.GET
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.dsl.io.*
import org.http4s.{Method, Request, Response, Uri}
import rest.ClientRest
import org.http4s.EntityDecoder.text

object GetQuestions {

  private val host = "http://127.0.0.1:3000"
  private val path = "kyc/questions"
  private val url = s"$host/$path"


  def execute: IO[String] = {
    ClientRest.execute(host, path, GET, expected)
  }

  private def expected(res: Response[IO]): IO[String] = {

    parseQuestions().fold(
      error => IO(println(s"Error parsing questions: $error")) *> IO.pure("Error"),
      questions => {

        Uri.fromString(url)
          .fold(
            error => IO(println(s"Invalid URL: $error")) *> IO.pure("Error"),
            uri => {
              val request = Request[IO](Method.GET, uri)
              res.status.code match {
                case code if code >= 200 && code < 300 =>
                  res.as[Seq[Question]].flatMap { body =>
                    val res = questions == body
                    IO(println(s"Success [$code]: $res"))
                      *> IO.pure("Success")
                  }
                case code =>
                  res.as[String].flatMap{ body =>
                      IO(println(s"Error [$code]: $body"))
                        .map(_ => body)
                  }
              }
            }
          )
      }
    )
  }


  private def parseQuestions(): Either[String, Seq[Question]] = {
    val questions =
      """[
        |   {
        |      "question":"Tenés experiencia en operatoria/inversiones en",
        |      "answers":[
        |         "Ninguna",
        |         "Bonos",
        |         "Acciones",
        |         "Cauciones",
        |         "Futuros",
        |         "Opciones"
        |      ]
        |   },
        |   {
        |      "question":"¿Qué te interesa más concretar en el mercado?",
        |      "answers":[
        |         "Invertir mis ahorros en bonos, acciones, cauciones a mediano y largo plazo con la finalidad de preservar mi capital",
        |         "Comprar y/o vender bonos, acciones, futuros y opciones con fines de cobertura",
        |         "Comprar y/o vender bonos, acciones, futuros y opciones a corto plazo",
        |         "Comprar y/o vender bonos, acciones, futuros y opciones en el día sin tomar posición (trading intradiario)"
        |      ]
        |   },
        |   {
        |      "question":"Calificá tu situación financiera y económica actual",
        |      "answers":[
        |         "Mala",
        |         "Regular",
        |         "Buena",
        |         "Muy buena",
        |         "Óptima"
        |      ]
        |   },
        |   {
        |      "question":"¿Qué porcentaje de tu capital estás dispuesto a arriesgar en inversiones u operaciones en el mercado?",
        |      "answers":[
        |         "Menos del 30%",
        |         "Entre el 30% y el 60%",
        |         "Hasta el 100%"
        |      ]
        |   },
        |   {
        |      "question":"¿Cuál será su límite de fondeo de cuenta anual?",
        |      "answers":[
        |         "Menos de $10.000.000 anuales",
        |         "Entre $10.000.001 y $25.000.000 anuales",
        |         "Entre $25.000.001 y $60.000.000 anuales",
        |         "Más de $60.000.001 anuales"
        |      ]
        |   }
        | ]""".stripMargin
    jawn.decode[Seq[Question]](questions)
      .left.map(_.getMessage)
  }
}

