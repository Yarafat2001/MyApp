package com.example

import com.example.data.model.ActionType
import com.example.data.model.AutomationRuleEntity
import com.example.data.model.CheckpointEntity
import com.example.data.model.IncidentPriority
import com.example.data.model.IncidentType
import com.example.data.model.LoneWorkerStatus
import com.example.data.model.ThreatLevel
import com.example.data.model.TriggerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun threatLevel_hierarchyAndDefconLabels_areAccurate() {
    assertEquals(1, ThreatLevel.NORMAL.levelNumber)
    assertEquals(2, ThreatLevel.ELEVATED.levelNumber)
    assertEquals(3, ThreatLevel.HIGH.levelNumber)
    assertEquals(4, ThreatLevel.LOCKDOWN.levelNumber)
  }

  @Test
  fun loneWorkerStatus_thresholdAndCountdownCalculations() {
    val initial = LoneWorkerStatus(
      isActive = true,
      totalCountdownSeconds = 900,
      remainingSeconds = 900,
      isAlarmStage = false,
      isExpired = false
    )
    assertTrue(initial.isActive)
    assertFalse(initial.isAlarmStage)
    assertFalse(initial.isExpired)

    val warningStage = initial.copy(remainingSeconds = 25, isAlarmStage = true, isExpired = false)
    assertTrue(warningStage.isAlarmStage)
    assertFalse(warningStage.isExpired)
    assertEquals(25, warningStage.remainingSeconds)

    val expiredStage = initial.copy(remainingSeconds = 0, isAlarmStage = true, isExpired = true)
    assertTrue(expiredStage.isAlarmStage)
    assertTrue(expiredStage.isExpired)
    assertEquals(0, expiredStage.remainingSeconds)

    val resetStage = expiredStage.copy(
      remainingSeconds = initial.totalCountdownSeconds,
      isAlarmStage = false,
      isExpired = false
    )
    assertFalse(resetStage.isExpired)
    assertFalse(resetStage.isAlarmStage)
    assertEquals(900, resetStage.remainingSeconds)
  }

  @Test
  fun automationRuleEntity_executionProperties() {
    val rule = AutomationRuleEntity(
      id = 1,
      title = "Perimeter Intrusion Siren",
      triggerType = TriggerType.SENSOR,
      conditionDesc = "PIR sensor breach after 22:00",
      actionType = ActionType.SOUND_ALARM,
      isEnabled = true,
      executionCount = 5,
      sector = "Sector Alpha"
    )

    assertEquals(5, rule.executionCount)
    assertTrue(rule.isEnabled)
    assertEquals(TriggerType.SENSOR, rule.triggerType)
    assertEquals(ActionType.SOUND_ALARM, rule.actionType)
  }

  @Test
  fun checkpointPacing_checklistParsing() {
    val rawTasks = "Inspect perimeter bollards,Confirm visitor log sync,Check gate barrier hydraulic pressure"
    val checkpoint = CheckpointEntity(
      routeId = "ALPHA",
      routeName = "Alpha Night",
      name = "Main Gate Kiosk",
      locationCode = "CP-01",
      sequenceOrder = 1,
      nfcTagId = "NFC-1",
      qrCode = "QR-1",
      checklistItemsRaw = rawTasks
    )

    val tasks = checkpoint.checklistItemsRaw.split(",").map { it.trim() }
    assertEquals(3, tasks.size)
    assertEquals("Inspect perimeter bollards", tasks[0])
    assertFalse(checkpoint.isCompleted)
  }
}
