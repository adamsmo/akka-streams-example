package com.adamsmo.common

import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import com.adamsmo.BaseTest
import org.specs2.specification.Scope

/**
  * Created by adam on 20.05.2016.
  */
class TestWithAccumulator extends BaseTest {

  class Context extends Scope {
    def f(e: Int, acc: Seq[Int]) = {
      val nAcc = acc :+ e
      ((e, nAcc.sum.toDouble / nAcc.length.toDouble), nAcc)
    }

    val toElements = new WithAccumulator[Int, (Int, Double), Seq[Int]](Seq[Int](), f)
    val flow = Flow[Int].via(toElements)
  }

  "accumulator" should {

    "correctly calculate accumulated average" in new Context {
      val (source, sink) = TestSource.probe[Int]
        .via(flow)
        .toMat(TestSink.probe[(Int, Double)])(Keep.both)
        .run()

      sink.request(3)
      source.sendNext(1)
      source.sendNext(2)
      source.sendNext(3)
      sink.expectNext((1, 1.0), (2, 1.5), (3, 2.0))
    }
  }
}