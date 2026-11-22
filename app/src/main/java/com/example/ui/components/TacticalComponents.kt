package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThreatLevel
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyanBright
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextPrimary
import com.example.ui.theme.GlassTextSecondary
import com.example.ui.theme.GlassWhiteHighlight
import com.example.ui.theme.GlassWhiteSubtle
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LightBgBottom
import com.example.ui.theme.LightBgMid
import com.example.ui.theme.LightBgTop
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.LightGlassSurfaceHigh
import com.example.ui.theme.LightGlassSurfaceLow
import com.example.ui.theme.LightGlassSurfaceMedium
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.PanicRed
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.VioletBright
import com.example.ui.theme.tr
import com.example.ui.viewmodel.SecOpsScreen
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure Crystal Aurora Backdrop supporting both Dark Obsidian Crystal and Light Pearl Sky-Prism themes.
 * High-saturation floating light orbs sit directly beneath translucent frosted glass cards.
 */
@Composable
fun AuroraGlassBackground(
  threatLevel: ThreatLevel,
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val infiniteTransition = rememberInfiniteTransition(label = "aurora_bg")
  val phase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6.28318f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 12000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "aurora_phase"
  )
  val pulse by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "aurora_pulse"
  )

  val primaryOrbColor by animateColorAsState(
    targetValue = when (threatLevel) {
      ThreatLevel.NORMAL -> if (isDark) CyberCyan else Color(0xFF00B4D8)
      ThreatLevel.ELEVATED -> AlertAmber
      ThreatLevel.HIGH -> PanicRed
      ThreatLevel.LOCKDOWN -> NeonMagenta
    },
    animationSpec = tween(600),
    label = "orb1_color"
  )

  val secondaryOrbColor by animateColorAsState(
    targetValue = when (threatLevel) {
      ThreatLevel.NORMAL -> if (isDark) NeonViolet else Color(0xFF8B5CF6)
      ThreatLevel.ELEVATED -> NeonMagenta
      ThreatLevel.HIGH -> AlertAmber
      ThreatLevel.LOCKDOWN -> PanicRed
    },
    animationSpec = tween(600),
    label = "orb2_color"
  )

  val baseGradient = if (isDark) {
    Brush.verticalGradient(
      colors = listOf(
        Color(0xFF060B18),
        Color(0xFF0A1326),
        Color(0xFF070E1E)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        LightBgTop,
        LightBgMid,
        LightBgBottom
      )
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(baseGradient)
      .drawBehind {
        val w = size.width
        val h = size.height

        // Top-right warm amber/gold ambient glow (matching reference screenshot)
        val amberCenter = Offset(w * 0.88f, h * 0.12f)
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFF59E0B).copy(alpha = if (isDark) 0.22f else 0.16f),
              Color(0xFFD97706).copy(alpha = 0.08f),
              Color.Transparent
            ),
            center = amberCenter,
            radius = w * 0.65f
          ),
          center = amberCenter,
          radius = w * 0.65f
        )

        // Center-left deep cobalt/sapphire blue glow
        val sapphireCenter = Offset(
          x = w * (0.18f + 0.10f * cos(phase)),
          y = h * (0.38f + 0.08f * sin(phase))
        )
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFF1D4ED8).copy(alpha = if (isDark) 0.28f * pulse else 0.20f),
              Color(0xFF2563EB).copy(alpha = 0.09f),
              Color.Transparent
            ),
            center = sapphireCenter,
            radius = w * 0.70f
          ),
          center = sapphireCenter,
          radius = w * 0.70f
        )

        // Bottom cyan/teal cyber orb
        val cyanCenter = Offset(
          x = w * (0.50f + 0.12f * sin(phase + 1.2f)),
          y = h * (0.84f + 0.06f * cos(phase + 1.2f))
        )
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              CyberCyan.copy(alpha = if (isDark) 0.22f else 0.18f),
              NeonViolet.copy(alpha = 0.08f),
              Color.Transparent
            ),
            center = cyanCenter,
            radius = w * 0.66f
          ),
          center = cyanCenter,
          radius = w * 0.66f
        )
      }
  ) {
    content()
  }
}

/**
 * Pure Crystal Glassmorphic Card with deep dark frosted glass,
 * high-contrast text visibility, and fine luminous specular rim border.
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  accentColor: Color = CyberCyan,
  cornerRadius: Dp = 22.dp,
  glowAlpha: Float = 0.20f,
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit
) {
  val isDark = LocalIsDarkTheme.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.975f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "glass_card_scale"
  )

  val shape = RoundedCornerShape(cornerRadius)

  val surfaceBrush = if (isDark) {
    Brush.linearGradient(
      colors = listOf(
        Color(0xFF132242).copy(alpha = 0.92f),
        Color(0xFF0E1B36).copy(alpha = 0.94f),
        Color(0xFF091326).copy(alpha = 0.96f)
      ),
      start = Offset(0f, 0f),
      end = Offset(800f, 900f)
    )
  } else {
    Brush.linearGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.96f),
        Color(0xFFF3F8FF).copy(alpha = 0.98f)
      ),
      start = Offset(0f, 0f),
      end = Offset(800f, 900f)
    )
  }

  val borderBrush = if (isDark) {
    Brush.linearGradient(
      colors = listOf(
        Color.White.copy(alpha = if (isPressed) 0.70f else 0.36f),
        accentColor.copy(alpha = if (isPressed) 0.85f else 0.52f),
        AlertAmber.copy(alpha = if (isPressed) 0.55f else 0.24f),
        Color.White.copy(alpha = 0.16f)
      ),
      start = Offset(0f, 0f),
      end = Offset(850f, 850f)
    )
  } else {
    Brush.linearGradient(
      colors = listOf(
        Color.White,
        accentColor.copy(alpha = 0.55f),
        Color(0xFF93C5FD)
      ),
      start = Offset(0f, 0f),
      end = Offset(850f, 850f)
    )
  }

  val clickableModifier = if (onClick != null) {
    Modifier.clickable(
      interactionSource = interactionSource,
      indication = null,
      onClick = onClick
    )
  } else {
    Modifier
  }

  Box(
    modifier = modifier
      .scale(scale)
      .clip(shape)
      .background(surfaceBrush)
      .drawBehind {
        val w = size.width
        val h = size.height

        // Top-Left Cyber Accent Refraction
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              accentColor.copy(alpha = if (isDark) glowAlpha * 0.55f else glowAlpha * 0.35f),
              Color.Transparent
            ),
            center = Offset(w * 0.14f, h * 0.12f),
            radius = w * 0.55f
          ),
          center = Offset(w * 0.14f, h * 0.12f),
          radius = w * 0.55f
        )

        // Subtle Top-Right Warm Golden-Amber Refraction (matching reference design)
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              AlertAmber.copy(alpha = if (isDark) 0.10f else 0.06f),
              Color.Transparent
            ),
            center = Offset(w * 0.86f, h * 0.18f),
            radius = w * 0.45f
          ),
          center = Offset(w * 0.86f, h * 0.18f),
          radius = w * 0.45f
        )

        // Crisp Top Specular Glass Rim Line
        drawLine(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color.Transparent,
              Color.White.copy(alpha = if (isDark) 0.65f else 0.95f),
              accentColor.copy(alpha = 0.75f),
              AlertAmber.copy(alpha = 0.55f),
              Color.Transparent
            )
          ),
          start = Offset(w * 0.06f, 1.2.dp.toPx()),
          end = Offset(w * 0.94f, 1.2.dp.toPx()),
          strokeWidth = 1.8.dp.toPx()
        )
      }
      .border(
        width = 1.3.dp,
        brush = borderBrush,
        shape = shape
      )
      .then(clickableModifier)
  ) {
    content()
  }
}

/**
 * Interactive Shimmering Gradient Typography that responds to touch with a spring bounce
 * and renders a multi-stop luminous gradient brush across the glyphs.
 */
@Composable
fun InteractiveGradientText(
  text: String,
  modifier: Modifier = Modifier,
  colors: List<Color> = listOf(Color.White, CyberCyan, AlertAmber),
  fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
  fontWeight: FontWeight = FontWeight.Black,
  onClick: (() -> Unit)? = null
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "grad_text_scale"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "text_shimmer")
  val shift by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 600f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "text_shimmer_shift"
  )

  val baseMod = if (onClick != null) {
    modifier
      .scale(scale)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
  } else {
    modifier.scale(scale)
  }

  Text(
    text = text,
    modifier = baseMod,
    style = MaterialTheme.typography.titleLarge.copy(
      fontSize = fontSize,
      fontWeight = fontWeight,
      brush = Brush.linearGradient(
        colors = colors,
        start = Offset(shift * 0.3f, 0f),
        end = Offset(shift + 320f, 120f)
      )
    )
  )
}


/**
 * Safe, Catchable Animated Icon Button with continuous Sonar Ripple Ring,
 * sweeping Shimmer Light Beam, bouncing icon badge, and tactile spring press!
 */
@Composable
fun CatchablePulseButton(
  text: String,
  icon: ImageVector,
  primaryColor: Color,
  secondaryColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  isUrgent: Boolean = false,
  testTag: String = ""
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val pressScale by animateFloatAsState(
    targetValue = if (isPressed) 0.91f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
    ),
    label = "catchable_btn_scale"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "catchable_anim")
  val sonarProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = if (isUrgent) 1200 else 2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "sonar_ring"
  )

  val iconBounce by infiniteTransition.animateFloat(
    initialValue = -2.5f,
    targetValue = 2.5f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = if (isUrgent) 450 else 850, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "icon_bounce"
  )

  val shimmerX by infiniteTransition.animateFloat(
    initialValue = -0.4f,
    targetValue = 1.4f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_sweep"
  )

  val tagMod = if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier
  val shape = RoundedCornerShape(18.dp)

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .scale(pressScale)
      .then(tagMod)
      .drawBehind {
        // Expanding Sonar Halo Ring outside button edge
        val extraRadius = 8.dp.toPx() * sonarProgress
        drawRoundRect(
          color = primaryColor.copy(alpha = (1f - sonarProgress) * 0.48f),
          topLeft = Offset(-extraRadius, -extraRadius),
          size = androidx.compose.ui.geometry.Size(
            size.width + extraRadius * 2,
            size.height + extraRadius * 2
          ),
          cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
          style = Stroke(width = 2.2.dp.toPx())
        )
      }
      .clip(shape)
      .background(
        Brush.linearGradient(
          colors = listOf(
            primaryColor,
            secondaryColor
          )
        )
      )
      .drawBehind {
        // Sweeping diagonal glass shimmer beam
        val beamStart = size.width * shimmerX
        drawLine(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color.Transparent,
              Color.White.copy(alpha = 0.42f),
              Color.Transparent
            ),
            startX = beamStart - 60f,
            endX = beamStart + 60f
          ),
          start = Offset(beamStart - 30f, 0f),
          end = Offset(beamStart + 30f, size.height),
          strokeWidth = 38.dp.toPx()
        )
      }
      .border(
        width = 1.5.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            Color.White.copy(alpha = 0.90f),
            primaryColor.copy(alpha = 0.6f),
            Color.White.copy(alpha = 0.40f)
          )
        ),
        shape = shape
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(34.dp)
          .offset(y = iconBounce.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.24f))
          .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = text,
          tint = Color.White,
          modifier = Modifier.size(19.dp)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column {
        Text(
          text = text,
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Black,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 15.sp
          )
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.White.copy(alpha = 0.88f),
              fontSize = 10.sp
            )
          )
        }
      }
    }
  }
}

/**
 * Pure Crystal Segmented Pill Switcher with adaptive Dark/Light styling.
 */
@Composable
fun GlassSegmentedTabs(
  tabs: List<String>,
  selectedIndex: Int,
  onTabSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = CyberCyan,
  testTags: List<String> = emptyList()
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = GlassTextPrimary()
  val mutedText = GlassTextMuted()

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(
        if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.65f)
      )
      .border(
        width = 1.2.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            Color.White.copy(alpha = if (isDark) 0.55f else 0.95f),
            accentColor.copy(alpha = 0.45f)
          )
        ),
        shape = RoundedCornerShape(18.dp)
      )
      .padding(5.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    tabs.forEachIndexed { index, title ->
      val isSelected = selectedIndex == index
      val interactionSource = remember { MutableInteractionSource() }
      val isPressed by interactionSource.collectIsPressedAsState()
      val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tab_scale_$index"
      )

      val bgBrush = if (isSelected) {
        Brush.horizontalGradient(
          colors = listOf(
            accentColor.copy(alpha = if (isDark) 0.42f else 0.85f),
            NeonViolet.copy(alpha = if (isDark) 0.38f else 0.78f)
          )
        )
      } else {
        Brush.horizontalGradient(
          colors = listOf(Color.Transparent, Color.Transparent)
        )
      }

      val tagModifier = if (index < testTags.size && testTags[index].isNotEmpty()) {
        Modifier.testTag(testTags[index])
      } else {
        Modifier
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .scale(scale)
          .then(tagModifier)
          .clip(RoundedCornerShape(14.dp))
          .background(bgBrush)
          .then(
            if (isSelected) {
              Modifier.border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                  colors = listOf(Color.White.copy(alpha = 0.85f), accentColor)
                ),
                shape = RoundedCornerShape(14.dp)
              )
            } else {
              Modifier
            }
          )
          .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = { onTabSelected(index) }
          )
          .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium.copy(
            color = if (isSelected) Color.White else if (isDark) mutedText else LightTextPrimary,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
            fontSize = 13.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun TacticalTopAppBar(
  threatLevel: ThreatLevel,
  activeRuleCount: Int,
  incidentCount: Int,
  onThreatLevelClick: () -> Unit,
  onToggleTheme: () -> Unit = {},
  onToggleLanguage: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val currentLang = LocalAppLanguage.current
  val primaryText = GlassTextPrimary()

  val infiniteTransition = rememberInfiniteTransition(label = "radar_rot")
  val radarAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radar_angle"
  )

  val headerAccent = when (threatLevel) {
    ThreatLevel.NORMAL -> CyberCyan
    ThreatLevel.ELEVATED -> AlertAmber
    ThreatLevel.HIGH -> PanicRed
    ThreatLevel.LOCKDOWN -> NeonMagenta
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.statusBars)
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    GlassCard(
      accentColor = headerAccent,
      cornerRadius = 24.dp,
      glowAlpha = 0.24f,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Brand Emblem with Live Rotating Radar Sweep
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(
                  Brush.linearGradient(
                    colors = listOf(
                      CyberCyan.copy(alpha = 0.35f),
                      NeonViolet.copy(alpha = 0.40f)
                    )
                  )
                )
                .border(
                  width = 1.4.dp,
                  brush = Brush.linearGradient(
                    colors = listOf(Color.White, CyberCyan, NeonViolet)
                  ),
                  shape = RoundedCornerShape(15.dp)
                ),
              contentAlignment = Alignment.Center
            ) {
              Canvas(modifier = Modifier.size(36.dp)) {
                drawCircle(
                  color = CyberCyan.copy(alpha = 0.35f),
                  style = Stroke(width = 1.2.dp.toPx())
                )
                rotate(radarAngle) {
                  drawArc(
                    brush = Brush.sweepGradient(
                      colors = listOf(
                        Color.Transparent,
                        CyberCyan.copy(alpha = 0.1f),
                        CyberCyan.copy(alpha = 0.75f)
                      )
                    ),
                    startAngle = 0f,
                    sweepAngle = 115f,
                    useCenter = true
                  )
                }
              }
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "SentinelOps Emblem",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = tr("Sentinel", "সেনটিনেল"),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.2).sp,
                    color = if (isDark) AlertAmber else Color(0xFFD97706),
                    fontSize = 19.sp
                  )
                )
                Text(
                  text = tr("Ops", "অপস"),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.2).sp,
                    color = primaryText,
                    fontSize = 19.sp
                  )
                )
              }
              Text(
                text = tr("TACTICAL GLASS COMMAND", "ক্রিস্টাল গ্লাস কমান্ড"),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp,
                  color = if (isDark) CyberCyan else Color(0xFF0284C7),
                  fontSize = 10.sp
                )
              )
            }
          }

          // Interactive Unified Frosted Capsule (Language + Theme) + DEFCON
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Unified Frosted Control Capsule (matching reference top-right capsule)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(
                  if (isDark) Color(0xF0121F3D) else Color(0xF2F0F7FF)
                )
                .border(
                  width = 1.2.dp,
                  brush = Brush.horizontalGradient(
                    listOf(
                      Color.White.copy(alpha = if (isDark) 0.5f else 0.85f),
                      CyberCyan.copy(alpha = 0.5f)
                    )
                  ),
                  shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 4.dp, vertical = 3.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                // Language Switcher Chip
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onToggleLanguage)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("language_toggle_button")
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Language,
                      contentDescription = "Language",
                      tint = if (isDark) Color.White else Color(0xFF0F172A),
                      modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (currentLang == AppLanguage.ENGLISH) "বাং" else "EN",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                      )
                    )
                  }
                }

                // Theme Switcher Circle (Vibrant filled circle like screenshot)
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                      if (isDark) Color(0xFF0284C7) else Color(0xFF2563EB)
                    )
                    .clickable(onClick = onToggleTheme)
                    .testTag("theme_toggle_button"),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Theme",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }

            // Interactive DEFCON Posture Badge
            ThreatLevelBadge(
              threatLevel = threatLevel,
              onClick = onThreatLevelClick
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Glass Telemetry Strip
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
              if (isDark) Color.White.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.55f)
            )
            .border(
              0.8.dp,
              if (isDark) Color.White.copy(alpha = 0.22f) else Color(0xFF93C5FD),
              RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 7.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            PulsingDot(color = TacticalEmerald)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = tr("LIVE NEURAL SYNC • 99.4%", "লাইভ নিউরাল সিঙ্ক • ৯৯.৪%"),
              style = MaterialTheme.typography.labelSmall.copy(
                color = primaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(
                      CyberCyan.copy(alpha = if (isDark) 0.26f else 0.85f),
                      NeonViolet.copy(alpha = if (isDark) 0.26f else 0.85f)
                    )
                  )
                )
                .border(0.8.dp, CyberCyan, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = tr("⚡ $activeRuleCount RULES", "⚡ $activeRuleCount রুলস"),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp
                )
              )
            }

            if (incidentCount > 0) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(PanicRed.copy(alpha = if (isDark) 0.32f else 0.88f))
                  .border(0.8.dp, PanicRed, RoundedCornerShape(8.dp))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = tr("🚨 $incidentCount ALARMS", "🚨 $incidentCount অ্যালার্ম"),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ThreatLevelBadge(
  threatLevel: ThreatLevel,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.90f else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "threat_badge_scale"
  )

  val (glowColor, secondaryColor) = when (threatLevel) {
    ThreatLevel.NORMAL -> Pair(TacticalEmerald, CyberCyan)
    ThreatLevel.ELEVATED -> Pair(AlertAmber, Color(0xFFF97316))
    ThreatLevel.HIGH -> Pair(PanicRed, NeonMagenta)
    ThreatLevel.LOCKDOWN -> Pair(NeonMagenta, PanicRed)
  }

  Box(
    modifier = modifier
      .scale(scale)
      .testTag("threat_level_button")
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.horizontalGradient(
          colors = listOf(glowColor, secondaryColor)
        )
      )
      .border(
        width = 1.2.dp,
        color = Color.White.copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      PulsingDot(color = Color.White)
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = tr("DEFCON ${threatLevel.levelNumber}", "ডেফকন ${threatLevel.levelNumber}"),
        style = MaterialTheme.typography.labelMedium.copy(
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 11.sp
        )
      )
    }
  }
}

@Composable
fun PulsingDot(
  color: Color,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val scale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.45f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot_scale"
  )
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot_alpha"
  )

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.size(12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(12.dp)
        .scale(scale)
        .clip(CircleShape)
        .background(color.copy(alpha = alpha * 0.40f))
    )
    Box(
      modifier = Modifier
        .size(7.dp)
        .clip(CircleShape)
        .background(color)
    )
  }
}

/**
 * Glassy Pill Chip matching the modern rounded badge aesthetic from the reference design.
 * High contrast, crystal border, and optional pulsing status dot.
 */
@Composable
fun GlassPillChip(
  text: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  hasDot: Boolean = false,
  dotColor: Color = TacticalEmerald,
  accentColor: Color = CyberCyan,
  isSelected: Boolean = false,
  onClick: (() -> Unit)? = null
) {
  val isDark = LocalIsDarkTheme.current
  val chipShape = RoundedCornerShape(50)
  val baseModifier = if (onClick != null) {
    modifier
      .clip(chipShape)
      .clickable(onClick = onClick)
  } else {
    modifier.clip(chipShape)
  }

  val bgBrush = if (isSelected) {
    Brush.horizontalGradient(
      listOf(
        accentColor.copy(alpha = if (isDark) 0.55f else 0.85f),
        NeonViolet.copy(alpha = if (isDark) 0.45f else 0.75f)
      )
    )
  } else {
    Brush.horizontalGradient(
      listOf(
        if (isDark) Color(0xF0121F3D) else Color(0xF2F0F7FF),
        if (isDark) Color(0xF00A142A) else Color(0xF2E2EDF9)
      )
    )
  }

  Row(
    modifier = baseModifier
      .background(bgBrush)
      .border(
        width = 1.2.dp,
        brush = Brush.horizontalGradient(
          listOf(
            Color.White.copy(alpha = if (isDark) 0.45f else 0.85f),
            accentColor.copy(alpha = 0.5f)
          )
        ),
        shape = chipShape
      )
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    if (hasDot) {
      Box(
        modifier = Modifier
          .size(7.dp)
          .clip(CircleShape)
          .background(dotColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
    } else if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) Color.White else (if (isDark) accentColor else Color(0xFF0284C7)),
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
    }

    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        color = if (isSelected || isDark) Color.White else Color(0xFF0F172A)
      )
    )
  }
}

/**
 * Floating Copilot Action Pill directly above the navigation bar dock,
 * matching the user's reference screenshot design with sparkling AI indicator.
 */
@Composable
fun FloatingCopilotButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "copilot_press"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "copilot_glow")
  val sparkleAngle by infiniteTransition.animateFloat(
    initialValue = -15f,
    targetValue = 15f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "sparkle_angle"
  )

  Box(
    modifier = modifier
      .scale(scale)
      .testTag("floating_copilot_btn")
      .clip(RoundedCornerShape(50))
      .background(
        Brush.horizontalGradient(
          colors = listOf(
            Color(0xFF0284C7),
            Color(0xFF2563EB),
            Color(0xFFD97706)
          )
        )
      )
      .border(
        width = 1.6.dp,
        brush = Brush.horizontalGradient(
          listOf(
            Color.White.copy(alpha = 0.95f),
            CyberCyan,
            AlertAmber,
            Color.White.copy(alpha = 0.8f)
          )
        ),
        shape = RoundedCornerShape(50)
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 14.dp, vertical = 9.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "AI Copilot",
          tint = Color.White,
          modifier = Modifier
            .size(17.dp)
            .scale(if (isPressed) 0.85f else 1f)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = tr("Copilot", "কোপাইলট"),
        style = MaterialTheme.typography.labelLarge.copy(
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 13.sp,
          letterSpacing = 0.4.sp
        )
      )
    }
  }
}

/**
 * Modern High-Contrast Floating Glass Dock with 4 primary tabs + center elevated Grid Action Launcher,
 * matching the user's reference screenshot aesthetic.
 */
@Composable
fun TacticalBottomNav(
  currentScreen: SecOpsScreen,
  onScreenSelected: (SecOpsScreen) -> Unit,
  incidentBadgeCount: Int = 0,
  onOpenTacticalHub: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val leftItems = listOf(
    NavigationItem(tr("Command", "কমান্ড"), SecOpsScreen.DASHBOARD, Icons.Default.Dashboard, CyberCyan, "nav_dashboard"),
    NavigationItem(tr("Patrols", "টহল"), SecOpsScreen.PATROLS, Icons.Default.DirectionsWalk, TacticalEmerald, "nav_patrols")
  )
  val rightItems = listOf(
    NavigationItem(tr("Alerts", "অ্যালার্ট"), SecOpsScreen.INCIDENTS, Icons.Default.NotificationsActive, PanicRed, "nav_incidents"),
    NavigationItem(tr("Reports", "রিপোর্ট"), SecOpsScreen.SHIFT_REPORT, Icons.Default.Summarize, CyanBright, "nav_report")
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars)
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .testTag("bottom_nav_bar")
  ) {
    val dockShape = RoundedCornerShape(30.dp)

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(dockShape)
        .background(
          if (isDark) {
            Brush.verticalGradient(
              listOf(
                Color(0xF0121E3B),
                Color(0xF5081023)
              )
            )
          } else {
            Brush.verticalGradient(
              listOf(
                Color(0xF5FFFFFF),
                Color(0xF2E6F0FA)
              )
            )
          }
        )
        .drawBehind {
          drawLine(
            brush = Brush.horizontalGradient(
              colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = if (isDark) 0.85f else 1f),
                CyberCyan.copy(alpha = 0.75f),
                NeonViolet.copy(alpha = 0.75f),
                Color.Transparent
              )
            ),
            start = Offset(size.width * 0.05f, 1.2.dp.toPx()),
            end = Offset(size.width * 0.95f, 1.2.dp.toPx()),
            strokeWidth = 2.dp.toPx()
          )
        }
        .border(
          width = 1.4.dp,
          brush = Brush.linearGradient(
            listOf(
              Color.White.copy(alpha = if (isDark) 0.65f else 0.95f),
              CyberCyan.copy(alpha = 0.5f),
              NeonViolet.copy(alpha = 0.5f),
              Color.White.copy(alpha = 0.35f)
            )
          ),
          shape = dockShape
        )
        .padding(horizontal = 6.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Tabs (Command, Patrols)
        leftItems.forEach { item ->
          val isSelected = currentScreen == item.screen
          GlassNavBarItem(
            item = item,
            isSelected = isSelected,
            isDark = isDark,
            badgeCount = 0,
            onClick = { onScreenSelected(item.screen) },
            modifier = Modifier.weight(1f)
          )
        }

        // Center Raised Action Hub Button (GridView launcher from reference image)
        CenterRaisedHubButton(
          onClick = onOpenTacticalHub,
          modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Right Tabs (Alerts, Reports)
        rightItems.forEach { item ->
          val isSelected = currentScreen == item.screen
          GlassNavBarItem(
            item = item,
            isSelected = isSelected,
            isDark = isDark,
            badgeCount = if (item.screen == SecOpsScreen.INCIDENTS) incidentBadgeCount else 0,
            onClick = { onScreenSelected(item.screen) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

/**
 * Center Raised Action Launcher matching the 4-square GridView button from the reference screenshot.
 */
@Composable
private fun CenterRaisedHubButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.88f else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "center_hub_scale"
  )

  Box(
    modifier = modifier
      .offset(y = (-8).dp)
      .scale(scale)
      .size(52.dp)
      .clip(RoundedCornerShape(20.dp))
      .background(
        Brush.linearGradient(
          listOf(
            CyberCyan,
            Color(0xFF0284C7),
            NeonViolet
          )
        )
      )
      .border(
        width = 1.6.dp,
        brush = Brush.linearGradient(
          listOf(
            Color.White,
            CyberCyan,
            Color.White.copy(alpha = 0.7f)
          )
        ),
        shape = RoundedCornerShape(20.dp)
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .testTag("nav_action_hub"),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.GridView,
      contentDescription = "Tactical Quick Access Hub",
      tint = Color.White,
      modifier = Modifier.size(26.dp)
    )
  }
}

/**
 * Tactical Action Hub Modal Sheet: Pops up when center GridView is tapped,
 * providing one-touch access to Gates, Automation, AI Copilot, Shift Reports, and Panic SOS.
 */
@Composable
fun TacticalActionHubSheet(
  onDismiss: () -> Unit,
  onScreenSelected: (SecOpsScreen) -> Unit,
  onOpenLoneWorker: () -> Unit,
  onPanicLockdown: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val primaryText = if (isDark) Color.White else Color(0xFF0F172A)
  val mutedText = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(28.dp))
        .background(
          if (isDark) {
            Brush.verticalGradient(
              listOf(
                Color(0xF8111D3B),
                Color(0xFA091225)
              )
            )
          } else {
            Brush.verticalGradient(
              listOf(
                Color(0xFAFFFFFF),
                Color(0xF5EAF2FB)
              )
            )
          }
        )
        .border(
          width = 1.5.dp,
          brush = Brush.linearGradient(
            listOf(
              Color.White.copy(alpha = if (isDark) 0.65f else 0.95f),
              CyberCyan.copy(alpha = 0.5f),
              NeonViolet.copy(alpha = 0.5f)
            )
          ),
          shape = RoundedCornerShape(28.dp)
        )
        .padding(20.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = tr("Tactical Action Hub", "কৌশলগত অ্যাকশন হাব"),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = primaryText,
                  fontSize = 17.sp
                )
              )
              Text(
                text = tr("Fast System Switching", "দ্রুত সিস্টেম স্যুইচিং"),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = mutedText,
                  fontSize = 11.sp
                )
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = primaryText
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Quick Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          HubActionCard(
            title = tr("Gate Access", "গেট অ্যাক্সেস"),
            subtitle = tr("Bollards & Doors", "বোলার্ড ও দরজা"),
            icon = Icons.Default.DoorFront,
            accentColor = AlertAmber,
            onClick = {
              onScreenSelected(SecOpsScreen.ACCESS)
              onDismiss()
            },
            modifier = Modifier.weight(1f)
          )
          HubActionCard(
            title = tr("Automation", "অটোমেশন"),
            subtitle = tr("Rules & Triggers", "রুলস ও ট্রিগার"),
            icon = Icons.Default.Bolt,
            accentColor = NeonViolet,
            onClick = {
              onScreenSelected(SecOpsScreen.AUTOMATION)
              onDismiss()
            },
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          HubActionCard(
            title = tr("AI Copilot", "এআই কোপাইলট"),
            subtitle = tr("Gemini 3.8 Flash", "বুদ্ধিমান সহকারী"),
            icon = Icons.Default.AutoAwesome,
            accentColor = CyberCyan,
            onClick = {
              onScreenSelected(SecOpsScreen.AI_COPILOT)
              onDismiss()
            },
            modifier = Modifier.weight(1f)
          )
          HubActionCard(
            title = tr("Lone Worker", "একক কর্মী সেফটি"),
            subtitle = tr("Check-in Timer", "চেক-ইন টাইমার"),
            icon = Icons.Default.AccessTime,
            accentColor = TacticalEmerald,
            onClick = {
              onOpenLoneWorker()
              onDismiss()
            },
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Emergency SOS Lockdown Strip
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.horizontalGradient(
                listOf(
                  PanicRed.copy(alpha = 0.85f),
                  Color(0xFF991B1B)
                )
              )
            )
            .clickable {
              onPanicLockdown()
              onDismiss()
            }
            .padding(vertical = 12.dp, horizontal = 16.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = tr("TRIGGER EMERGENCY LOCKDOWN (SOS)", "জরুরি লকডাউন ট্রিগার (SOS)"),
              style = MaterialTheme.typography.labelLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                fontSize = 12.sp
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HubActionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1f,
    label = "hub_card_press"
  )

  Box(
    modifier = modifier
      .scale(scale)
      .clip(RoundedCornerShape(18.dp))
      .background(
        if (isDark) Color(0xF0182647) else Color(0xF2F0F7FF)
      )
      .border(
        width = 1.2.dp,
        brush = Brush.linearGradient(
          listOf(
            Color.White.copy(alpha = if (isDark) 0.45f else 0.85f),
            accentColor.copy(alpha = 0.5f)
          )
        ),
        shape = RoundedCornerShape(18.dp)
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(14.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = accentColor,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = if (isDark) Color.White else Color(0xFF0F172A),
          fontSize = 13.sp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(
          color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
          fontSize = 10.sp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun GlassNavBarItem(
  item: NavigationItem,
  isSelected: Boolean,
  isDark: Boolean,
  badgeCount: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val itemScale by animateFloatAsState(
    targetValue = when {
      isPressed -> 0.86f
      isSelected -> 1.08f
      else -> 1.0f
    },
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "nav_item_scale_${item.testTag}"
  )

  val iconOffsetY by animateDpAsState(
    targetValue = if (isSelected) (-2).dp else 0.dp,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "nav_icon_offset_${item.testTag}"
  )

  val unselectedColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
  val iconTint by animateColorAsState(
    targetValue = if (isSelected) Color.White else unselectedColor,
    animationSpec = tween(220),
    label = "nav_tint_${item.testTag}"
  )

  val pillAlpha by animateFloatAsState(
    targetValue = if (isSelected) 1f else 0f,
    animationSpec = tween(220),
    label = "nav_pill_alpha_${item.testTag}"
  )

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .scale(itemScale)
      .testTag(item.testTag)
      .clip(RoundedCornerShape(20.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(vertical = 4.dp, horizontal = 2.dp)
  ) {
    if (pillAlpha > 0.01f) {
      Box(
        modifier = Modifier
          .matchParentSize()
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.linearGradient(
              colors = listOf(
                item.accentColor.copy(alpha = if (isDark) 0.55f * pillAlpha else 0.90f * pillAlpha),
                NeonViolet.copy(alpha = if (isDark) 0.45f * pillAlpha else 0.80f * pillAlpha)
              )
            )
          )
          .border(
            width = 1.2.dp,
            brush = Brush.linearGradient(
              colors = listOf(
                Color.White.copy(alpha = 0.9f * pillAlpha),
                item.accentColor.copy(alpha = 0.8f * pillAlpha)
              )
            ),
            shape = RoundedCornerShape(18.dp)
          )
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
      Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier.offset(y = iconOffsetY)
      ) {
        Icon(
          imageVector = item.icon,
          contentDescription = item.label,
          tint = iconTint,
          modifier = Modifier.size(22.dp)
        )

        if (badgeCount > 0) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .offset(x = 6.dp, y = (-4).dp)
              .size(17.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  colors = listOf(PanicRed, NeonMagenta)
                )
              )
              .border(1.dp, Color.White, CircleShape)
          ) {
            Text(
              text = "$badgeCount",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(3.dp))

      Text(
        text = item.label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = if (isSelected) Color.White else unselectedColor,
          fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
          fontSize = 11.sp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(2.dp))
      Box(
        modifier = Modifier
          .width(if (isSelected) 18.dp else 0.dp)
          .height(2.5.dp)
          .clip(CircleShape)
          .background(Color.White)
      )
    }
  }
}

private data class NavigationItem(
  val label: String,
  val screen: SecOpsScreen,
  val icon: ImageVector,
  val accentColor: Color,
  val testTag: String
)

@Composable
fun TacticalBanner(
  message: String?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = message != null,
    enter = slideInVertically() + fadeIn(),
    exit = slideOutVertically() + fadeOut(),
    modifier = modifier
  ) {
    if (message != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color(0xE60D1B3A),
                Color(0xE625144A)
              )
            )
          )
          .border(
            width = 1.4.dp,
            brush = Brush.horizontalGradient(
              colors = listOf(Color.White, CyberCyan, NeonMagenta)
            ),
            shape = RoundedCornerShape(18.dp)
          )
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
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
                .size(30.dp)
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(17.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = message,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
              ),
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
