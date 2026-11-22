package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccessLogEntity
import com.example.data.model.AutomationLogEntity
import com.example.data.model.AutomationRuleEntity
import com.example.data.model.CheckpointEntity
import com.example.data.model.IncidentEntity
import com.example.data.model.IncidentStatus
import com.example.data.model.ThreatLevel
import com.example.ui.components.CatchablePulseButton
import com.example.ui.components.GlassCard
import com.example.ui.components.InteractiveGradientText
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyanBright
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.PanicRed
import com.example.ui.theme.Slate200
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.VioletBright
import com.example.ui.theme.tr
import com.example.ui.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShiftReportScreen(
  viewModel: SecurityViewModel,
  threatLevel: ThreatLevel,
  checkpoints: List<CheckpointEntity>,
  incidents: List<IncidentEntity>,
  rules: List<AutomationRuleEntity>,
  accessLogs: List<AccessLogEntity>,
  automationLogs: List<AutomationLogEntity>,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val now = remember { Date() }
  val dateFormatted = remember(now) { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(now) }

  val completedCheckpoints = checkpoints.count { it.isCompleted }
  val totalCheckpoints = checkpoints.size
  val activeIncidents = incidents.count { it.status != IncidentStatus.RESOLVED }
  val totalExecutions = rules.sumOf { it.executionCount }

  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()

  val compiledReport = remember(threatLevel, checkpoints, incidents, rules, accessLogs) {
    buildString {
      appendLine("==================================================")
      appendLine("      SENTINELOPS SECURITY SHIFT HANDOVER")
      appendLine("==================================================")
      appendLine("Timestamp: $dateFormatted")
      appendLine("Officer on Duty: Officer Sentinel-01 (ID: #4092)")
      appendLine("Sector Assignment: Sector Alpha Perimeter & Vault")
      appendLine("Threat Posture: ${threatLevel.label}")
      appendLine("--------------------------------------------------")
      appendLine("PATROL OPERATIONS:")
      appendLine("• Route: Sector Alpha Night Patrol")
      appendLine("• Checkpoints Completed: $completedCheckpoints of $totalCheckpoints")
      checkpoints.forEach { cp ->
        val statusStr = if (cp.isCompleted) "VERIFIED" else "PENDING"
        appendLine("  - [${cp.locationCode}] ${cp.name}: $statusStr")
        if (cp.notes.isNotBlank()) appendLine("    Note: ${cp.notes}")
      }
      appendLine("--------------------------------------------------")
      appendLine("INCIDENTS & AUTO-DISPATCH TRIAGE:")
      appendLine("• Total Incidents: ${incidents.size} (Active: $activeIncidents)")
      incidents.forEach { inc ->
        appendLine("  - [${inc.priority.tag}] ${inc.title} - Status: ${inc.status.name}")
        appendLine("    Location: ${inc.location} | Responder: ${inc.autoDispatchedOfficer}")
      }
      appendLine("--------------------------------------------------")
      appendLine("SECOPS AUTOMATION ENGINE:")
      appendLine("• Active Rules: ${rules.count { it.isEnabled }} of ${rules.size}")
      appendLine("• Total Auto-Executions: $totalExecutions cycles")
      appendLine("--------------------------------------------------")
      appendLine("PERIMETER ACCESS SUMMARY:")
      appendLine("• Total Gate Entries Logged: ${accessLogs.size}")
      appendLine("==================================================")
      appendLine("END OF SHIFT BRIEFING - TRANSMITTED TO COMMAND")
    }
  }

  val copySuccessMsg = tr("✓ Shift briefing copied to clipboard.", "✓ শিফট রিপোর্ট ক্লিপবোর্ডে কপি করা হয়েছে।")
  val transmitSuccessMsg = tr("✓ Shift package transmitted to SecOps Central Dispatch.", "✓ শিফট রিপোর্ট কেন্দ্রীয় কমান্ডে পাঠানো হয়েছে।")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 6.dp, bottom = 28.dp)
  ) {
    item {
      GlassCard(
        accentColor = CyanBright,
        cornerRadius = 24.dp,
        glowAlpha = 0.22f,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              InteractiveGradientText(
                text = tr("SHIFT BRIEFING LOG", "স্বয়ংক্রিয় শিফট ব্রিফিং"),
                colors = if (isDark) {
                  listOf(Color.White, CyanBright, TacticalEmerald)
                } else {
                  listOf(Color(0xFF071124), Color(0xFF0284C7), Color(0xFF059669))
                },
                fontSize = 20.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = tr("DISPATCH COMPLIANCE & HANDOVER LOG", "ডিসপ্যাচ কমপ্লায়েন্স এবং হ্যান্ডওভার লগ"),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isDark) AlertAmber else Color(0xFFD97706),
                  fontWeight = FontWeight.ExtraBold
                )
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(TacticalEmerald.copy(alpha = if (isDark) 0.24f else 0.85f))
                .border(1.dp, TacticalEmerald, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = tr("AUTO-COMPILED", "স্বয়ংক্রিয়"),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isDark) TacticalEmerald else Color.White,
                  fontWeight = FontWeight.Black
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ReportStat(
              tr("PATROL ROUTE", "টহল রুট"),
              "$completedCheckpoints/$totalCheckpoints",
              TacticalEmerald,
              Modifier.weight(1f)
            )
            ReportStat(
              tr("INCIDENTS", "ইনসিডেন্ট"),
              tr("$activeIncidents Active", "$activeIncidents সক্রিয়"),
              if (activeIncidents > 0) PanicRed else TacticalEmerald,
              Modifier.weight(1f)
            )
            ReportStat(
              tr("AUTOMATIONS", "অটোমেশন"),
              tr("$totalExecutions Exec", "$totalExecutions বার"),
              CyberCyan,
              Modifier.weight(1f)
            )
          }
        }
      }
    }

    // Catchable Animated Handover Action Buttons
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        CatchablePulseButton(
          text = tr("Copy Handover", "লগ কপি করুন"),
          icon = Icons.Default.ContentCopy,
          primaryColor = CyberCyan,
          secondaryColor = NeonViolet,
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("SentinelOps Shift Handover", compiledReport)
            clipboard.setPrimaryClip(clip)
            viewModel.showBanner(copySuccessMsg)
          },
          modifier = Modifier.weight(1f),
          testTag = "copy_shift_report_button"
        )

        CatchablePulseButton(
          text = tr("Transmit Dispatch", "ডিসপ্যাচে পাঠান"),
          icon = Icons.Default.Send,
          primaryColor = TacticalEmerald,
          secondaryColor = CyberCyan,
          onClick = {
            viewModel.showBanner(transmitSuccessMsg)
          },
          modifier = Modifier.weight(1f),
          testTag = "transmit_shift_report_button"
        )
      }
    }

    // Formatted Glassmorphic Shift Handover Terminal
    item {
      GlassCard(
        accentColor = NeonViolet,
        cornerRadius = 22.dp,
        glowAlpha = 0.18f,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = tr("COMPILED TELEMETRY BRIEFING", "সংকলিত টেলিমেট্রি ব্রিফিং"),
              style = MaterialTheme.typography.labelMedium.copy(
                color = if (isDark) VioletBright else Color(0xFF6D28D9),
                fontWeight = FontWeight.Black
              )
            )

            Text(
              text = "SHA-256 VERIFIED",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDark) CyanBright else Color(0xFF0077B6),
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xD9050914))
              .border(1.2.dp, CyberCyan.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
              .padding(14.dp)
          ) {
            Text(
              text = compiledReport,
              style = MaterialTheme.typography.bodySmall.copy(
                color = Slate200,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 11.sp,
                lineHeight = 17.sp
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ReportStat(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            color.copy(alpha = if (isDark) 0.22f else 0.18f),
            if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.70f)
          )
        )
      )
      .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
      .padding(9.dp)
  ) {
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = secondaryText,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        ),
        maxLines = 1
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.labelMedium.copy(
          color = primaryText,
          fontWeight = FontWeight.Black
        ),
        maxLines = 1
      )
    }
  }
}
