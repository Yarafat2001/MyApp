package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AccessLogEntity
import com.example.data.model.ActionType
import com.example.data.model.AutomationLogEntity
import com.example.data.model.AutomationRuleEntity
import com.example.data.model.CheckpointEntity
import com.example.data.model.GateClearanceStatus
import com.example.data.model.IncidentEntity
import com.example.data.model.IncidentPriority
import com.example.data.model.IncidentStatus
import com.example.data.model.IncidentType
import com.example.data.model.TriggerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    AutomationRuleEntity::class,
    CheckpointEntity::class,
    IncidentEntity::class,
    AccessLogEntity::class,
    AutomationLogEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class SecurityDatabase : RoomDatabase() {

  abstract fun securityDao(): SecurityDao

  companion object {
    @Volatile
    private var INSTANCE: SecurityDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): SecurityDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          SecurityDatabase::class.java,
          "sentinel_ops_security.db"
        )
        .addCallback(SecurityDatabaseCallback(scope))
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }

    private class SecurityDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.securityDao())
          }
        }
      }

      suspend fun populateInitialData(dao: SecurityDao) {
        val now = System.currentTimeMillis()

        // 1. Initial Automation Rules
        dao.insertRules(
          listOf(
            AutomationRuleEntity(
              title = "Perimeter Intrusion Strobe & Dispatch",
              triggerType = TriggerType.SENSOR,
              conditionDesc = "PIR Motion > 30s in Sector Bravo after 21:00",
              actionType = ActionType.STROBE_LIGHTS,
              isEnabled = true,
              executionCount = 14,
              lastTriggeredEpochMs = now - 1800000,
              sector = "Sector B (North Fence)"
            ),
            AutomationRuleEntity(
              title = "Lone-Worker Heartbeat Safeguard",
              triggerType = TriggerType.LONE_WORKER_INACTIVITY,
              conditionDesc = "Officer check-in countdown reaches 00:00 without confirmation",
              actionType = ActionType.ALERT_SUPERVISOR,
              isEnabled = true,
              executionCount = 2,
              lastTriggeredEpochMs = now - 86400000,
              sector = "All Patrol Routes"
            ),
            AutomationRuleEntity(
              title = "Data Center Vault Lockdown on Force Alarm",
              triggerType = TriggerType.BADGE,
              conditionDesc = "3 Consecutive Failed Biometric Badge attempts at Server Room 4",
              actionType = ActionType.LOCKDOWN_GATE,
              isEnabled = true,
              executionCount = 1,
              lastTriggeredEpochMs = now - 7200000,
              sector = "Sub-level 2 Vault"
            ),
            AutomationRuleEntity(
              title = "Checkpoint Pacing Escalation",
              triggerType = TriggerType.CHECKPOINT_DELAY,
              conditionDesc = "Officer delayed past target pace interval by > 10 mins",
              actionType = ActionType.AUTO_DISPATCH,
              isEnabled = true,
              executionCount = 5,
              lastTriggeredEpochMs = now - 36000000,
              sector = "Perimeter Route Alpha"
            ),
            AutomationRuleEntity(
              title = "After-Hours Cargo Gate Automated Clearance",
              triggerType = TriggerType.GEOFENCE,
              conditionDesc = "Whitelisted logistics truck NFC scan matches manifest pre-clearance",
              actionType = ActionType.LOG_EVENT,
              isEnabled = true,
              executionCount = 38,
              lastTriggeredEpochMs = now - 900000,
              sector = "Gate 2 (Cargo Logistics)"
            )
          )
        )

        // 2. Initial Patrol Route Checkpoints (Alpha Night Round)
        dao.insertCheckpoints(
          listOf(
            CheckpointEntity(
              routeId = "ROUTE_ALPHA",
              routeName = "Sector Alpha Night Patrol",
              name = "Main Gate Security Kiosk",
              locationCode = "CP-01-MG",
              sequenceOrder = 1,
              isCompleted = true,
              completedAtEpochMs = now - 1800000,
              targetIntervalMins = 8,
              nfcTagId = "NFC-8821-GATE",
              qrCode = "SEC-ALPHA-CP01-PASS",
              checklistItemsRaw = "Inspect perimeter bollards,Confirm visitor log sync,Check gate barrier hydraulic pressure",
              notes = "Barrier seals intact. Officer pass normal."
            ),
            CheckpointEntity(
              routeId = "ROUTE_ALPHA",
              routeName = "Sector Alpha Night Patrol",
              name = "Server Room & Vault Corridor",
              locationCode = "CP-02-SR",
              sequenceOrder = 2,
              isCompleted = true,
              completedAtEpochMs = now - 900000,
              targetIntervalMins = 10,
              nfcTagId = "NFC-9043-VAULT",
              qrCode = "SEC-ALPHA-CP02-VAULT",
              checklistItemsRaw = "Verify biometric door magnetic latch,Inspect thermal HVAC status,Check server rack tamper seals",
              notes = "All rack tamper seals verified."
            ),
            CheckpointEntity(
              routeId = "ROUTE_ALPHA",
              routeName = "Sector Alpha Night Patrol",
              name = "Sub-station Transformer Bay",
              locationCode = "CP-03-PWR",
              sequenceOrder = 3,
              isCompleted = false,
              completedAtEpochMs = null,
              targetIntervalMins = 12,
              nfcTagId = "NFC-7712-TRANS",
              qrCode = "SEC-ALPHA-CP03-TRANS",
              checklistItemsRaw = "Check high-voltage perimeter lock,Listen for capacitor hum anomalies,Inspect emergency fire extinguisher seal",
              notes = ""
            ),
            CheckpointEntity(
              routeId = "ROUTE_ALPHA",
              routeName = "Sector Alpha Night Patrol",
              name = "North Perimeter Fence & IR Beams",
              locationCode = "CP-04-NPF",
              sequenceOrder = 4,
              isCompleted = false,
              completedAtEpochMs = null,
              targetIntervalMins = 15,
              nfcTagId = "NFC-4491-FENCE",
              qrCode = "SEC-ALPHA-CP04-FENCE",
              checklistItemsRaw = "Physical check of razor wire tension,Test optical infrared beam alignment,Inspect CCTV PTZ camera 07 housing",
              notes = ""
            ),
            CheckpointEntity(
              routeId = "ROUTE_ALPHA",
              routeName = "Sector Alpha Night Patrol",
              name = "Executive Rooftop Helipad & Access Stair",
              locationCode = "CP-05-HLP",
              sequenceOrder = 5,
              isCompleted = false,
              completedAtEpochMs = null,
              targetIntervalMins = 10,
              nfcTagId = "NFC-3302-HELI",
              qrCode = "SEC-ALPHA-CP05-HELI",
              checklistItemsRaw = "Verify windsock lighting,Check roof access heavy fire door latch,Inspect emergency phone terminal",
              notes = ""
            )
          )
        )

        // 3. Initial Incidents
        dao.insertIncidents(
          listOf(
            IncidentEntity(
              title = "Motion Alarm Triggered at Sector B Outer Fence",
              type = IncidentType.INTRUSION,
              priority = IncidentPriority.HIGH,
              status = IncidentStatus.RESPONDING,
              reportedAtEpochMs = now - 1200000,
              location = "Sector Bravo Fence (Post 14)",
              notes = "Automated thermal camera detected movement near tree line. Audio strobe dispatched.",
              sopStepsRaw = "Acknowledge Central Dispatch|Verify thermal camera PTZ-04 feed|Approach with tactical flashlight at low ready|Inspect razor wire integrity|Log clear status or request backup",
              completedStepsCount = 2,
              autoDispatchedOfficer = "Officer Sentinel-4 (En Route)"
            ),
            IncidentEntity(
              title = "Badge Reader Tamper Alarm on Fire Exit 03",
              type = IncidentType.ACCESS_VIOLATION,
              priority = IncidentPriority.MEDIUM,
              status = IncidentStatus.RESOLVED,
              reportedAtEpochMs = now - 5400000,
              location = "East Wing Exit 03",
              notes = "Magnetic contact switch loose due to heavy gust. Technicians re-calibrated. False alarm.",
              sopStepsRaw = "Verify door position sensor|Check area for forced entry marks|Re-seat magnetic contact|Report resolution to SecOps",
              completedStepsCount = 4,
              autoDispatchedOfficer = "Officer Sentinel-2"
            )
          )
        )

        // 4. Initial Access Logs
        dao.insertAccessLogs(
          listOf(
            AccessLogEntity(
              badgeId = "SEC-EMP-8842",
              visitorName = "Marcus Vance",
              company = "Sentinel Network Security",
              clearanceLevel = "Level 4 (Admin)",
              vehiclePlate = "7XYZ892",
              gateName = "Gate 1 (Executive)",
              timestampEpochMs = now - 600000,
              status = GateClearanceStatus.APPROVED,
              automatedReason = "Biometric facial token matched active shift schedule"
            ),
            AccessLogEntity(
              badgeId = "VIS-TMP-1049",
              visitorName = "Apex Logistics Driver",
              company = "Apex Supply Chain",
              clearanceLevel = "Level 1 (Delivery)",
              vehiclePlate = "3KLM510",
              gateName = "Gate 2 (Cargo)",
              timestampEpochMs = now - 2400000,
              status = GateClearanceStatus.APPROVED,
              automatedReason = "Automated bill-of-lading license plate match"
            ),
            AccessLogEntity(
              badgeId = "UNKNOWN-RFID-990",
              visitorName = "Unregistered Card",
              company = "Unknown",
              clearanceLevel = "None",
              vehiclePlate = "8ABC123",
              gateName = "Gate 3 (Sub-Station)",
              timestampEpochMs = now - 7200000,
              status = GateClearanceStatus.DENIED,
              automatedReason = "Card decommissioned in centralized directory. Barrier held locked."
            )
          )
        )

        // 5. Initial Automation Logs
        dao.insertAutomationLog(
          AutomationLogEntity(
            ruleTitle = "Perimeter Intrusion Strobe & Dispatch",
            triggerDesc = "PIR Motion Sector Bravo Post 14",
            actionExecuted = "Floodlights activated + Backup alert to Agent Sentinel-4",
            timestampEpochMs = now - 1200000,
            status = "EXECUTED",
            details = "Zone 2 floodlights triggered for 180s"
          )
        )
      }
    }
  }
}
