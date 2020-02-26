package com.knoldus

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.{AskTimeoutException, ask}
import akka.routing.RoundRobinPool
import scala.concurrent.Future
import scala.concurrent.duration._

object AnalysisOb extends App with Utils {

  val system = ActorSystem("AnalysisSystem")

  val props =Props[Analysis]
  implicit val timeout = Timeout(2.second)


  val list = getListOfFiles("src/main/resources/log-files")

  val actor = system.actorOf(props.withRouter(RoundRobinPool(3,supervisorStrategy = mySupervisorStrategy)).withDispatcher("fixed-thread-pool"), "myactor")
  system.scheduler.scheduleOnce(0.second,actor,ScheduleMsg("cleanFile"))

  val logAnalysisResult : File = getListOfFiles("src/main/resources/log-analysis-result").head

  val res = list.map(file => {

    (actor ? file).mapTo[FileAnalysisResult].recover {
      case exception: AskTimeoutException =>   actor ! "Fail"
        FileAnalysisResult(writeAnalysisToFile(logAnalysisResult,file.getName,-1,-1,-1), -1, -1, -1)
    }
  })

  val results = Future.sequence(res)

  system.scheduler.scheduleWithFixedDelay(2.second,300.second,actor,ScheduleMsg("displayResult"))

}
