package com.adamsmo.logic

/**
  * Created by adam on 20.05.2016.
  */
case class FailureMessage(machineName: String, current: Double, current_alert: Double, average_current: Double)

case class MachineInfo(name: String, current: Double, current_alert: Double)

case class MachineUUID(uuid: String)
