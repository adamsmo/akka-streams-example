package com.adamsmo.logic

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.adamsmo.common.Configuration
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future

/**
  * Created by adam on 18.05.2016.
  */

class Api(conf: Configuration) {

  import conf._
  import conf.system.dispatcher

  implicit val listOfUrls = listFormat[String]
  implicit val machineInfo = jsonFormat3(MachineInfo)

  def getMachines: Future[List[MachineUUID]] = Http().singleRequest(HttpRequest(uri = machinesList)).flatMap { response =>
      Unmarshal(response.entity).to[List[String]].map(_.map(_.replaceAllLiterally(machineUrlPrefix, "")).map(MachineUUID(_)))
    }

  def getMachineInfoRequest(machine: MachineUUID) = HttpRequest(uri = s"$machineInfoUrl${machine.uuid}")

  def toMachineInfo(response: HttpResponse): Future[MachineInfo] = Unmarshal(response.entity).to[MachineInfo]

}
