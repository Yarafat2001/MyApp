package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CheckpointEntity
import com.example.data.model.IncidentType
import com.example.data.model.LoneWorkerStatus
import com.example.data.model.ThreatLevel
import com.example.ui.components.CatchablePulseButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillChip
import com.example.ui.components.InteractiveGradientText
import com.example.ui.components.PulsingDot
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyanBright
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.PanicRed
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.VioletBright
import com.example.ui.theme.tr
import com.example.ui.viewmodel.SecOpsScreen
import com.example.ui.viewmodel.SecurityViewModel

@Composable
fun DashboardScreen(
  viewModel: SecurityViewModel,
  loneWorkerStatus: LoneWorkerStatus,
  threatLevel: ThreatLevel,
  checkpoints: List<CheckpointEntity>,
  activeRuleCount: Int,
  incidentCount: Int,
  onNavigate: (SecOpsScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  var showSosDialog by remember { mutableStateOf<IncidentType?>(null) }
  var showThreatLevelDialog by remember { mutableStateOf(false) }

  val heartRateBpm by viewModel.agentHeartRateBpm.collectAsStateWithLifecycle()
  val patrolSteps by viewModel.patrolStepsCount.collectAsStateWithLifecycle()
  val signalQuality by viewModel.meshSignalQuality.collectAsStateWithLifecycle()

  val completedCount = checkpoints.count { it.isCompleted }
  val totalCheckpoints = checkpoints.size.coerceAtLeast(1)
  val nextCheckpoint = checkpoints.firstOrNull { !it.isCompleted }
  val primaryText = GlassTextPrimary()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 6.dp, bottom = 28.dp)
  ) {
    item {
      OfficerStatusHeader(
        threatLevel = threatLevel,
        heartRateBpm = heartRateBpm,
        patrolSteps = patrolSteps,
        signalQuality = signalQuality,
        onThreatClick = { showThreatLevelDialog = true }
      )
    }

    // Pure Crystal Lone-Worker Safety Monitor with Catchable Pulse Button
    item {
      LoneWorkerSafetyCard(
        status = loneWorkerStatus,
        onCheckIn = { viewModel.checkInLoneWorker() },
        onSelectInterval = { viewModel.setLoneWorkerInterval(it) },
        onTestExpiry = { viewModel.testTriggerLoneWorkerExpiry() },
        onToggleActive = { viewModel.toggleLoneWorkerActive() }
      )
    }

    // Catchable Animated One-Touch SOS Dispatch Grid
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = PanicRed,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = tr("ONE-TOUCH AUTOMATED SOS", "তাৎক্ষণিক অটোমেটেড এসওএস"),
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Black,
                color = primaryText
              )
            )
          }
          Text(
            text = tr("INSTANT TRIAGE", "দ্রুত ডিসপ্যাচ"),
            style = MaterialTheme.typography.labelSmall.copy(
              color = NeonMagenta,
              fontWeight = FontWeight.Black
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          QuickSosButton(
            title = tr("INTRUSION", "অনুপ্রবেশ"),
            subtitle = tr("Perimeter Breach", "সীমানা লঙ্ঘন"),
            icon = Icons.Default.Security,
            color = PanicRed,
            secondaryColor = NeonMagenta,
            modifier = Modifier.weight(1f),
            testTag = "sos_intrusion",
            onClick = { showSosDialog = IncidentType.INTRUSION }
          )
          QuickSosButton(
            title = tr("FIRE / SMOKE", "অগ্নি / ধোঁয়া"),
            subtitle = tr("Thermal Hazard", "তাপীয় ঝুঁকি"),
            icon = Icons.Default.LocalFireDepartment,
            color = AlertAmber,
            secondaryColor = Color(0xFFFF6B00),
            modifier = Modifier.weight(1f),
            testTag = "sos_fire",
            onClick = { showSosDialog = IncidentType.FIRE_SMOKE }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          QuickSosButton(
            title = tr("OFFICER SOS", "অফিসার এসওএস"),
            subtitle = tr("Medical / Trauma", "জরুরি চিকিৎসা"),
            icon = Icons.Default.LocalHospital,
            color = NeonMagenta,
            secondaryColor = NeonViolet,
            modifier = Modifier.weight(1f),
            testTag = "sos_medical",
            onClick = { showSosDialog = IncidentType.MEDICAL }
          )
          QuickSosButton(
            title = tr("ACCESS TAMPER", "গেট টেম্পার"),
            subtitle = tr("Forced Vault Door", "জোরপূর্বক প্রবেশ"),
            icon = Icons.Default.Lock,
            color = CyberCyan,
            secondaryColor = NeonViolet,
            modifier = Modifier.weight(1f),
            testTag = "sos_access",
            onClick = { showSosDialog = IncidentType.ACCESS_VIOLATION }
          )
        }
      }
    }

    // Sentinel AI Copilot Tactical Card
    item {
      GlassCard(
        accentColor = NeonViolet,
        cornerRadius = 24.dp,
        glowAlpha = 0.28f,
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigate(SecOpsScreen.AI_COPILOT) }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(NeonViolet, CyberCyan)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "AI Copilot",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = tr("SENTINEL AI COPILOT", "সেন্টিনেল এআই কো-পাইলট"),
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = primaryText
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TacticalEmerald.copy(alpha = 0.2f))
                    .border(0.8.dp, TacticalEmerald, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "GEMINI 3.8",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = TacticalEmerald,
                      fontSize = 9.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(2.dp))

              Text(
                text = tr(
                  "SOP checklists, breach triage & shift log drafting",
                  "জরুরি এসওপি, সীমানা লঙ্ঘন সহায়তা ও শিফট রিপোর্ট"
                ),
                style = MaterialTheme.typography.bodySmall.copy(
                  color = primaryText.copy(alpha = 0.72f),
                  fontSize = 11.5.sp
                )
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                if (LocalIsDarkTheme.current) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.85f)
              )
              .border(
                1.dp,
                NeonViolet.copy(alpha = 0.6f),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = "Open AI",
              tint = if (LocalIsDarkTheme.current) CyberCyan else NeonViolet,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // Active Patrol Pacing Glass Widget with Catchable Verify Button
    item {
      PatrolPacingCard(
        completedCount = completedCount,
        totalCount = totalCheckpoints,
        nextCheckpoint = nextCheckpoint,
        onOpenPatrol = { onNavigate(SecOpsScreen.PATROLS) }
      )
    }

    // Interactive SecOps Automation Matrix
    item {
      GlassCard(
        accentColor = NeonViolet,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(RoundedCornerShape(11.dp))
                  .background(
                    Brush.linearGradient(
                      colors = listOf(CyberCyan.copy(alpha = 0.3f), NeonViolet.copy(alpha = 0.3f))
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Bolt,
                  contentDescription = null,
                  tint = CyberCyan,
                  modifier = Modifier.size(19.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = tr("AUTOMATION ENGINE MATRIX", "অটোমেশন ইঞ্জিন ম্যাট্রিক্স"),
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = primaryText
                )
              )
            }
            TextButton(
              onClick = { onNavigate(SecOpsScreen.AUTOMATION) },
              modifier = Modifier.testTag("view_all_automations")
            ) {
              Text(
                tr("Manage Rules", "রুলস পরিচালনা"),
                color = if (LocalIsDarkTheme.current) CyanBright else Color(0xFF0077B6),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = if (LocalIsDarkTheme.current) CyanBright else Color(0xFF0077B6),
                modifier = Modifier.size(15.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            StatusStatPill(
              label = tr("ACTIVE RULES", "সক্রিয় রুলস"),
              value = tr("$activeRuleCount Active", "$activeRuleCount সক্রিয়"),
              iconColor = CyberCyan,
              onClick = { onNavigate(SecOpsScreen.AUTOMATION) },
              modifier = Modifier.weight(1f)
            )
            StatusStatPill(
              label = tr("BARRIER GATES", "ব্যারিয়ার গেট"),
              value = tr("3 Monitored", "৩টি পর্যবেক্ষণে"),
              iconColor = TacticalEmerald,
              onClick = { onNavigate(SecOpsScreen.ACCESS) },
              modifier = Modifier.weight(1f)
            )
            StatusStatPill(
              label = tr("DISPATCH ALERTS", "ডিসপ্যাচ অ্যালার্ট"),
              value = if (incidentCount == 0) tr("All Clear", "সব স্বাভাবিক") else tr("$incidentCount Active", "$incidentCount সক্রিয়"),
              iconColor = if (incidentCount == 0) TacticalEmerald else PanicRed,
              onClick = { onNavigate(SecOpsScreen.INCIDENTS) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  }

  // Quick SOS Confirm Dialog
  showSosDialog?.let { incidentType ->
    val isDarkDialog = LocalIsDarkTheme.current
    AlertDialog(
      onDismissRequest = { showSosDialog = null },
      containerColor = if (isDarkDialog) Color(0xFF0E1B36) else Color.White,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = PanicRed)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            tr("Confirm Auto-Dispatch SOS", "অটো-ডিসপ্যাচ এসওএস নিশ্চিত করুন"),
            color = GlassTextPrimary(),
            fontWeight = FontWeight.ExtraBold
          )
        }
      },
      text = {
        Text(
          tr(
            "Trigger automated alarm broadcast for ${incidentType.title}?\n\nThis will auto-notify Central SecOps Dispatch, log GPS coordinates, and activate standard operating procedures.",
            "${incidentType.title}-এর জন্য স্বয়ংক্রিয় অ্যালার্ম চালু করবেন?\n\nএটি কেন্দ্রীয় কমান্ডে তাৎক্ষণিক বার্তা পাঠাবে এবং জিপিএস লোকেশন রেকর্ড করবে।"
          ),
          color = GlassTextSecondary(),
          fontSize = 14.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.triggerQuickSOS(incidentType, "Sector Alpha - Officer Sentinel-1")
            showSosDialog = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = PanicRed, contentColor = Color.White),
          modifier = Modifier.testTag("confirm_sos_button")
        ) {
          Text(tr("TRIGGER SOS NOW", "এখনই এসওএস পাঠান"), fontWeight = FontWeight.Black)
        }
      },
      dismissButton = {
        TextButton(onClick = { showSosDialog = null }) {
          Text(tr("Cancel", "বাতিল"), color = GlassTextMuted())
        }
      }
    )
  }

  if (showThreatLevelDialog) {
    ThreatLevelDialog(
      current = threatLevel,
      onSelect = {
        viewModel.setThreatLevel(it)
        showThreatLevelDialog = false
      },
      onDismiss = { showThreatLevelDialog = false }
    )
  }

  // Full-screen / prominent Lone Worker Emergency Visual Alert Overlay with Haptics
  if (loneWorkerStatus.isExpired) {
    LoneWorkerEmergencyVisualAlert(
      status = loneWorkerStatus,
      onCheckIn = { viewModel.checkInLoneWorker() }
    )
  }
}

@Composable
private fun OfficerStatusHeader(
  threatLevel: ThreatLevel,
  heartRateBpm: Int,
  patrolSteps: Int,
  signalQuality: Float,
  onThreatClick: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  val accent = when (threatLevel) {
    ThreatLevel.NORMAL -> CyberCyan
    ThreatLevel.ELEVATED -> AlertAmber
    ThreatLevel.HIGH -> PanicRed
    ThreatLevel.LOCKDOWN -> NeonMagenta
  }

  val infiniteTransition = rememberInfiniteTransition(label = "bio_pulse")
  val heartScale by infiniteTransition.animateFloat(
    initialValue = 0.88f,
    targetValue = 1.24f,
    animationSpec = infiniteRepeatable(
      animation = tween(550, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "heart_scale"
  )

  GlassCard(
    accentColor = accent,
    cornerRadius = 24.dp,
    glowAlpha = 0.24f,
    onClick = onThreatClick,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          InteractiveGradientText(
            text = tr("SENTINEL-01 • 99.8%", "সেনটিনেল-০১ • ৯৯.৮%"),
            colors = if (isDark) {
              listOf(Color.White, Color(0xFFE2E8F0), CyberCyan, AlertAmber)
            } else {
              listOf(Color(0xFF071124), Color(0xFF0284C7), Color(0xFFD97706))
            },
            fontSize = 24.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          // Emerald Stock/Duty Pill matching reference screenshot "• In stock · 25"
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(50))
              .background(
                if (isDark) Color(0xFF064E3B).copy(alpha = 0.78f) else Color(0xFFD1FAE5)
              )
              .border(
                width = 1.2.dp,
                color = TacticalEmerald.copy(alpha = 0.85f),
                shape = RoundedCornerShape(50)
              )
              .padding(horizontal = 12.dp, vertical = 5.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(7.dp)
                  .clip(CircleShape)
                  .background(TacticalEmerald)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = tr("Active Duty · Sector Alpha", "সক্রিয় ডিউটি · সেক্টর আলফা"),
                style = MaterialTheme.typography.labelMedium.copy(
                  color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46),
                  fontWeight = FontWeight.ExtraBold
                )
              )
            }
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
              Brush.horizontalGradient(
                colors = listOf(accent, NeonViolet)
              )
            )
            .border(1.2.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            PulsingDot(color = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = threatLevel.name,
              style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Black
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2x2 Frosted Telemetry Grid Card (Matching the 4-item feature box in the reference image)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(
            if (isDark) Color(0xFF0A142A).copy(alpha = 0.78f) else Color(0xFFF0F7FF)
          )
          .border(
            1.dp,
            if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFBFDBFE),
            RoundedCornerShape(18.dp)
          )
          .padding(14.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Item 1: Heart Rate Biometrics
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = PanicRed,
                modifier = Modifier
                  .padding(top = 2.dp)
                  .size(18.dp)
                  .scale(heartScale)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "$heartRateBpm BPM Pulse",
                  style = MaterialTheme.typography.labelLarge.copy(
                    color = primaryText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                  )
                )
                Text(
                  text = tr("Biometric Telemetry", "বায়োমেট্রিক পালস"),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = secondaryText,
                    fontSize = 11.sp
                  )
                )
              }
            }

            // Item 2: Patrol Pedometer
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.DirectionsWalk,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier
                  .padding(top = 2.dp)
                  .size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = tr("$patrolSteps Steps", "$patrolSteps পদক্ষেপ"),
                  style = MaterialTheme.typography.labelLarge.copy(
                    color = primaryText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                  )
                )
                Text(
                  text = tr("Active Patrol Route", "টহল রুট ট্র্যাকার"),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = secondaryText,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Item 3: 100% Encrypted Mesh
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Sensors,
                contentDescription = null,
                tint = TacticalEmerald,
                modifier = Modifier
                  .padding(top = 2.dp)
                  .size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = String.format("%.1f%% Mesh Sync", signalQuality),
                  style = MaterialTheme.typography.labelLarge.copy(
                    color = primaryText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                  )
                )
                Text(
                  text = tr("Direct Command Link", "সরাসরি কমান্ড সিঙ্ক"),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = secondaryText,
                    fontSize = 11.sp
                  )
                )
              }
            }

            // Item 4: AI Threat Shield
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = AlertAmber,
                modifier = Modifier
                  .padding(top = 2.dp)
                  .size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = tr("AI Threat Shield", "এআই থ্রেট শিল্ড"),
                  style = MaterialTheme.typography.labelLarge.copy(
                    color = primaryText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                  )
                )
                Text(
                  text = tr("Instant Auto-Triage", "তাৎক্ষণিক বিশ্লেষণ"),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = secondaryText,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // High-Contrast Glassy Status Capsule Pills (Matching reference design)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GlassPillChip(
          text = tr("• Live Active · Shift-02", "• সক্রিয় · শিফট-০২"),
          hasDot = true,
          dotColor = TacticalEmerald,
          accentColor = TacticalEmerald,
          modifier = Modifier.weight(1f)
        )
        GlassPillChip(
          text = tr("AI Shield · Gemini 3.8", "এআই শিল্ড · সক্রিয়"),
          icon = Icons.Default.SmartToy,
          accentColor = CyberCyan,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun LoneWorkerSafetyCard(
  status: LoneWorkerStatus,
  onCheckIn: () -> Unit,
  onSelectInterval: (Int) -> Unit,
  onTestExpiry: () -> Unit,
  onToggleActive: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val mutedText = GlassTextMuted()
  val haptic = LocalHapticFeedback.current

  // Trigger Compose haptic feedback when entering alarm warning or expiry
  LaunchedEffect(status.isExpired, status.remainingSeconds) {
    if (status.isExpired) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    } else if (status.remainingSeconds in 1..3) {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    } else if (status.remainingSeconds == 30) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  val minutes = status.remainingSeconds / 60
  val seconds = status.remainingSeconds % 60
  val timeFormatted = String.format("%02d:%02d", minutes, seconds)
  val progress = if (status.totalCountdownSeconds > 0) {
    status.remainingSeconds.toFloat() / status.totalCountdownSeconds.toFloat()
  } else 0f

  val isUrgent = status.isAlarmStage || status.isExpired

  val accentColor by animateColorAsState(
    targetValue = if (isUrgent) PanicRed else CyberCyan,
    label = "lone_worker_accent"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "ring_glow")
  val ringPulse by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (status.isExpired) 400 else 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ring_pulse"
  )

  val strobeAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(450, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "card_strobe"
  )

  GlassCard(
    accentColor = accentColor,
    cornerRadius = 26.dp,
    glowAlpha = if (status.isExpired) 0.45f * strobeAlpha else (if (status.isAlarmStage) 0.35f else 0.22f),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("lone_worker_card")
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(accentColor.copy(alpha = if (status.isExpired) 0.35f else 0.24f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (status.isExpired) Icons.Default.Warning else Icons.Default.AccessTime,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = tr("LONE-WORKER SAFETY AUTOMATION", "লোন-ওয়ার্কার সেফটি অটোমেশন"),
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Black,
                color = primaryText
              )
            )
            Text(
              text = if (status.isExpired) {
                tr("🚨 CHECK-IN EXPIRED — ESCALATED", "🚨 চেক-ইন সময় শেষ — কমান্ডে বার্তা")
              } else {
                tr("DEAD-MAN SWITCH & BIOMETRIC PULSE", "ডেড-ম্যান সুইচ এবং হার্টবিট পালস")
              },
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (status.isExpired) PanicRed else (if (isDark) VioletBright else Color(0xFF6D28D9)),
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
              if (status.isExpired) PanicRed.copy(alpha = if (isDark) 0.35f else 0.85f)
              else if (status.isActive) TacticalEmerald.copy(alpha = if (isDark) 0.25f else 0.85f)
              else Slate400.copy(alpha = 0.25f)
            )
            .border(
              1.dp,
              if (status.isExpired) PanicRed else if (status.isActive) TacticalEmerald else Slate400,
              RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onToggleActive)
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = if (status.isExpired) tr("🚨 EXPIRED", "🚨 মেয়াদ শেষ")
            else if (status.isActive) tr("● ARMED", "● সক্রিয়")
            else tr("PAUSED", "বিরতি"),
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (status.isExpired) (if (isDark) PanicRed else Color.White)
              else if (status.isActive) (if (isDark) TacticalEmerald else Color.White)
              else mutedText,
              fontWeight = FontWeight.Black
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Animated Circular Neon Countdown Dial
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(82.dp)
          ) {
            Canvas(modifier = Modifier.size(76.dp)) {
              drawArc(
                color = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFCBD5E1),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 7.5.dp.toPx(), cap = StrokeCap.Round)
              )
              drawArc(
                brush = Brush.sweepGradient(
                  colors = if (isUrgent) {
                    listOf(PanicRed, NeonMagenta, Color.White, PanicRed)
                  } else {
                    listOf(accentColor, NeonViolet, TacticalEmerald, accentColor)
                  }
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 7.5.dp.toPx(), cap = StrokeCap.Round)
              )
            }
            Text(
              text = if (status.isExpired) "0%" else "${(progress * 100).toInt()}%",
              style = MaterialTheme.typography.labelLarge.copy(
                color = if (status.isExpired) PanicRed else (if (isDark) CyanBright.copy(alpha = ringPulse) else Color(0xFF0077B6)),
                fontWeight = FontWeight.Black,
                fontFamily = JetBrainsMonoFontFamily
              )
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = tr("HEARTBEAT COUNTDOWN", "হার্টবিট কাউন্টডাউন"),
              style = MaterialTheme.typography.labelSmall.copy(
                color = mutedText,
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = if (status.isExpired) "00:00" else timeFormatted,
              style = MaterialTheme.typography.headlineLarge.copy(
                color = if (isUrgent) PanicRed else primaryText,
                fontWeight = FontWeight.Black,
                fontFamily = JetBrainsMonoFontFamily
              )
            )
            Text(
              text = if (status.isExpired) {
                tr("🚨 CHECK-IN EXPIRED - ALARM ACTIVE", "🚨 চেক-ইন সময় শেষ - জরুরি সংকেত")
              } else if (status.isAlarmStage) {
                tr("⚠️ WARNING: < 30S REMAINING", "⚠️ সতর্কবার্তা: ৩০ সেকেন্ড বাকি")
              } else {
                tr("Auto-escalates if unconfirmed", "নিশ্চিত না করলে স্বয়ংক্রিয় অ্যালার্ম")
              },
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isUrgent) PanicRed else (if (isDark) TacticalEmerald else Color(0xFF059669)),
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        // Safe, Catchable Animated Pulse Button
        CatchablePulseButton(
          text = if (status.isExpired) tr("OVERRIDE / SAFE", "ওভাররাইড / নিরাপদ") else tr("CONFIRM SAFE", "নিরাপদ নিশ্চিত"),
          subtitle = tr("Tap Check-In", "চেক-ইন করুন"),
          icon = Icons.Default.CheckCircle,
          primaryColor = if (isUrgent) PanicRed else CyberCyan,
          secondaryColor = if (isUrgent) NeonMagenta else NeonViolet,
          isUrgent = isUrgent,
          onClick = onCheckIn,
          testTag = "lone_worker_checkin_button"
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Glass Interval Selector Chips & Quick 5s Test Trigger
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = tr("INTERVAL:", "সময়সীমা:"),
          style = MaterialTheme.typography.labelSmall.copy(
            color = mutedText,
            fontWeight = FontWeight.Bold
          )
        )
        listOf(5, 15, 30).forEach { mins ->
          val isCurrent = status.totalCountdownSeconds == mins * 60 && !status.isExpired
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(
                if (isCurrent) {
                  Brush.horizontalGradient(
                    colors = listOf(CyberCyan, NeonViolet)
                  )
                } else {
                  Brush.horizontalGradient(
                    colors = listOf(
                      if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.65f),
                      if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.65f)
                    )
                  )
                }
              )
              .border(
                1.dp,
                if (isCurrent) Color.White else CyberCyan.copy(alpha = 0.4f),
                RoundedCornerShape(10.dp)
              )
              .clickable { onSelectInterval(mins) }
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = "${mins}m",
              style = MaterialTheme.typography.labelMedium.copy(
                color = if (isCurrent) Color.White else primaryText,
                fontWeight = FontWeight.Black
              )
            )
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Quick Test 5s Expiry Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.horizontalGradient(
                listOf(AlertAmber.copy(alpha = 0.28f), PanicRed.copy(alpha = 0.28f))
              )
            )
            .border(
              1.dp,
              AlertAmber.copy(alpha = 0.8f),
              RoundedCornerShape(10.dp)
            )
            .clickable { onTestExpiry() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("test_expiry_5s_button")
        ) {
          Text(
            text = tr("⚡ Test 5s", "⚡ ৫ সে. টেস্ট"),
            style = MaterialTheme.typography.labelMedium.copy(
              color = if (isDark) AlertAmber else Color(0xFFB45309),
              fontWeight = FontWeight.Black,
              fontSize = 11.5.sp
            )
          )
        }
      }
    }
  }
}

/**
 * High-Urgency Visual Alert Overlay Modal for Lone Worker Safety Expiration.
 * Features flashing strobe borders, pulsing siren beacon, and dead-man override.
 */
@Composable
fun LoneWorkerEmergencyVisualAlert(
  status: LoneWorkerStatus,
  onCheckIn: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val haptic = LocalHapticFeedback.current

  // Urgent strobe pulse animation
  val infiniteTransition = rememberInfiniteTransition(label = "emergency_strobe")
  val strobeAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(450, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "strobe_alpha"
  )
  val beaconScale by infiniteTransition.animateFloat(
    initialValue = 0.92f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "beacon_scale"
  )

  // Trigger repeated Compose haptic feedback when the alert is visible
  LaunchedEffect(status.isExpired) {
    while (status.isExpired) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      kotlinx.coroutines.delay(1200)
    }
  }

  AlertDialog(
    onDismissRequest = { /* Dead-man switch: must interact explicitly */ },
    containerColor = Color.Transparent,
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .testTag("lone_worker_emergency_modal"),
    confirmButton = {
      Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CatchablePulseButton(
          text = tr("I AM SAFE — CANCEL ALARM", "আমি নিরাপদ — অ্যালার্ম বাতিল করুন"),
          subtitle = tr("Dead-Man Override", "ডেড-ম্যান ওভাররাইড"),
          icon = Icons.Default.CheckCircle,
          primaryColor = PanicRed,
          secondaryColor = NeonMagenta,
          isUrgent = true,
          onClick = onCheckIn,
          modifier = Modifier.fillMaxWidth(),
          testTag = "emergency_deadman_override_button"
        )
      }
    },
    text = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(26.dp))
          .background(
            if (isDark) {
              Brush.verticalGradient(
                listOf(
                  PanicRed.copy(alpha = 0.28f * strobeAlpha),
                  Color(0xFF1F0910).copy(alpha = 0.94f)
                )
              )
            } else {
              Brush.verticalGradient(
                listOf(
                  Color.White,
                  Color(0xFFFFF0F2)
                )
              )
            }
          )
          .border(
            width = 2.5.dp,
            brush = Brush.linearGradient(
              listOf(
                PanicRed.copy(alpha = strobeAlpha),
                Color.White.copy(alpha = strobeAlpha * 0.9f),
                PanicRed
              )
            ),
            shape = RoundedCornerShape(26.dp)
          )
          .padding(20.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          // Flashing emergency siren beacon
          Box(
            modifier = Modifier
              .size(68.dp)
              .scale(beaconScale)
              .clip(CircleShape)
              .background(PanicRed.copy(alpha = 0.25f))
              .border(2.dp, PanicRed.copy(alpha = strobeAlpha), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Alert Beacon",
              tint = PanicRed,
              modifier = Modifier.size(38.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = tr("LONE-WORKER CHECK-IN EXPIRED!", "লোন-ওয়ার্কার চেক-ইন সময় শেষ!"),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = PanicRed,
              fontSize = 17.sp,
              textAlign = TextAlign.Center
            )
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = tr("DEAD-MAN SWITCH TIMEOUT — OFFICER UNRESPONSIVE", "ডেড-ম্যান সুইচ সময় শেষ — অফিসার সাড়া দেননি"),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color.White.copy(alpha = 0.85f) else Slate950,
              fontSize = 11.sp,
              textAlign = TextAlign.Center
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Big countdown ticker showing 00:00 EXPIRED in glowing red
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(PanicRed.copy(alpha = 0.15f))
              .border(1.dp, PanicRed.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
              .padding(horizontal = 20.dp, vertical = 8.dp)
          ) {
            Text(
              text = "00:00 EXPIRED",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = PanicRed,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 24.sp
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = tr(
              "Central SecOps Command has been auto-notified via priority telemetry. GPS sector coordinates locked. Vital signs: Pulse active. Tap Dead-Man Override below to confirm safety.",
              "কেন্দ্রীয় কমান্ডে জরুরি সতর্কবার্তা পাঠানো হয়েছে। জিপিএস স্থানাঙ্ক রেকর্ড করা হয়েছে। আপনি নিরাপদ থাকলে অবিলম্বে নিচের ওভাররাইড বোতামে ট্যাপ করুন।"
            ),
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (isDark) Slate200 else Slate700,
              fontSize = 12.sp,
              textAlign = TextAlign.Center,
              lineHeight = 17.sp
            )
          )
        }
      }
    }
  )
}

@Composable
private fun QuickSosButton(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  secondaryColor: Color,
  modifier: Modifier = Modifier,
  testTag: String,
  onClick: () -> Unit
) {
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  val infiniteTransition = rememberInfiniteTransition(label = "sos_icon_$title")
  val iconPulse by infiniteTransition.animateFloat(
    initialValue = 0.92f,
    targetValue = 1.14f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "sos_icon_scale"
  )

  GlassCard(
    accentColor = color,
    cornerRadius = 20.dp,
    glowAlpha = 0.25f,
    onClick = onClick,
    modifier = modifier.testTag(testTag)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .scale(iconPulse)
          .clip(RoundedCornerShape(14.dp))
          .background(
            Brush.linearGradient(
              colors = listOf(color, secondaryColor)
            )
          )
          .border(1.2.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Black,
            color = primaryText
          )
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall.copy(
            color = secondaryText,
            fontWeight = FontWeight.SemiBold
          ),
          maxLines = 1
        )
      }
    }
  }
}

@Composable
private fun PatrolPacingCard(
  completedCount: Int,
  totalCount: Int,
  nextCheckpoint: CheckpointEntity?,
  onOpenPatrol: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()

  GlassCard(
    accentColor = TacticalEmerald,
    cornerRadius = 24.dp,
    glowAlpha = 0.22f,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("patrol_pacing_card")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(TacticalEmerald.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.DirectionsWalk,
              contentDescription = null,
              tint = TacticalEmerald,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = tr("ACTIVE PATROL PACER", "সক্রিয় টহল পেসার"),
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Black,
              color = primaryText
            )
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(TacticalEmerald.copy(alpha = if (isDark) 0.24f else 0.85f))
            .border(1.dp, TacticalEmerald, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = tr("$completedCount / $totalCount DONE", "$completedCount / $totalCount সম্পন্ন"),
            style = MaterialTheme.typography.labelMedium.copy(
              color = if (isDark) TacticalEmerald else Color.White,
              fontWeight = FontWeight.Black,
              fontFamily = JetBrainsMonoFontFamily
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      LinearProgressIndicator(
        progress = { completedCount.toFloat() / totalCount.toFloat() },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = TacticalEmerald,
        trackColor = if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFCBD5E1),
        strokeCap = StrokeCap.Round
      )

      Spacer(modifier = Modifier.height(14.dp))

      if (nextCheckpoint != null) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = tr("NEXT CHECKPOINT #${nextCheckpoint.sequenceOrder}", "পরবর্তী চেকপয়েন্ট #${nextCheckpoint.sequenceOrder}"),
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDark) VioletBright else Color(0xFF6D28D9),
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = nextCheckpoint.name,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = primaryText
              ),
              maxLines = 1
            )
            Text(
              text = "Token: ${nextCheckpoint.nfcTagId} • ${nextCheckpoint.targetIntervalMins}m",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDark) CyanBright else Color(0xFF0077B6),
                fontFamily = JetBrainsMonoFontFamily
              )
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          CatchablePulseButton(
            text = tr("Verify", "যাচাই করুন"),
            icon = Icons.Default.QrCodeScanner,
            primaryColor = CyberCyan,
            secondaryColor = NeonViolet,
            onClick = onOpenPatrol,
            testTag = "open_patrol_button"
          )
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = tr("✓ Full Route Alpha Complete. Ready for handover.", "✓ সম্পূর্ণ রুট আলফা সম্পন্ন হয়েছে।"),
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (isDark) TacticalEmerald else Color(0xFF059669),
              fontWeight = FontWeight.Bold
            )
          )
          TextButton(onClick = onOpenPatrol) {
            Text(
              tr("Review", "পর্যালোচনা"),
              color = if (isDark) TacticalEmerald else Color(0xFF059669),
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StatusStatPill(
  label: String,
  value: String,
  iconColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.93f else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "stat_pill_scale"
  )

  Box(
    modifier = modifier
      .scale(scale)
      .clip(RoundedCornerShape(14.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            iconColor.copy(alpha = if (isDark) 0.22f else 0.18f),
            if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.70f)
          )
        )
      )
      .border(1.2.dp, iconColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(10.dp)
  ) {
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = secondaryText,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        ),
        maxLines = 1
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        PulsingDot(color = iconColor)
        Spacer(modifier = Modifier.width(5.dp))
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
}

@Composable
private fun ThreatLevelDialog(
  current: ThreatLevel,
  onSelect: (ThreatLevel) -> Unit,
  onDismiss: () -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = if (isDark) Color(0xFF0E1B36) else Color.White,
    title = {
      Text(
        text = tr("Set SecOps Threat Posture", "নিরাপত্তা থ্রেট লেভেল নির্ধারণ করুন"),
        color = primaryText,
        fontWeight = FontWeight.ExtraBold
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ThreatLevel.entries.forEach { level ->
          val isSelected = level == current
          val levelColor = when (level) {
            ThreatLevel.NORMAL -> TacticalEmerald
            ThreatLevel.ELEVATED -> AlertAmber
            ThreatLevel.HIGH -> PanicRed
            ThreatLevel.LOCKDOWN -> NeonMagenta
          }
          GlassCard(
            accentColor = levelColor,
            cornerRadius = 16.dp,
            glowAlpha = if (isSelected) 0.30f else 0.12f,
            onClick = { onSelect(level) },
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              PulsingDot(color = levelColor)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = level.label,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                  )
                )
                Text(
                  text = when (level) {
                    ThreatLevel.NORMAL -> tr("Routine patrol & automated visitor clearance", "নিয়মিত টহল এবং স্বয়ংক্রিয় গেট ক্লিয়ারেন্স")
                    ThreatLevel.ELEVATED -> tr("Heightened surveillance, 15m checkpoint pacing", "বাড়তি নজরদারি এবং ১৫ মিনিট চেকপয়েন্ট পেসিং")
                    ThreatLevel.HIGH -> tr("Secondary perimeter floodlights pre-armed", "সেকেন্ডারি ফ্লাডলাইট এবং অ্যালার্ম প্রস্তুত")
                    ThreatLevel.LOCKDOWN -> tr("Remote pulse locks on all gates & automated siren", "সব গেটে রিমোট লকডাউন এবং সাইরেন সক্রিয়")
                  },
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = secondaryText
                  )
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(tr("Close", "বন্ধ করুন"), color = if (isDark) CyberCyan else Color(0xFF0284C7), fontWeight = FontWeight.Bold)
      }
    }
  )
}
