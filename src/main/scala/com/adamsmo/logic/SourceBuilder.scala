package com.adamsmo.logic

import akka.actor.Cancellable
import akka.http.scaladsl.Http
import akka.stream.scaladsl.Source
import com.adamsmo.common._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Failure


class SourceBuilder(api: Api, conf: Configuration, msgLogic: MessagesLogic) {

  import conf._

  val logger = Logger(LoggerFactory.getLogger("reactive-streams"))

  val machineInfoRequestInterval = 5
  val machineListRequestInterval = 30
  //5 min * 60 seconds * 1/5 samples/second
  val eventsForAverage = 5 * 60 / machineInfoRequestInterval
  val requestRatio = machineListRequestInterval / machineInfoRequestInterval

  def build(): Source[FailureMessage, Cancellable] = {
    val machines = Source.tick[Unit](0 seconds, machineInfoRequestInterval seconds, Unit)
      .via(new ResultDuplicator[Unit, Future[Seq[MachineUUID]]](_ => api.getMachines, requestRatio))

    Source.tick[Unit](0 seconds, machineInfoRequestInterval seconds, Unit)
      .zip(machines).map(_._2)
      .mapAsync(conf.parallelism)(a => a)
      .via(new FlattenSeq[MachineUUID]())
      .map(uuid => api.getMachineInfoRequest(uuid) -> 1)
      .via(Http().cachedHostConnectionPool[Int](conf.host))
      .map(_._1.recoverWith {
        case e: Throwable =>
          logger.error("http request failed", e)
          e.printStackTrace(System.out)
          new Failure(e)
      })
      .map(_.toOption)
      .collect { case Some(e) => e }

      .mapAsync[MachineInfo](conf.parallelism)(api.toMachineInfo)

      .groupBy(conf.maxNumberOfMachines, _.name)
      .via(new WithAccumulator[MachineInfo, (Double, MachineInfo), Seq[MachineInfo]](Seq[MachineInfo](), averageCurrent))
      .mergeSubstreams

      .map { case (avg, machine) => msgLogic.toMessage(avg, machine) }
      .collect { case Some(e) => e }
  }

  def averageCurrent(elem: MachineInfo, acc: Seq[MachineInfo]) = {
    val newAcc = (acc :+ elem).takeRight(eventsForAverage)
    ((newAcc.map(_.current).sum / newAcc.length, elem), newAcc)
  }
}