package com.adamsmo.common

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

/**
  * Created by adam on 18.05.2016.
  */
class Configuration {

  implicit val system = ActorSystem("mech-park")
  implicit val materializer = ActorMaterializer()

  val host = "machinepark.actyx.io"
  val machinesList = s"http://$host/api/v1/machines"
  val machineUrlPrefix = "$API_ROOT/machine/"
  val machineInfoUrl = s"http://$host/api/v1/machine/"

  val maxNumberOfMachines = 1000
  val parallelism = 10
}
