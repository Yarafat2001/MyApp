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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.IncidentEntity
import com.example.data.model.IncidentPriority
import com.example.data.model.IncidentStatus
import com.example.data.model.IncidentType
import com.example.ui.components.CatchablePulseButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassSegmentedTabs
import com.example.ui.components.InteractiveGradientText
import com.example.ui.components.PulsingDot
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyanBright
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.PanicRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.VioletBright
import com.example.ui.theme.tr
import com.example.ui.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncidentsScreen(
  viewModel: SecurityViewModel,
  incidents: List<IncidentEntity>,
  modifier: Modifier = Modifier
) {
  var selectedFilterTab by remember { mutableIntStateOf(0) }
  var showNewIncidentDialog by remember { mutableStateOf(false) }

  val activeCount = incidents.count { it.status != IncidentStatus.RESOLVED }
  val criticalCount = incidents.count { it.priority == IncidentPriority.CRITICAL }

  val filteredIncidents = remember(incidents, selectedFilterTab) {
    when (selectedFilterTab) {
      0 -> incidents
      1 -> incidents.filter { it.status == IncidentStatus.OPEN || it.status == IncidentStatus.RESPONDING }
      else -> incidents.filter { it.status == IncidentStatus.RESOLVED }
    }
  }

  val primaryText = GlassTextPrimary()

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    GlassCard(
      accentColor = PanicRed,
      cornerRadius = 24.dp,
      glowAlpha = 0.24f,
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
              text = tr("INCIDENT DISPATCH", "ইনসিডেন্ট ডিসপ্যাচ"),
              colors = if (LocalIsDarkTheme.current) {
                listOf(Color.White, PanicRed, AlertAmber)
              } else {
                listOf(Color(0xFF071124), PanicRed, Color(0xFFD97706))
              },
              fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = tr("REAL-TIME TRIAGE & PROTOCOL WORKFLOW", "তাৎক্ষণিক ট্রায়াজ এবং প্রোটোকল"),
              style = MaterialTheme.typography.labelSmall.copy(
                color = NeonMagenta,
                fontWeight = FontWeight.ExtraBold
              )
            )
          }

          CatchablePulseButton(
            text = tr("File Alert", "অ্যালার্ট দিন"),
            icon = Icons.Default.Add,
            primaryColor = PanicRed,
            secondaryColor = NeonMagenta,
            isUrgent = true,
            onClick = { showNewIncidentDialog = true },
            testTag = "report_incident_button"
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IncidentStatChip(
            tr("ACTIVE ALERTS", "সক্রিয় অ্যালার্ট"),
            tr("$activeCount Active", "$activeCount সক্রিয়"),
            if (activeCount > 0) PanicRed else TacticalEmerald,
            Modifier.weight(1f)
          )
          IncidentStatChip(
            tr("P1 CRITICAL", "পি-১ জরুরি"),
            "$criticalCount P1",
            NeonMagenta,
            Modifier.weight(1f)
          )
          IncidentStatChip(
            tr("AUTO-DISPATCH", "অটো-ডিসপ্যাচ"),
            tr("Central Linked", "সংযুক্ত"),
            CyberCyan,
            Modifier.weight(1f)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    GlassSegmentedTabs(
      tabs = listOf(
        tr("All (${incidents.size})", "সব (${incidents.size})"),
        tr("Open ($activeCount)", "চলমান ($activeCount)"),
        tr("Resolved (${incidents.count { it.status == IncidentStatus.RESOLVED }})", "সমাধান (${incidents.count { it.status == IncidentStatus.RESOLVED }})")
      ),
      selectedIndex = selectedFilterTab,
      onTabSelected = { selectedFilterTab = it },
      accentColor = PanicRed
    )

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(bottom = 28.dp),
      modifier = Modifier
        .fillMaxSize()
        .testTag("incidents_list")
    ) {
      items(filteredIncidents, key = { it.id }) { incident ->
        IncidentCard(
          incident = incident,
          onAdvanceStep = { viewModel.advanceIncidentStep(incident) },
          onStatusChange = { newStatus -> viewModel.updateIncidentStatus(incident.id, newStatus) }
        )
      }
    }
  }

  if (showNewIncidentDialog) {
    CreateIncidentDialog(
      onDismiss = { showNewIncidentDialog = false },
      onCreate = { _, type, _, location, _, _ ->
        viewModel.triggerQuickSOS(type, location)
        showNewIncidentDialog = false
      }
    )
  }
}

@Composable
private fun IncidentCard(
  incident: IncidentEntity,
  onAdvanceStep: () -> Unit,
  onStatusChange: (IncidentStatus) -> Unit
) {
  val sopSteps = remember(incident.sopStepsRaw) {
    incident.sopStepsRaw.split("|").map { it.trim() }.filter { it.isNotBlank() }
  }
  val totalSteps = sopSteps.size.coerceAtLeast(1)
  val dateStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(incident.reportedAtEpochMs))

  val priorityColor = when (incident.priority) {
    IncidentPriority.CRITICAL -> PanicRed
    IncidentPriority.HIGH -> AlertAmber
    IncidentPriority.MEDIUM -> NeonMagenta
    IncidentPriority.LOW -> CyberCyan
  }

  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val secondaryText = GlassTextSecondary()
  val mutedText = GlassTextMuted()

  GlassCard(
    accentColor = if (incident.status == IncidentStatus.RESOLVED) TacticalEmerald else priorityColor,
    cornerRadius = 22.dp,
    glowAlpha = if (incident.status == IncidentStatus.RESOLVED) 0.14f else 0.24f,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("incident_card_${incident.id}")
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
          PulsingDot(color = priorityColor)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = incident.priority.label,
            style = MaterialTheme.typography.labelMedium.copy(
              color = priorityColor,
              fontWeight = FontWeight.Black
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• $dateStr",
            style = MaterialTheme.typography.labelSmall.copy(
              color = mutedText,
              fontFamily = JetBrainsMonoFontFamily
            )
          )
        }

        val statusColor = when (incident.status) {
          IncidentStatus.OPEN -> PanicRed
          IncidentStatus.RESPONDING -> AlertAmber
          IncidentStatus.CONTAINED -> CyberCyan
          IncidentStatus.RESOLVED -> TacticalEmerald
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(statusColor.copy(alpha = if (isDark) 0.25f else 0.85f))
            .border(1.dp, statusColor, RoundedCornerShape(10.dp))
            .clickable {
              val nextStatus = when (incident.status) {
                IncidentStatus.OPEN -> IncidentStatus.RESPONDING
                IncidentStatus.RESPONDING -> IncidentStatus.CONTAINED
                IncidentStatus.CONTAINED -> IncidentStatus.RESOLVED
                IncidentStatus.RESOLVED -> IncidentStatus.OPEN
              }
              onStatusChange(nextStatus)
            }
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = incident.status.name,
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isDark) statusColor else Color.White,
              fontWeight = FontWeight.Black
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = incident.title,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.ExtraBold,
          color = primaryText
        )
      )

      Text(
        text = "📍 ${incident.location} • Unit: ${incident.autoDispatchedOfficer}",
        style = MaterialTheme.typography.labelSmall.copy(
          color = if (isDark) CyanBright else Color(0xFF0077B6),
          fontWeight = FontWeight.Bold
        )
      )

      if (incident.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = incident.notes,
          style = MaterialTheme.typography.bodySmall.copy(
            color = secondaryText
          )
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Column(
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = tr("AUTOMATED SOP CHECKLIST", "স্বয়ংক্রিয় এসওপি চেকলিস্ট"),
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isDark) VioletBright else Color(0xFF6D28D9),
              fontWeight = FontWeight.Black
            )
          )
          Text(
            text = "${incident.completedStepsCount} / $totalSteps",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (incident.completedStepsCount >= totalSteps) TacticalEmerald else (if (isDark) CyanBright else Color(0xFF0077B6)),
              fontFamily = JetBrainsMonoFontFamily,
              fontWeight = FontWeight.Black
            )
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        sopSteps.forEachIndexed { index, step ->
          val isDone = index < incident.completedStepsCount
          val isCurrent = index == incident.completedStepsCount

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
          ) {
            Icon(
              imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Security,
              contentDescription = null,
              tint = if (isDone) TacticalEmerald else if (isCurrent) CyberCyan else mutedText,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "${index + 1}. $step",
              style = MaterialTheme.typography.bodySmall.copy(
                color = if (isDone) mutedText else if (isCurrent) primaryText else secondaryText,
                fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Normal
              )
            )
          }
        }

        if (incident.completedStepsCount < totalSteps) {
          Spacer(modifier = Modifier.height(10.dp))
          CatchablePulseButton(
            text = tr("Complete & Advance Next SOP Step", "পরবর্তী এসওপি ধাপ সম্পন্ন করুন"),
            icon = Icons.Default.FastForward,
            primaryColor = CyberCyan,
            secondaryColor = NeonViolet,
            onClick = onAdvanceStep,
            modifier = Modifier.fillMaxWidth(),
            testTag = "advance_sop_${incident.id}"
          )
        }
      }
    }
  }
}

@Composable
private fun IncidentStatChip(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateIncidentDialog(
  onDismiss: () -> Unit,
  onCreate: (String, IncidentType, IncidentPriority, String, String, String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var location by remember { mutableStateOf("Sector Alpha") }
  var notes by remember { mutableStateOf("") }

  var selectedType by remember { mutableStateOf(IncidentType.INTRUSION) }
  var typeExpanded by remember { mutableStateOf(false) }

  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val menuBg = if (isDark) Color(0xFF132242) else Color.White

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = if (isDark) Color(0xFF0E1B36) else Color.White,
    title = { Text(tr("Log & Auto-Dispatch Incident", "ইনসিডেন্ট রিপোর্ট এবং ডিসপ্যাচ"), color = primaryText, fontWeight = FontWeight.ExtraBold) },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(tr("Incident Summary", "ঘটনার বিবরণ")) },
          placeholder = { Text("e.g. Unidentified movement at north fence") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = DarkCardBorder,
            focusedTextColor = primaryText,
            unfocusedTextColor = primaryText
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("incident_title_input")
        )

        ExposedDropdownMenuBox(
          expanded = typeExpanded,
          onExpandedChange = { typeExpanded = !typeExpanded }
        ) {
          OutlinedTextField(
            value = selectedType.title,
            onValueChange = {},
            readOnly = true,
            label = { Text(tr("Category", "ধরন")) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CyberCyan,
              unfocusedBorderColor = DarkCardBorder,
              focusedTextColor = primaryText,
              unfocusedTextColor = primaryText
            ),
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = typeExpanded,
            onDismissRequest = { typeExpanded = false },
            modifier = Modifier.background(menuBg)
          ) {
            IncidentType.entries.forEach { type ->
              DropdownMenuItem(
                text = { Text(type.title, color = primaryText) },
                onClick = {
                  selectedType = type
                  typeExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = location,
          onValueChange = { location = it },
          label = { Text(tr("Specific Location", "নির্দিষ্ট স্থান")) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = DarkCardBorder,
            focusedTextColor = primaryText,
            unfocusedTextColor = primaryText
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("incident_location_input")
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text(tr("Agent Field Notes", "ফিল্ড নোট")) },
          maxLines = 3,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = DarkCardBorder,
            focusedTextColor = primaryText,
            unfocusedTextColor = primaryText
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("incident_notes_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onCreate(
              title,
              selectedType,
              IncidentPriority.HIGH,
              location.ifEmpty { "Sector Alpha" },
              notes,
              "Acknowledge Central Dispatch|Verify sector sensors|Dispatch unit|Resolve & submit report"
            )
          }
        },
        enabled = title.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = PanicRed, contentColor = Color.White),
        modifier = Modifier.testTag("submit_incident_button")
      ) {
        Text(tr("Broadcast Alarm", "অ্যালার্ম পাঠান"), fontWeight = FontWeight.Black)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(tr("Cancel", "বাতিল"), color = GlassTextMuted())
      }
    }
  )
}
