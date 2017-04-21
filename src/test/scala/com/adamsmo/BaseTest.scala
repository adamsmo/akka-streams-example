package com.adamsmo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.specs2.mutable.Specification

/**
  * Created by adam on 20.05.2016.
  */
class BaseTest extends Specification {

  implicit val system = ActorSystem("test-mech-park")
  implicit val materializer = ActorMaterializer()

}
