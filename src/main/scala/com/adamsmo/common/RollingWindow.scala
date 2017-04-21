package com.adamsmo.common

import akka.stream.stage._
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}

class RollingWindow[T](maxWindowSize: Int) extends GraphStage[FlowShape[T, Seq[T]]] {

  val in = Inlet[T]("RollingWindow.in")
  val out = Outlet[Seq[T]]("RollingWindow.out")

  override val shape = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      var seq: Seq[T] = Seq()

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val elem = grab(in)
          seq = (seq :+ elem).takeRight(maxWindowSize)
          push(out, seq)
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }
}

