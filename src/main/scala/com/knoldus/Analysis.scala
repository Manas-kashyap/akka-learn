package com.knoldus


import java.io.File
import akka.actor.{Actor, ActorLogging}
import akka.pattern.{AskTimeoutException}
import scala.io.Source




class Analysis extends Actor with Utils with ActorLogging {

  val logAnalysisResult : File = getListOfFiles("src/main/resources/log-analysis-result").head

  override def receive: Receive = {
    case file: File => log.info("passed : " + self.path)
      sender ! analyseFile(file)
    case "Fail" =>  log.info("failed : " + self.path)
      throw new AskTimeoutException("AskTimeoutException due to overflow of mailbox")
    case scheduleMsg : ScheduleMsg if scheduleMsg.msg.equalsIgnoreCase("displayResult") => log.info("\n\n\n" + displayResult.toString)
    case scheduleMsg : ScheduleMsg if scheduleMsg.msg.equalsIgnoreCase("cleanFile") => cleanFile
  }

  def cleanFile : Boolean = {
    import java.io._
    try{
      val bw = new BufferedWriter(new FileWriter(logAnalysisResult))
      bw.write("")
      bw.close()
      true
    }catch {
      case _ : FileNotFoundException => false
    }
  }

  def analyseFile(file: File): FileAnalysisResult = {
    val fileContent = Source.fromFile(file).getLines.toList
    val res = fileContent.foldLeft((0, 0, 0)) { (acc, line) => {
      if (line.contains("ERROR")) {
        (acc._1 + 1, acc._2, acc._3)
      }
      else if (line.contains("WARN")) {
        (acc._1, acc._2 + 1, acc._3)
      }
      else if (line.contains("INFO")) {
        (acc._1, acc._2, acc._3 + 1)
      }
      else {
        acc
      }
    }
    }

    FileAnalysisResult(writeAnalysisToFile(logAnalysisResult,file.getName, res._1, res._2, res._3), res._1, res._2, res._3)
  }

  def displayResult : String = {
    val fileContent = Source.fromFile(logAnalysisResult).getLines.mkString("\n")
    fileContent
  }
}
