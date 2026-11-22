package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.IncidentStatus
import com.example.ui.components.AuroraGlassBackground
import com.example.ui.components.FloatingCopilotButton
import com.example.ui.components.TacticalActionHubSheet
import com.example.ui.components.TacticalBanner
import com.example.ui.components.TacticalBottomNav
import com.example.ui.components.TacticalTopAppBar
import com.example.ui.screens.AccessControlScreen
import com.example.ui.screens.AutomationHubScreen
import com.example.ui.screens.ChatbotScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IncidentsScreen
import com.example.ui.screens.PatrolScreen
import com.example.ui.screens.ShiftReportScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SecOpsScreen
import com.example.ui.viewmodel.SecurityViewModel
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

  private val viewModel: SecurityViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
    configureAppCheck(this, intent)

    enableEdgeToEdge()
    setContent {
      val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
      val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

      MyApplicationTheme(
        darkTheme = isDarkTheme,
        appLanguage = appLanguage
      ) {
        SentinelOpsApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun SentinelOpsApp(viewModel: SecurityViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val threatLevel by viewModel.threatLevel.collectAsStateWithLifecycle()
  val loneWorkerStatus by viewModel.loneWorkerStatus.collectAsStateWithLifecycle()
  val patrolElapsedSeconds by viewModel.patrolElapsedSeconds.collectAsStateWithLifecycle()
  val gates by viewModel.gates.collectAsStateWithLifecycle()
  val bannerMessage by viewModel.activeBannerMessage.collectAsStateWithLifecycle()

  val rules by viewModel.rules.collectAsStateWithLifecycle()
  val checkpoints by viewModel.checkpoints.collectAsStateWithLifecycle()
  val incidents by viewModel.incidents.collectAsStateWithLifecycle()
  val accessLogs by viewModel.accessLogs.collectAsStateWithLifecycle()
  val automationLogs by viewModel.automationLogs.collectAsStateWithLifecycle()

  val activeRuleCount = rules.count { it.isEnabled }
  val activeIncidentCount = incidents.count { it.status != IncidentStatus.RESOLVED }

  BackHandler(enabled = currentScreen != SecOpsScreen.DASHBOARD) {
    viewModel.navigateTo(SecOpsScreen.DASHBOARD)
  }

  var showTacticalHub by remember { mutableStateOf(false) }

  AuroraGlassBackground(threatLevel = threatLevel) {
    Scaffold(
      contentWindowInsets = WindowInsets(0.dp),
      topBar = {
        TacticalTopAppBar(
          threatLevel = threatLevel,
          activeRuleCount = activeRuleCount,
          incidentCount = activeIncidentCount,
          onThreatLevelClick = {
            val nextLevel = when (threatLevel) {
              com.example.data.model.ThreatLevel.NORMAL -> com.example.data.model.ThreatLevel.ELEVATED
              com.example.data.model.ThreatLevel.ELEVATED -> com.example.data.model.ThreatLevel.HIGH
              com.example.data.model.ThreatLevel.HIGH -> com.example.data.model.ThreatLevel.LOCKDOWN
              com.example.data.model.ThreatLevel.LOCKDOWN -> com.example.data.model.ThreatLevel.NORMAL
            }
            viewModel.setThreatLevel(nextLevel)
          },
          onToggleTheme = { viewModel.toggleTheme() },
          onToggleLanguage = { viewModel.toggleLanguage() }
        )
      },
      bottomBar = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.End
        ) {
          if (currentScreen != SecOpsScreen.AI_COPILOT) {
            FloatingCopilotButton(
              onClick = { viewModel.navigateTo(SecOpsScreen.AI_COPILOT) },
              modifier = Modifier.padding(end = 18.dp, bottom = 2.dp)
            )
          }
          TacticalBottomNav(
            currentScreen = currentScreen,
            onScreenSelected = { viewModel.navigateTo(it) },
            onOpenTacticalHub = { showTacticalHub = true },
            incidentBadgeCount = activeIncidentCount
          )
        }
      },
      containerColor = Color.Transparent
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        TacticalBanner(
          message = bannerMessage,
          onDismiss = { viewModel.dismissBanner() }
        )

        Box(modifier = Modifier.weight(1f)) {
          AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
              (fadeIn(animationSpec = tween(260)) + scaleIn(initialScale = 0.96f, animationSpec = tween(260))) togetherWith
                fadeOut(animationSpec = tween(180))
            },
            label = "screen_transition"
          ) { targetScreen ->
            when (targetScreen) {
              SecOpsScreen.DASHBOARD -> DashboardScreen(
                viewModel = viewModel,
                loneWorkerStatus = loneWorkerStatus,
                threatLevel = threatLevel,
                checkpoints = checkpoints,
                activeRuleCount = activeRuleCount,
                incidentCount = activeIncidentCount,
                onNavigate = { viewModel.navigateTo(it) }
              )
              SecOpsScreen.AUTOMATION -> AutomationHubScreen(
                viewModel = viewModel,
                rules = rules,
                logs = automationLogs
              )
              SecOpsScreen.PATROLS -> PatrolScreen(
                viewModel = viewModel,
                checkpoints = checkpoints,
                patrolElapsedSeconds = patrolElapsedSeconds
              )
              SecOpsScreen.INCIDENTS -> IncidentsScreen(
                viewModel = viewModel,
                incidents = incidents
              )
              SecOpsScreen.ACCESS -> AccessControlScreen(
                viewModel = viewModel,
                gates = gates,
                accessLogs = accessLogs
              )
              SecOpsScreen.AI_COPILOT -> ChatbotScreen(
                viewModel = viewModel
              )
              SecOpsScreen.SHIFT_REPORT -> ShiftReportScreen(
                viewModel = viewModel,
                threatLevel = threatLevel,
                checkpoints = checkpoints,
                incidents = incidents,
                rules = rules,
                accessLogs = accessLogs,
                automationLogs = automationLogs
              )
            }
          }
        }
      }
    }

    if (showTacticalHub) {
      TacticalActionHubSheet(
        onDismiss = { showTacticalHub = false },
        onScreenSelected = { screen ->
          viewModel.navigateTo(screen)
          showTacticalHub = false
        },
        onOpenLoneWorker = {
          viewModel.checkInLoneWorker()
          viewModel.showBanner("Lone Worker Check-In Confirmed • Timer Reset")
          showTacticalHub = false
        },
        onPanicLockdown = {
          viewModel.setThreatLevel(com.example.data.model.ThreatLevel.LOCKDOWN)
          viewModel.triggerQuickSOS(com.example.data.model.IncidentType.INTRUSION, "Central Command - Panic SOS")
          showTacticalHub = false
        }
      )
    }
  }
}
