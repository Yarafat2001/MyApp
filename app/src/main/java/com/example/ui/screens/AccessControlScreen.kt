package com.example.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccessLogEntity
import com.example.data.model.GateClearanceStatus
import com.example.data.model.GateController
import com.example.ui.components.CatchablePulseButton
import com.example.ui.components.GlassCard
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
import com.example.ui.theme.PanicRed
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.VioletBright
import com.example.ui.theme.tr
import com.example.ui.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AccessControlScreen(
  viewModel: SecurityViewModel,
  gates: List<GateController>,
  accessLogs: List<AccessLogEntity>,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 6.dp, bottom = 28.dp)
  ) {
    item {
      GlassCard(
        accentColor = AlertAmber,
        cornerRadius = 24.dp,
        glowAlpha = 0.22f,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          InteractiveGradientText(
            text = tr("GATE AUTOMATION & CLEARANCE", "গেট অটোমেশন এবং ক্লিয়ারেন্স"),
            colors = if (isDark) {
              listOf(Color.White, AlertAmber, CyberCyan)
            } else {
              listOf(Color(0xFF071124), Color(0xFFD97706), Color(0xFF0284C7))
            },
            fontSize = 20.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = tr("PERIMETER BARRIERS & CREDENTIAL SCANNING", "সীমানা ব্যারিয়ার এবং ক্রেডেনশিয়াল স্ক্যানিং"),
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isDark) AlertAmber else Color(0xFFD97706),
              fontWeight = FontWeight.ExtraBold
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CatchablePulseButton(
              text = tr("Simulate Pass", "অনুমোদন টেস্ট"),
              icon = Icons.Default.CheckCircle,
              primaryColor = TacticalEmerald,
              secondaryColor = CyberCyan,
              onClick = {
                viewModel.simulateGateScan(
                  isAuthorized = true,
                  name = "Elena Rostova (SecNet Tech)",
                  plate = "9KLP224",
                  gateName = "Gate 1 - North Barrier"
                )
              },
              modifier = Modifier.weight(1f),
              testTag = "simulate_authorized_scan"
            )

            CatchablePulseButton(
              text = tr("Simulate Breach", "ব্রিচ টেস্ট"),
              icon = Icons.Default.NoAccounts,
              primaryColor = PanicRed,
              secondaryColor = NeonMagenta,
              isUrgent = true,
              onClick = {
                viewModel.simulateGateScan(
                  isAuthorized = false,
                  name = "Unregistered Vehicle",
                  plate = "NO-PLATE-00",
                  gateName = "Gate 2 - Cargo"
                )
              },
              modifier = Modifier.weight(1f),
              testTag = "simulate_denied_scan"
            )
          }
        }
      }
    }

    item {
      Text(
        text = tr("ACTIVE PERIMETER BARRIERS", "সক্রিয় সীমানা ব্যারিয়ার"),
        style = MaterialTheme.typography.labelMedium.copy(
          color = if (isDark) VioletBright else Color(0xFF6D28D9),
          fontWeight = FontWeight.Black
        )
      )
    }

    items(gates, key = { it.gateId }) { gate ->
      GateControlCard(
        gate = gate,
        onToggleLock = { viewModel.toggleGateLock(gate.gateId) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = tr("REAL-TIME CLEARANCE LOGS (${accessLogs.size})", "রিয়েল-টাইম ক্লিয়ারেন্স লগ (${accessLogs.size})"),
        style = MaterialTheme.typography.labelMedium.copy(
          color = if (isDark) CyanBright else Color(0xFF0077B6),
          fontWeight = FontWeight.Black
        )
      )
    }

    items(accessLogs, key = { it.id }) { log ->
      AccessLogCard(log = log)
    }
  }
}

@Composable
private fun GateControlCard(
  gate: GateController,
  onToggleLock: () -> Unit
) {
  val gateAccent = if (gate.isLocked) PanicRed else TacticalEmerald
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  GlassCard(
    accentColor = gateAccent,
    cornerRadius = 22.dp,
    glowAlpha = 0.20f,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("gate_card_${gate.gateId}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(gateAccent.copy(alpha = 0.24f))
              .border(1.2.dp, gateAccent, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (gate.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
              contentDescription = null,
              tint = gateAccent,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = gate.name,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = primaryText
              )
            )
            Text(
              text = if (gate.isLocked) {
                tr("Status: SECURE LOCKDOWN", "স্ট্যাটাস: সিকিউর লকডাউন")
              } else {
                tr("Status: ARMED / AUTO-PASS", "স্ট্যাটাস: অটো-পাস সক্রিয়")
              },
              style = MaterialTheme.typography.labelSmall.copy(
                color = gateAccent,
                fontWeight = FontWeight.Black
              )
            )
          }
        }

        CatchablePulseButton(
          text = if (gate.isLocked) tr("UNLOCK", "আনলক") else tr("LOCK DOWN", "লকডাউন"),
          icon = if (gate.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
          primaryColor = if (gate.isLocked) TacticalEmerald else PanicRed,
          secondaryColor = if (gate.isLocked) CyberCyan else NeonMagenta,
          isUrgent = !gate.isLocked,
          onClick = onToggleLock,
          testTag = "toggle_gate_${gate.gateId}"
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.Videocam,
            contentDescription = null,
            tint = if (isDark) CyberCyan else Color(0xFF0077B6),
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            tr("ANPR Neural Cam: Online", "এএনপিআর ক্যামেরা: অনলাইন"),
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isDark) CyanBright else Color(0xFF0077B6),
              fontWeight = FontWeight.Bold
            )
          )
        }

        Text(
          text = tr("Throughput: ${gate.automatedPassCount} Vehicles", "যানবাহন পারাপার: ${gate.automatedPassCount}"),
          style = MaterialTheme.typography.labelSmall.copy(
            color = secondaryText,
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold
          )
        )
      }
    }
  }
}

@Composable
private fun AccessLogCard(log: AccessLogEntity) {
  val dateStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestampEpochMs))
  val isApproved = log.status == GateClearanceStatus.APPROVED
  val accent = if (isApproved) TacticalEmerald else PanicRed
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  GlassCard(
    accentColor = accent,
    cornerRadius = 18.dp,
    glowAlpha = 0.14f,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      PulsingDot(color = accent)

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = log.visitorName,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = primaryText
            )
          )
          Text(
            text = dateStr,
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isDark) VioletBright else Color(0xFF6D28D9),
              fontFamily = JetBrainsMonoFontFamily,
              fontWeight = FontWeight.Bold
            )
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Plate: ${log.vehiclePlate} • ${log.gateName}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isDark) CyanBright else Color(0xFF0077B6),
              fontFamily = JetBrainsMonoFontFamily,
              fontWeight = FontWeight.Bold
            )
          )
          Text(
            text = log.status.name,
            style = MaterialTheme.typography.labelSmall.copy(
              color = accent,
              fontWeight = FontWeight.Black
            )
          )
        }

        Text(
          text = log.automatedReason,
          style = MaterialTheme.typography.labelSmall.copy(
            color = secondaryText
          )
        )
      }
    }
  }
}
