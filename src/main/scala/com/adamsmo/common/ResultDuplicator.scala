package com.adamsmo.common

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}

/**
  * Created by adam on 20.05.2016.
  */
class ResultDuplicator[I, O](f: I => O, count: Int) extends GraphStage[FlowShape[I, O]] {

  val in = Inlet[I]("Duplicator.in")
  val out = Outlet[O]("Duplicator.out")

  override val shape = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      var last: Option[O] = None
      var counter: Int = 0

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val elem = f(grab(in))
          last = Some(elem)
          counter += 1
          push(out, elem)
        }

        override def onUpstreamFinish(): Unit = {
          if (counter < count && last.isDefined) {
            for (_ <- 1 to (count - counter)) {
              emit(out, last.get)
            }
          }
          complete(out)
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          if (counter < count && last.isDefined) {
            push(out, last.get)
            counter += 1
          } else {
            counter = 0
            pull(in)
          }
        }
      })
    }
}