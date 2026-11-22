package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AccessLogEntity
import com.example.data.model.AutomationLogEntity
import com.example.data.model.AutomationRuleEntity
import com.example.data.model.CheckpointEntity
import com.example.data.model.IncidentEntity
import com.example.data.model.IncidentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {

  // Automation Rules
  @Query("SELECT * FROM automation_rules ORDER BY id DESC")
  fun getAllRulesFlow(): Flow<List<AutomationRuleEntity>>

  @Query("SELECT * FROM automation_rules WHERE isEnabled = 1")
  suspend fun getActiveRules(): List<AutomationRuleEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRule(rule: AutomationRuleEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRules(rules: List<AutomationRuleEntity>)

  @Update
  suspend fun updateRule(rule: AutomationRuleEntity)

  @Query("UPDATE automation_rules SET isEnabled = :isEnabled WHERE id = :ruleId")
  suspend fun setRuleEnabled(ruleId: Long, isEnabled: Boolean)

  @Query("UPDATE automation_rules SET executionCount = executionCount + 1, lastTriggeredEpochMs = :timestamp WHERE id = :ruleId")
  suspend fun recordRuleExecution(ruleId: Long, timestamp: Long)

  @Delete
  suspend fun deleteRule(rule: AutomationRuleEntity)

  // Checkpoints / Patrol Routes
  @Query("SELECT * FROM checkpoints ORDER BY sequenceOrder ASC")
  fun getAllCheckpointsFlow(): Flow<List<CheckpointEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCheckpoints(checkpoints: List<CheckpointEntity>)

  @Update
  suspend fun updateCheckpoint(checkpoint: CheckpointEntity)

  @Query("UPDATE checkpoints SET isCompleted = 1, completedAtEpochMs = :completedAt, notes = :notes WHERE id = :id")
  suspend fun completeCheckpoint(id: Long, completedAt: Long, notes: String)

  @Query("UPDATE checkpoints SET isCompleted = 0, completedAtEpochMs = NULL")
  suspend fun resetRouteCheckpoints()

  // Incidents
  @Query("SELECT * FROM incidents ORDER BY reportedAtEpochMs DESC")
  fun getAllIncidentsFlow(): Flow<List<IncidentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertIncident(incident: IncidentEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertIncidents(incidents: List<IncidentEntity>)

  @Update
  suspend fun updateIncident(incident: IncidentEntity)

  @Query("UPDATE incidents SET status = :status WHERE id = :id")
  suspend fun updateIncidentStatus(id: Long, status: IncidentStatus)

  // Access Logs
  @Query("SELECT * FROM access_logs ORDER BY timestampEpochMs DESC LIMIT 50")
  fun getRecentAccessLogsFlow(): Flow<List<AccessLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccessLog(log: AccessLogEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccessLogs(logs: List<AccessLogEntity>)

  // Automation Logs
  @Query("SELECT * FROM automation_logs ORDER BY timestampEpochMs DESC LIMIT 60")
  fun getRecentAutomationLogsFlow(): Flow<List<AutomationLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAutomationLog(log: AutomationLogEntity): Long

  @Query("DELETE FROM automation_logs")
  suspend fun clearAutomationLogs()
}
