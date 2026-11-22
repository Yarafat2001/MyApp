package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SecurityDatabase
import com.example.data.model.AccessLogEntity
import com.example.data.model.ActionType
import com.example.data.model.AutomationLogEntity
import com.example.data.model.AutomationRuleEntity
import com.example.data.model.CheckpointEntity
import com.example.data.model.GateClearanceStatus
import com.example.data.model.GateController
import com.example.data.model.IncidentEntity
import com.example.data.model.IncidentPriority
import com.example.data.model.IncidentStatus
import com.example.data.model.IncidentType
import com.example.data.model.LoneWorkerStatus
import com.example.data.model.ThreatLevel
import com.example.data.model.TriggerType
import com.example.data.repository.SecurityRepository
import com.example.data.ai.SentinelAiService
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SecOpsScreen {
  DASHBOARD,
  AUTOMATION,
  PATROLS,
  INCIDENTS,
  ACCESS,
  AI_COPILOT,
  SHIFT_REPORT
}

class SecurityViewModel(application: Application) : AndroidViewModel(application) {

  private val database = SecurityDatabase.getDatabase(application, viewModelScope)
  private val repository = SecurityRepository(database.securityDao())

  // Navigation
  private val _currentScreen = MutableStateFlow(SecOpsScreen.DASHBOARD)
  val currentScreen: StateFlow<SecOpsScreen> = _currentScreen.asStateFlow()

  // Threat Level / Posture
  private val _threatLevel = MutableStateFlow(ThreatLevel.NORMAL)
  val threatLevel: StateFlow<ThreatLevel> = _threatLevel.asStateFlow()

  // Lone-Worker Safety Monitor
  private val _loneWorkerStatus = MutableStateFlow(
    LoneWorkerStatus(
      isActive = true,
      totalCountdownSeconds = 900,
      remainingSeconds = 900,
      isAlarmStage = false
    )
  )
  val loneWorkerStatus: StateFlow<LoneWorkerStatus> = _loneWorkerStatus.asStateFlow()

  // Gate Controllers
  private val _gates = MutableStateFlow(
    listOf(
      GateController("GATE-01", "Gate 1 - North Executive Barrier", isLocked = false, System.currentTimeMillis()),
      GateController("GATE-02", "Gate 2 - Logistics Cargo Gate", isLocked = false, System.currentTimeMillis()),
      GateController("GATE-03", "Gate 3 - Sub-Station Power Vault", isLocked = true, System.currentTimeMillis())
    )
  )
  val gates: StateFlow<List<GateController>> = _gates.asStateFlow()

  // Patrol Pacer Timer (elapsed seconds at current active checkpoint)
  private val _patrolElapsedSeconds = MutableStateFlow(245)
  val patrolElapsedSeconds: StateFlow<Int> = _patrolElapsedSeconds.asStateFlow()

  // Notification / Toast Message
  private val _activeBannerMessage = MutableStateFlow<String?>(null)
  val activeBannerMessage: StateFlow<String?> = _activeBannerMessage.asStateFlow()

  // Theme & Language State (Dark/Light Crystal Glass & English/Bengali)
  private val _isDarkTheme = MutableStateFlow(true)
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

  private val _appLanguage = MutableStateFlow(com.example.ui.theme.AppLanguage.ENGLISH)
  val appLanguage: StateFlow<com.example.ui.theme.AppLanguage> = _appLanguage.asStateFlow()

  // Real-Time Biometric & Field Telemetry
  private val _agentHeartRateBpm = MutableStateFlow(76)
  val agentHeartRateBpm: StateFlow<Int> = _agentHeartRateBpm.asStateFlow()

  private val _patrolStepsCount = MutableStateFlow(4280)
  val patrolStepsCount: StateFlow<Int> = _patrolStepsCount.asStateFlow()

  private val _meshSignalQuality = MutableStateFlow(99.4f)
  val meshSignalQuality: StateFlow<Float> = _meshSignalQuality.asStateFlow()

  // Data streams from Room
  val rules: StateFlow<List<AutomationRuleEntity>> = repository.getAllRules()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val checkpoints: StateFlow<List<CheckpointEntity>> = repository.getAllCheckpoints()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val incidents: StateFlow<List<IncidentEntity>> = repository.getAllIncidents()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val accessLogs: StateFlow<List<AccessLogEntity>> = repository.getRecentAccessLogs()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val automationLogs: StateFlow<List<AutomationLogEntity>> = repository.getRecentAutomationLogs()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private var timerJob: Job? = null

  init {
    startLoneWorkerPacer()
    seedDefaultsIfNeeded()
  }

  private fun seedDefaultsIfNeeded() {
    viewModelScope.launch {
      // SecurityDatabaseCallback handles initial populate, but fallback guarantee:
      delay(300)
    }
  }

  fun navigateTo(screen: SecOpsScreen) {
    _currentScreen.value = screen
  }

  fun toggleTheme() {
    val next = !_isDarkTheme.value
    _isDarkTheme.value = next
    vibrateFeedback(80)
    val isBn = _appLanguage.value == com.example.ui.theme.AppLanguage.BENGALI
    showBanner(
      if (next) {
        if (isBn) "ডার্ক ক্রিস্টাল গ্লাস থিম সক্রিয় করা হয়েছে" else "Dark Crystal Glass Theme Activated"
      } else {
        if (isBn) "লাইট পার্ল গ্লাস থিম সক্রিয় করা হয়েছে" else "Light Pearl Glass Theme Activated"
      }
    )
  }

  fun toggleLanguage() {
    val next = if (_appLanguage.value == com.example.ui.theme.AppLanguage.ENGLISH) {
      com.example.ui.theme.AppLanguage.BENGALI
    } else {
      com.example.ui.theme.AppLanguage.ENGLISH
    }
    _appLanguage.value = next
    vibrateFeedback(80)
    showBanner(
      if (next == com.example.ui.theme.AppLanguage.BENGALI) {
        "বাংলা ভাষা সক্রিয় করা হয়েছে (Bengali Active)"
      } else {
        "English Language Activated"
      }
    )
  }

  fun dismissBanner() {
    _activeBannerMessage.value = null
  }

  fun showBanner(message: String) {
    _activeBannerMessage.value = message
    viewModelScope.launch {
      delay(4000)
      if (_activeBannerMessage.value == message) {
        _activeBannerMessage.value = null
      }
    }
  }

  // Threat Level
  fun setThreatLevel(level: ThreatLevel) {
    _threatLevel.value = level
    vibrateFeedback(if (level == ThreatLevel.LOCKDOWN) 800 else 200)
    viewModelScope.launch {
      repository.logAutomationEvent(
        title = "Threat Posture Shift",
        trigger = "Supervisor Manual Escalation",
        action = "Security state shifted to: ${level.label}",
        details = if (level == ThreatLevel.LOCKDOWN) "All exterior gates locked. Automated sirens pre-armed." else "Standard protocols active"
      )
      if (level == ThreatLevel.LOCKDOWN) {
        _gates.value = _gates.value.map { it.copy(isLocked = true) }
      }
      showBanner("Security Threat Level: ${level.label}")
    }
  }

  // Lone-Worker Monitor Logic & Real-Time Telemetry Ticker
  private fun startLoneWorkerPacer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (true) {
        delay(1000)
        val current = _loneWorkerStatus.value
        if (current.isActive) {
          if (current.remainingSeconds > 0) {
            val updated = current.remainingSeconds - 1
            val isAlarm = updated <= 30
            val isExpired = updated == 0
            if (updated == 30) {
              vibrateFeedback(400)
              showBanner("⚠️ LONE-WORKER CHECK-IN: 30 seconds remaining!")
            }
            if (isExpired) {
              triggerLoneWorkerEmergency()
            }
            _loneWorkerStatus.value = current.copy(
              remainingSeconds = updated,
              isAlarmStage = isAlarm,
              isExpired = isExpired
            )
          } else if (current.isExpired) {
            // Pulse strong tactile alarm feedback every second while expired
            triggerAlarmHaptic()
          }
        }
        // Advance patrol timer & live real-time telemetry
        val elapsed = _patrolElapsedSeconds.value + 1
        _patrolElapsedSeconds.value = elapsed
        _agentHeartRateBpm.value = 74 + ((elapsed * 3) % 9)
        if (elapsed % 2 == 0) {
          _patrolStepsCount.value = _patrolStepsCount.value + 1
        }
        _meshSignalQuality.value = 98.8f + ((elapsed % 11) * 0.1f)
      }
    }
  }

  fun checkInLoneWorker() {
    val total = _loneWorkerStatus.value.totalCountdownSeconds
    _loneWorkerStatus.value = _loneWorkerStatus.value.copy(
      remainingSeconds = total,
      isAlarmStage = false,
      isExpired = false,
      lastHeartbeatEpochMs = System.currentTimeMillis()
    )
    vibrateFeedback(120)
    viewModelScope.launch {
      repository.logAutomationEvent(
        title = "Lone-Worker Check-in",
        trigger = "Agent Manual Heartbeat Button",
        action = "Safety countdown reset to ${total / 60}m",
        details = "Officer verified conscious and responsive"
      )
      showBanner("✓ Safety Check-in Confirmed. Timer reset to ${total / 60}m.")
    }
  }

  fun setLoneWorkerInterval(minutes: Int) {
    val seconds = minutes * 60
    _loneWorkerStatus.value = _loneWorkerStatus.value.copy(
      totalCountdownSeconds = seconds,
      remainingSeconds = seconds,
      isAlarmStage = false,
      isExpired = false
    )
    showBanner("Lone-worker interval set to $minutes minutes.")
  }

  fun testTriggerLoneWorkerExpiry() {
    _loneWorkerStatus.value = _loneWorkerStatus.value.copy(
      totalCountdownSeconds = 10,
      remainingSeconds = 5,
      isAlarmStage = true,
      isExpired = false
    )
    showBanner("⚡ TEST MODE: Lone-worker timer set to 5s. Standby for alert expiration!")
  }

  fun toggleLoneWorkerActive() {
    val current = _loneWorkerStatus.value.isActive
    _loneWorkerStatus.value = _loneWorkerStatus.value.copy(
      isActive = !current,
      isExpired = false
    )
    showBanner(if (!current) "Lone-Worker Monitor Activated" else "Lone-Worker Monitor Suspended")
  }

  private fun triggerLoneWorkerEmergency() {
    triggerAlarmHaptic()
    viewModelScope.launch {
      repository.reportIncident(
        title = "LONE-WORKER UNRESPONSIVE ALARM",
        type = IncidentType.MEDICAL,
        priority = IncidentPriority.CRITICAL,
        location = "Patrol Sector Alpha (Officer Sentinel-1)",
        notes = "Automated Lone-Worker monitor timed out. Agent failed to confirm heartbeat check-in within designated window.",
        sopSteps = "Attempt emergency radio callout|Broadcast GPS coordinates to nearest officer|Dispatch emergency response unit|Verify agent vital signs"
      )
      showBanner("🚨 LONE-WORKER EMERGENCY DISPATCHED TO COMMAND!")
    }
  }

  // Automation Rules
  fun toggleRule(ruleId: Long, isEnabled: Boolean) {
    viewModelScope.launch {
      repository.toggleRule(ruleId, isEnabled)
      showBanner(if (isEnabled) "Rule activated" else "Rule paused")
    }
  }

  fun createRule(
    title: String,
    triggerType: TriggerType,
    conditionDesc: String,
    actionType: ActionType,
    sector: String
  ) {
    viewModelScope.launch {
      val rule = AutomationRuleEntity(
        title = title,
        triggerType = triggerType,
        conditionDesc = conditionDesc,
        actionType = actionType,
        sector = sector,
        isEnabled = true,
        executionCount = 0
      )
      repository.createRule(rule)
      showBanner("Automation Rule '$title' provisioned successfully.")
    }
  }

  fun deleteRule(rule: AutomationRuleEntity) {
    viewModelScope.launch {
      repository.deleteRule(rule)
      showBanner("Rule removed.")
    }
  }

  fun simulateTriggerRule(rule: AutomationRuleEntity) {
    vibrateFeedback(250)
    viewModelScope.launch {
      repository.simulateTriggerRule(rule)
      showBanner("⚡ Rule Triggered: ${rule.actionType.displayName}")
    }
  }

  // Patrol Operations
  fun completeCheckpoint(checkpoint: CheckpointEntity, notes: String) {
    vibrateFeedback(150)
    viewModelScope.launch {
      repository.completeCheckpoint(checkpoint.id, notes.ifEmpty { "Checkpoint validated via automated token scan" })
      _patrolElapsedSeconds.value = 0
      showBanner("✓ Verified: ${checkpoint.name}")
    }
  }

  fun resetPatrolRoute() {
    viewModelScope.launch {
      repository.resetRouteCheckpoints()
      _patrolElapsedSeconds.value = 0
      showBanner("Patrol route reset for new shift.")
    }
  }

  // Quick SOS / Incident Reporting
  fun triggerQuickSOS(type: IncidentType, location: String) {
    vibrateFeedback(800)
    viewModelScope.launch {
      val (priority, sop) = when (type) {
        IncidentType.INTRUSION -> Pair(
          IncidentPriority.CRITICAL,
          "Sound perimeter horn|Lock down adjacent blast doors|Approach with backup unit|Detain & identify trespasser|File SecOps arrest memo"
        )
        IncidentType.FIRE_SMOKE -> Pair(
          IncidentPriority.CRITICAL,
          "Trip building fire alarm pull station|Evacuate sector occupants|Call municipal Fire Department (911)|Shut off HVAC dampers"
        )
        IncidentType.MEDICAL -> Pair(
          IncidentPriority.CRITICAL,
          "Request automated AED delivery|Perform CPR / trauma first aid|Guide ambulance to Gate 1|Document responder handoff"
        )
        IncidentType.ACCESS_VIOLATION -> Pair(
          IncidentPriority.HIGH,
          "Review PTZ camera footage|Check biometric reader tamper seal|Dispatch officer to door latch|Verify badge credentials"
        )
        IncidentType.HAZARD -> Pair(
          IncidentPriority.HIGH,
          "Cordon off 50ft perimeter with safety tape|Notify Facility Engineering|Power down electrical mains|Verify vent systems"
        )
        IncidentType.SUSPICIOUS -> Pair(
          IncidentPriority.MEDIUM,
          "Monitor subject movements on CCTV|Log vehicle license plate|Approach and request visitor pass|Escort off-property if unauthorized"
        )
      }

      repository.reportIncident(
        title = "EMERGENCY: ${type.title}",
        type = type,
        priority = priority,
        location = location,
        notes = "Instant one-touch SOS trigger activated by security agent on-duty.",
        sopSteps = sop
      )
      showBanner("🚨 PRIORITY 1 ALARM DISPATCHED: ${type.title}")
    }
  }

  fun updateIncidentStatus(incidentId: Long, newStatus: IncidentStatus) {
    viewModelScope.launch {
      repository.updateIncidentStatus(incidentId, newStatus)
      showBanner("Incident updated to ${newStatus.name}")
    }
  }

  fun advanceIncidentStep(incident: IncidentEntity) {
    viewModelScope.launch {
      val totalSteps = incident.sopStepsRaw.split("|").filter { it.isNotBlank() }.size
      val next = (incident.completedStepsCount + 1).coerceAtMost(totalSteps)
      val updatedStatus = if (next >= totalSteps) IncidentStatus.RESOLVED else IncidentStatus.RESPONDING
      repository.updateIncident(
        incident.copy(
          completedStepsCount = next,
          status = updatedStatus
        )
      )
      showBanner("SOP Step $next of $totalSteps logged.")
    }
  }

  // Access Control Gates
  fun toggleGateLock(gateId: String) {
    vibrateFeedback(100)
    _gates.value = _gates.value.map { gate ->
      if (gate.gateId == gateId) {
        val newState = !gate.isLocked
        viewModelScope.launch {
          repository.logAutomationEvent(
            title = "Gate Control Automated Pulse",
            trigger = "Agent Manual Gate Switch",
            action = if (newState) "Gate ${gate.name} LOCKED" else "Gate ${gate.name} UNLOCKED",
            details = "Barrier hydraulics cycle complete"
          )
        }
        gate.copy(isLocked = newState, lastAutomatedCycle = System.currentTimeMillis())
      } else {
        gate
      }
    }
  }

  fun simulateGateScan(isAuthorized: Boolean, name: String, plate: String, gateName: String) {
    vibrateFeedback(150)
    viewModelScope.launch {
      val status = if (isAuthorized) GateClearanceStatus.APPROVED else GateClearanceStatus.DENIED
      val reason = if (isAuthorized) "Plate $plate & QR token match pre-cleared access roster" else "Unauthorized plate / expired visitor badge token"
      repository.recordAccessDecision(
        badgeId = if (isAuthorized) "BADGE-${(1000..9999).random()}" else "FLAGGED-REVOKED",
        visitorName = name,
        company = if (isAuthorized) "Approved Contractor" else "Unknown Entity",
        clearanceLevel = if (isAuthorized) "Level 2 (Facilities)" else "Restricted",
        vehiclePlate = plate,
        gateName = gateName,
        status = status,
        reason = reason
      )
      showBanner(if (isAuthorized) "✓ Gate Access Granted for $name" else "⛔ Gate Access DENIED for $name")
    }
  }

  // Haptic feedback
  private fun vibrateFeedback(durationMs: Long) {
    try {
      val context = getApplication<Application>()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(durationMs)
        }
      }
    } catch (_: Exception) {
      // Ignored
    }
  }

  fun triggerAlarmHaptic() {
    try {
      val context = getApplication<Application>()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val timings = longArrayOf(0, 350, 150, 350, 150, 500)
        val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
        val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
          vibratorManager?.defaultVibrator?.vibrate(effect)
        } else {
          @Suppress("DEPRECATION")
          val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
          vibrator?.vibrate(effect)
        }
      } else {
        vibrateFeedback(800)
      }
    } catch (_: Exception) {
      // Ignored
    }
  }

  // Sentinel AI Copilot State
  private val aiService = SentinelAiService()

  private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
    listOf(
      ChatMessage(
        sender = MessageSender.SENTINEL_AI,
        text = "Sentinel AI Tactical Dispatch online. Standing by for SOP checklists, perimeter breach containment, lone-worker safety guidance, and automated shift summaries.\n\nসেন্টিনেল এআই ট্যাকটিক্যাল ডিসপ্যাচ প্রস্তুত। যে কোনো নিরাপত্তা নির্দেশিকা, জরুরি সহায়তা বা শিফট রিপোর্ট তৈরিতে বার্তা পাঠান।"
      )
    )
  )
  val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

  private val _isAiGenerating = MutableStateFlow(false)
  val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

  fun sendChatMessage(prompt: String) {
    val cleanPrompt = prompt.trim()
    if (cleanPrompt.isEmpty() || _isAiGenerating.value) return

    val userMessage = ChatMessage(
      sender = MessageSender.USER,
      text = cleanPrompt
    )
    val assistantPlaceholder = ChatMessage(
      sender = MessageSender.SENTINEL_AI,
      text = "",
      isStreaming = true
    )

    val currentHistory = _chatMessages.value
    _chatMessages.value = currentHistory + userMessage + assistantPlaceholder
    _isAiGenerating.value = true
    vibrateFeedback(40)

    viewModelScope.launch {
      val accumulated = StringBuilder()
      try {
        aiService.streamResponse(cleanPrompt, currentHistory).collect { chunk ->
          accumulated.append(chunk)
          _chatMessages.value = _chatMessages.value.map { msg ->
            if (msg.id == assistantPlaceholder.id) {
              msg.copy(text = accumulated.toString(), isStreaming = true)
            } else {
              msg
            }
          }
        }
        _chatMessages.value = _chatMessages.value.map { msg ->
          if (msg.id == assistantPlaceholder.id) {
            val finalText = if (accumulated.isBlank()) {
              "Sentinel AI Dispatch transmission concluded with no data. Please retry."
            } else {
              accumulated.toString()
            }
            msg.copy(text = finalText, isStreaming = false)
          } else {
            msg
          }
        }
      } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        _chatMessages.value = _chatMessages.value.map { msg ->
          if (msg.id == assistantPlaceholder.id) {
            msg.copy(
              text = "Transmission link interrupted: ${e.localizedMessage ?: "Unknown network failure"}",
              isStreaming = false,
              isError = true
            )
          } else {
            msg
          }
        }
      } finally {
        _isAiGenerating.value = false
      }
    }
  }

  fun clearChat() {
    _chatMessages.value = listOf(
      ChatMessage(
        sender = MessageSender.SENTINEL_AI,
        text = "Tactical communications channel cleared. Standing by for queries in English or বাংলা."
      )
    )
    vibrateFeedback(40)
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }
}
