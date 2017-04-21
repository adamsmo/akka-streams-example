package com.adamsmo.common

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

/**
  * Created by adam on 20.05.2016.
  */
class FlattenSeq[T]() extends GraphStage[FlowShape[Seq[T], T]] {

  val in = Inlet[Seq[T]]("ToElements.in")
  val out = Outlet[T]("ToElements.out")

  override val shape = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      var seq: Seq[T] = Seq()

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          seq ++= grab(in)
          if (seq.nonEmpty) {
            push(out, seq.head)
            seq = seq.tail
          } else {
            pull(in)
          }
        }

        override def onUpstreamFinish(): Unit = {
          if (seq.nonEmpty) seq.foreach(emit(out, _))
          complete(out)
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          if (seq.nonEmpty) {
            push(out, seq.head)
            seq = seq.tail
          } else {
            pull(in)
          }
        }
      })
    }
}
