package com.adamsmo.logic

import com.adamsmo.BaseTest
import org.specs2.specification.Scope

/**
  * Created by adam on 18.05.2016.
  */

class TestMessagesLogic extends BaseTest {

  class Context extends Scope {
    val msgLogic = new MessagesLogic()
    val avg = 26.52
    val machineInfo = MachineInfo(name = "Trumpf TruLaser Cell 7000 [#08]", current = 26.52, current_alert = 24.0)
  }

  "graph" should {

    "produce message for alert current" in new Context {
      val result = msgLogic.toMessage(avg, machineInfo)
      result mustEqual Some(FailureMessage(machineInfo.name, machineInfo.current, machineInfo.current_alert, avg))
    }

    "produce message for alert current" in new Context {
      val result = msgLogic.toMessage(avg, machineInfo.copy(current_alert = 50.0))
      result mustEqual None
    }
  }
}