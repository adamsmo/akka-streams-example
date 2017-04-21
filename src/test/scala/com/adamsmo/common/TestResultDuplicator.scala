package com.adamsmo.common

import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import com.adamsmo.BaseTest
import org.specs2.specification.Scope

/**
  * Created by adam on 20.05.2016.
  */
class TestResultDuplicator extends BaseTest {

  class Context extends Scope {
    val toElements = new ResultDuplicator[Int, String](_.toString, 3)
    val flow = Flow[Int].via(toElements)
  }

  "result duplicator" should {

    "emit multiple copies of element" in new Context {

      val (source, sink) = TestSource.probe[Int]
        .via(flow)
        .toMat(TestSink.probe[String])(Keep.both)
        .run()

      sink.request(7)
      source.sendNext(1)
      source.sendNext(2)
      source.sendNext(3)
      sink.expectNext("1", "1", "1", "2", "2", "2", "3")
    }
  }
}

