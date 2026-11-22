package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CheckpointEntity
import com.example.ui.components.CatchablePulseButton
import com.example.ui.components.GlassCard
import com.example.ui.components.InteractiveGradientText
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyanBright
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.PanicRed
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.VioletBright
import com.example.ui.theme.tr
import com.example.ui.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PatrolScreen(
  viewModel: SecurityViewModel,
  checkpoints: List<CheckpointEntity>,
  patrolElapsedSeconds: Int,
  modifier: Modifier = Modifier
) {
  var activeVerificationCheckpoint by remember { mutableStateOf<CheckpointEntity?>(null) }
  var showResetConfirm by remember { mutableStateOf(false) }

  val completedCount = checkpoints.count { it.isCompleted }
  val totalCount = checkpoints.size.coerceAtLeast(1)
  val elapsedMins = patrolElapsedSeconds / 60
  val elapsedSecs = patrolElapsedSeconds % 60
  val pacingTime = String.format("%02d:%02d", elapsedMins, elapsedSecs)

  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val mutedText = GlassTextMuted()

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    GlassCard(
      accentColor = TacticalEmerald,
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
              text = tr("PATROL ROUTE ALPHA", "টহল রুট আলফা"),
              colors = if (isDark) {
                listOf(Color.White, TacticalEmerald, CyberCyan)
              } else {
                listOf(Color(0xFF071124), Color(0xFF059669), Color(0xFF0284C7))
              },
              fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = tr("AUTOMATED NFC / QR VERIFICATION", "স্বয়ংক্রিয় এনএফসি / কিউআর যাচাইকরণ"),
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDark) AlertAmber else Color(0xFFD97706),
                fontWeight = FontWeight.ExtraBold
              )
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(
                if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.75f)
              )
              .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
              .clickable { showResetConfirm = true }
              .padding(horizontal = 12.dp, vertical = 7.dp)
              .testTag("reset_patrol_route_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                tint = if (isDark) CyanBright else Color(0xFF0077B6),
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                tr("Reset Round", "রাউন্ড রিসেট"),
                color = primaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                  Brush.linearGradient(
                    colors = listOf(CyberCyan.copy(alpha = 0.3f), NeonViolet.copy(alpha = 0.3f))
                  )
                )
                .border(1.2.dp, CyberCyan, RoundedCornerShape(14.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Timer, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = tr("CURRENT CHECKPOINT PACE", "বর্তমান চেকপয়েন্ট পেস"),
                style = MaterialTheme.typography.labelSmall.copy(color = mutedText, fontWeight = FontWeight.Bold)
              )
              Text(
                text = pacingTime,
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.Black,
                  fontFamily = JetBrainsMonoFontFamily,
                  color = if (elapsedMins > 12) PanicRed else primaryText
                )
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = tr("PROGRESSION", "অগ্রগতি"),
              style = MaterialTheme.typography.labelSmall.copy(color = mutedText, fontWeight = FontWeight.Bold)
            )
            Text(
              text = tr("$completedCount of $totalCount COMPLETE", "$completedCount / $totalCount সম্পন্ন"),
              style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isDark) TacticalEmerald else Color(0xFF059669),
                fontWeight = FontWeight.Black,
                fontFamily = JetBrainsMonoFontFamily
              )
            )
            Text(
              text = if (completedCount == totalCount) {
                tr("✓ ROUTE VERIFIED", "✓ রুট যাচাইকৃত")
              } else {
                tr("PATROL IN PROGRESS", "টহল চলমান")
              },
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (completedCount == totalCount) TacticalEmerald else AlertAmber,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(bottom = 28.dp),
      modifier = Modifier
        .fillMaxSize()
        .testTag("checkpoints_list")
    ) {
      items(checkpoints, key = { it.id }) { checkpoint ->
        CheckpointCard(
          checkpoint = checkpoint,
          onVerifyClick = { activeVerificationCheckpoint = checkpoint }
        )
      }
    }
  }

  activeVerificationCheckpoint?.let { checkpoint ->
    VerificationDialog(
      checkpoint = checkpoint,
      onDismiss = { activeVerificationCheckpoint = null },
      onConfirm = { notes ->
        viewModel.completeCheckpoint(checkpoint, notes)
        activeVerificationCheckpoint = null
      }
    )
  }

  if (showResetConfirm) {
    AlertDialog(
      onDismissRequest = { showResetConfirm = false },
      containerColor = if (isDark) Color(0xFF0E1B36) else Color.White,
      title = { Text(tr("Reset Patrol Route?", "টহল রুট রিসেট করবেন?"), color = primaryText, fontWeight = FontWeight.ExtraBold) },
      text = {
        Text(
          tr(
            "This will reset all checkpoints in Route Alpha to pending status for the next scheduled patrol shift.",
            "এটি রুট আলফার সমস্ত চেকপয়েন্ট পরবর্তী শিফটের জন্য পুনরায় পেন্ডিং অবস্থায় রিসেট করবে।"
          ),
          color = GlassTextSecondary()
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.resetPatrolRoute()
            showResetConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Slate950)
        ) {
          Text(tr("Reset Route", "রিসেট করুন"), fontWeight = FontWeight.Black)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirm = false }) {
          Text(tr("Cancel", "বাতিল"), color = mutedText)
        }
      }
    )
  }
}

@Composable
private fun CheckpointCard(
  checkpoint: CheckpointEntity,
  onVerifyClick: () -> Unit
) {
  val taskList = remember(checkpoint.checklistItemsRaw) {
    checkpoint.checklistItemsRaw.split(",").map { it.trim() }.filter { it.isNotBlank() }
  }
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  GlassCard(
    accentColor = if (checkpoint.isCompleted) TacticalEmerald else CyberCyan,
    cornerRadius = 22.dp,
    glowAlpha = 0.20f,
    onClick = if (!checkpoint.isCompleted) onVerifyClick else null,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("checkpoint_item_${checkpoint.sequenceOrder}")
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
              .size(40.dp)
              .clip(CircleShape)
              .background(
                if (checkpoint.isCompleted) {
                  Brush.linearGradient(colors = listOf(TacticalEmerald, CyberCyan))
                } else {
                  Brush.linearGradient(colors = listOf(CyberCyan, NeonViolet))
                }
              )
              .border(1.2.dp, Color.White.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            if (checkpoint.isCompleted) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            } else {
              Text(
                text = "${checkpoint.sequenceOrder}",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = checkpoint.name,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = primaryText
              )
            )
            Text(
              text = "${checkpoint.locationCode} • Pace: ${checkpoint.targetIntervalMins}m",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDark) CyanBright else Color(0xFF0077B6),
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        if (checkpoint.isCompleted) {
          val dateStr = checkpoint.completedAtEpochMs?.let {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
          } ?: "Done"
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(TacticalEmerald.copy(alpha = if (isDark) 0.25f else 0.85f))
              .border(1.dp, TacticalEmerald, RoundedCornerShape(10.dp))
              .padding(horizontal = 10.dp, vertical = 5.dp)
          ) {
            Text(
              text = "✓ $dateStr",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDark) TacticalEmerald else Color.White,
                fontWeight = FontWeight.Black
              )
            )
          }
        } else {
          CatchablePulseButton(
            text = tr("Verify", "যাচাই"),
            icon = Icons.Default.QrCodeScanner,
            primaryColor = CyberCyan,
            secondaryColor = NeonViolet,
            onClick = onVerifyClick,
            testTag = "verify_cp_${checkpoint.sequenceOrder}"
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(
            if (isDark) Color.White.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.65f)
          )
          .border(
            1.dp,
            if (isDark) Color.White.copy(alpha = 0.18f) else Color(0xFF93C5FD),
            RoundedCornerShape(14.dp)
          )
          .padding(12.dp)
      ) {
        Text(
          text = tr("INSPECTION PROTOCOLS:", "পরিদর্শন তালিকা:"),
          style = MaterialTheme.typography.labelSmall.copy(
            color = if (isDark) VioletBright else Color(0xFF6D28D9),
            fontWeight = FontWeight.Black
          )
        )
        taskList.forEach { task ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = if (checkpoint.isCompleted) Icons.Default.CheckCircle else Icons.Default.DirectionsWalk,
              contentDescription = null,
              tint = if (checkpoint.isCompleted) TacticalEmerald else (if (isDark) CyanBright else Color(0xFF0077B6)),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = task,
              style = MaterialTheme.typography.bodySmall.copy(
                color = if (checkpoint.isCompleted) secondaryText else primaryText
              )
            )
          }
        }
      }

      if (checkpoint.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Note: ${checkpoint.notes}",
          style = MaterialTheme.typography.labelSmall.copy(
            color = if (isDark) CyanBright else Color(0xFF0077B6),
            fontFamily = JetBrainsMonoFontFamily
          )
        )
      }
    }
  }
}

@Composable
private fun VerificationDialog(
  checkpoint: CheckpointEntity,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit
) {
  var notes by remember { mutableStateOf("") }
  var simulatedTokenScanned by remember { mutableStateOf(false) }

  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = if (isDark) Color(0xFF0E1B36) else Color.White,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Nfc, contentDescription = null, tint = CyberCyan)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          tr("Verify Checkpoint #${checkpoint.sequenceOrder}", "চেকপয়েন্ট #${checkpoint.sequenceOrder} যাচাই করুন"),
          color = primaryText,
          fontWeight = FontWeight.ExtraBold
        )
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = checkpoint.name,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = primaryText)
        )
        Text(
          text = "Required Token: ${checkpoint.nfcTagId} / QR: ${checkpoint.qrCode}",
          style = MaterialTheme.typography.labelSmall.copy(color = if (isDark) CyberCyan else Color(0xFF0284C7), fontFamily = JetBrainsMonoFontFamily)
        )

        GlassCard(
          accentColor = if (simulatedTokenScanned) TacticalEmerald else CyberCyan,
          cornerRadius = 14.dp,
          glowAlpha = 0.24f,
          onClick = { simulatedTokenScanned = true },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("simulate_nfc_scan")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (simulatedTokenScanned) Icons.Default.CheckCircle else Icons.Default.Nfc,
              contentDescription = null,
              tint = if (simulatedTokenScanned) TacticalEmerald else CyberCyan,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (simulatedTokenScanned) {
                  tr("✓ NFC TOKEN VERIFIED", "✓ এনএফসি টোকেন যাচাইকৃত")
                } else {
                  tr("TAP TO SIMULATE NFC/QR SCAN", "এনএফসি/কিউআর স্ক্যান করতে ট্যাপ করুন")
                },
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (simulatedTokenScanned) TacticalEmerald else primaryText
                )
              )
              Text(
                text = if (simulatedTokenScanned) {
                  tr("Physical proximity confirmed (GPS < 5m)", "শারীরিক অবস্থান নিশ্চিত করা হয়েছে")
                } else {
                  tr("Tap to trigger hardware token reader", "হার্ডওয়্যার টোকেন রিডার সক্রিয় করুন")
                },
                style = MaterialTheme.typography.labelSmall.copy(color = secondaryText)
              )
            }
          }
        }

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text(tr("Observation Notes (Optional)", "পর্যবেক্ষণ নোট (ঐচ্ছিক)")) },
          placeholder = { Text("e.g. Doors secure, lighting verified") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = DarkCardBorder,
            focusedTextColor = primaryText,
            unfocusedTextColor = primaryText
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("checkpoint_notes_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(notes.ifEmpty { "NFC token verified, inspection tasks passed." })
        },
        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Slate950),
        modifier = Modifier.testTag("confirm_verification_button")
      ) {
        Text(tr("Complete Verification", "যাচাই সম্পন্ন করুন"), fontWeight = FontWeight.Black)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(tr("Cancel", "বাতিল"), color = GlassTextMuted())
      }
    }
  )
}
