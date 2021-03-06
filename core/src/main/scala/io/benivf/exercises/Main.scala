package io.benivf.exercises

import scala.concurrent.duration._

import cats.effect.IOApp
import cats.effect._
import cats.syntax.all._

object Main extends IOApp {
  val max = 10
  val permits = 2
  import core._

  private def timeFor(id: Int): FiniteDuration = 1.second + id.millis

  def run(args: List[String]): IO[Int] =
    putStrLn(s"Create semaphore") >>
      Semaphore(permits) >>= { s =>
      parTraverse((1 to max).toList) { x =>
        s.acquire.bracket { _ =>
          putStrLn(s"Acquired $x") >>
            simulateTask(x, timeFor(x))
        } { _ =>
          s.release >>
            putStrLn(s"Released $x")
        }
      }
    } >>= { result =>
      putStrLn(s"Done! [${result.mkString(",")}]").as(0)
    }

}
