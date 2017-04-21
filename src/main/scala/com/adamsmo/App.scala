package com.adamsmo

import akka.stream.scaladsl.Sink
import com.adamsmo.common.Configuration
import com.adamsmo.logic.{Api, MessagesLogic, SourceBuilder}

/**
  * Created by adam on 19.05.2016.
  */
object App {
  def main(args: Array[String]) {
    val conf = new Configuration

    import conf._

    val api = new Api(conf)
    val msgLogic = new MessagesLogic

    new SourceBuilder(api, conf, msgLogic).build()
      .runWith(Sink.foreach(msgLogic.handleMessage))
  }
}
