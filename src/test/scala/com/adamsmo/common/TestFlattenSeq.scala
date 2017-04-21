package com.adamsmo.common

import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import com.adamsmo.BaseTest
import org.specs2.specification.Scope

/**
  * Created by adam on 20.05.2016.
  */
class TestFlattenSeq extends BaseTest {

  class Context extends Scope {
    val toElements = new FlattenSeq[Int]()
    val flow = Flow[List[Int]].via(toElements)
  }

  "to elements" should {

    "correctly unpack list" in new Context {
      val (source, sink) = TestSource.probe[List[Int]]
        .via(flow)
        .toMat(TestSink.probe[Int])(Keep.both)
        .run()

      sink.request(6)
      source.sendNext(List())
      source.sendNext(List(1, 2))
      source.sendNext(List())
      source.sendNext(List(3))
      source.sendNext(List(1, 2, 3))
      sink.expectNext(1, 2, 3, 1, 2, 3)
    }
  }
}
