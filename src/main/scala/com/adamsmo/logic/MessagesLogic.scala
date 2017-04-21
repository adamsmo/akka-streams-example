package com.adamsmo.logic

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

/**
  * Created by adam on 20.05.2016.
  */
class MessagesLogic() {

  val logger = Logger(LoggerFactory.getLogger("reactive-streams"))

  def handleMessage(msg: FailureMessage) = logger.info(s"'${msg.machineName}', drawn current: ${msg.current} , " +
    s"alert current: ${msg.current_alert} , average for last 5min: ${msg.average_current}")

  def toMessage(avg: Double, machine: MachineInfo) = machine match {
    case m if m.current >= m.current_alert => Some(FailureMessage(m.name, current = m.current,
      current_alert = m.current_alert, average_current = avg))
    case _ => None
  }
}
