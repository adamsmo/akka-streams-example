package com.adamsmo.common

import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import com.adamsmo.BaseTest
import org.specs2.specification.Scope

/**
  * Created by adam on 18.05.2016.
  */


class TestRollingWindow extends BaseTest {

  class Context extends Scope {
    val rollingWindow = new RollingWindow[Int](2)
    val flow = Flow[Int].via(rollingWindow)
  }

  "rolling window" should {

    "correctly group elements" in new Context {
      val (source, sink) = TestSource.probe[Int]
        .via(flow)
        .toMat(TestSink.probe[Seq[Int]])(Keep.both)
        .run()

      sink.request(3)
      source.sendNext(1)
      source.sendNext(2)
      source.sendNext(3)
      sink.expectNext(Seq(1), Seq(1, 2), Seq(2, 3))
    }
  }
}