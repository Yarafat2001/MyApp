package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ThreatLevel(val label: String, val levelNumber: Int) {
  NORMAL("GUARD NORMAL (ALPHA)", 1),
  ELEVATED("ELEVATED CAUTION (BRAVO)", 2),
  HIGH("HIGH ALERT (CHARLIE)", 3),
  LOCKDOWN("FACILITY LOCKDOWN (DELTA)", 4)
}

enum class TriggerType(val displayName: String, val icon: String) {
  TIMER("Scheduled Timer", "timer"),
  SENSOR("IoT Sensor / Motion", "sensors"),
  GEOFENCE("Geofence / Perimeter", "location_on"),
  BADGE("Access / Badge Swipe", "badge"),
  CHECKPOINT_DELAY("Missed Checkpoint", "warning"),
  LONE_WORKER_INACTIVITY("Lone-Worker Timeout", "health_and_safety"),
  MANUAL("Manual Emergency Trigger", "touch_app")
}

enum class ActionType(val displayName: String, val severityLevel: String) {
  ALERT_SUPERVISOR("Notify Dispatch / Command", "High"),
  STROBE_LIGHTS("Activate Sector Floodlights", "Medium"),
  LOCKDOWN_GATE("Remote Pulse Gate Lockdown", "Critical"),
  AUTO_DISPATCH("Auto-Dispatch Backup Officer", "Critical"),
  SOUND_ALARM("Sound High-Decibel Siren", "High"),
  LOG_EVENT("Auto-Log Incident in SecOps", "Low")
}

enum class IncidentPriority(val label: String, val tag: String) {
  CRITICAL("P1 - CRITICAL", "P1"),
  HIGH("P2 - HIGH", "P2"),
  MEDIUM("P3 - MEDIUM", "P3"),
  LOW("P4 - LOW", "P4")
}

enum class IncidentType(val title: String) {
  INTRUSION("Perimeter Breach / Intrusion"),
  FIRE_SMOKE("Fire / Smoke Alarm Triggered"),
  MEDICAL("Officer or Personnel Medical SOS"),
  ACCESS_VIOLATION("Forced Door / Tamper Alarm"),
  HAZARD("Chemical / Electrical Infrastructure Hazard"),
  SUSPICIOUS("Suspicious Vehicle / Drone Sighting")
}

enum class IncidentStatus {
  OPEN,
  RESPONDING,
  CONTAINED,
  RESOLVED
}

enum class GateClearanceStatus {
  APPROVED,
  DENIED,
  FLAGGED_REVOKED
}

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val triggerType: TriggerType,
  val conditionDesc: String,
  val actionType: ActionType,
  val isEnabled: Boolean = true,
  val executionCount: Int = 0,
  val lastTriggeredEpochMs: Long? = null,
  val sector: String = "All Sectors"
)

@Entity(tableName = "checkpoints")
data class CheckpointEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val routeId: String,
  val routeName: String,
  val name: String,
  val locationCode: String,
  val sequenceOrder: Int,
  val isCompleted: Boolean = false,
  val completedAtEpochMs: Long? = null,
  val targetIntervalMins: Int = 10,
  val nfcTagId: String,
  val qrCode: String,
  val checklistItemsRaw: String, // Comma separated checklist tasks
  val notes: String = "",
  val latitude: Double = 37.7749,
  val longitude: Double = -122.4194
)

@Entity(tableName = "incidents")
data class IncidentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val type: IncidentType,
  val priority: IncidentPriority,
  val status: IncidentStatus = IncidentStatus.OPEN,
  val reportedAtEpochMs: Long = System.currentTimeMillis(),
  val location: String,
  val notes: String = "",
  val sopStepsRaw: String, // Pipe or comma separated standard operating procedures
  val completedStepsCount: Int = 0,
  val autoDispatchedOfficer: String = "Agent Sentinel-4"
)

@Entity(tableName = "access_logs")
data class AccessLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val badgeId: String,
  val visitorName: String,
  val company: String,
  val clearanceLevel: String,
  val vehiclePlate: String,
  val gateName: String,
  val timestampEpochMs: Long = System.currentTimeMillis(),
  val status: GateClearanceStatus = GateClearanceStatus.APPROVED,
  val automatedReason: String = "Clearance credentials verified via SecNet"
)

@Entity(tableName = "automation_logs")
data class AutomationLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val ruleTitle: String,
  val triggerDesc: String,
  val actionExecuted: String,
  val timestampEpochMs: Long = System.currentTimeMillis(),
  val status: String = "SUCCESS",
  val details: String = ""
)

data class LoneWorkerStatus(
  val isActive: Boolean = true,
  val totalCountdownSeconds: Int = 900, // 15 mins
  val remainingSeconds: Int = 900,
  val isAlarmStage: Boolean = false,
  val isExpired: Boolean = false,
  val lastHeartbeatEpochMs: Long = System.currentTimeMillis()
)

data class GateController(
  val gateId: String,
  val name: String,
  val isLocked: Boolean,
  val lastAutomatedCycle: Long,
  val cameraOnline: Boolean = true,
  val automatedPassCount: Int = 42
)

enum class MessageSender {
  USER,
  SENTINEL_AI
}

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val sender: MessageSender,
  val text: String,
  val timestampEpochMs: Long = System.currentTimeMillis(),
  val isStreaming: Boolean = false,
  val isError: Boolean = false
)

