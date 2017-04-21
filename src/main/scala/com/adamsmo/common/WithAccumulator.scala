package com.adamsmo.common

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

/**
  * Created by adam on 20.05.2016.
  */
class WithAccumulator[I, O, ACC](initialValue: ACC, f: (I, ACC) => (O, ACC)) extends GraphStage[FlowShape[I, O]] {

  val in = Inlet[I]("WithAccumulator.in")
  val out = Outlet[O]("WithAccumulator.out")

  override val shape = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      var accumulator: ACC = initialValue

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val (e, newAccumulator) = f(grab(in), accumulator)
          accumulator = newAccumulator
          push(out, e)
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }
}

