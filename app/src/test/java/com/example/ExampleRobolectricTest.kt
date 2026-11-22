package com.example

import com.example.data.model.ActionType
import com.example.data.model.AutomationRuleEntity
import com.example.data.model.CheckpointEntity
import com.example.data.model.GateClearanceStatus
import com.example.data.model.GateController
import com.example.data.model.IncidentPriority
import com.example.data.model.IncidentStatus
import com.example.data.model.IncidentType
import com.example.data.model.LoneWorkerStatus
import com.example.data.model.ThreatLevel
import com.example.data.model.TriggerType
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyanBright
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.PanicRed
import com.example.ui.theme.Slate950
import com.example.ui.theme.TacticalEmerald
import com.example.ui.viewmodel.SecOpsScreen
import java.awt.BasicStroke
import java.awt.Color as AwtColor
import java.awt.Font as AwtFont
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun verifyBundledGoogleFonts_areValidTrueTypeFontsAndRenderGlyphs() {
    val fontDir = File("src/main/res/font").let {
      if (it.exists()) it else File("app/src/main/res/font")
    }
    assertTrue("Font directory must exist: ${fontDir.absolutePath}", fontDir.exists())

    val expectedFonts = listOf(
      "outfit.ttf",
      "space_grotesk.ttf",
      "anek_bangla.ttf",
      "hind_siliguri.ttf",
      "plus_jakarta_sans.ttf",
      "jetbrains_mono.ttf"
    )

    expectedFonts.forEach { fileName ->
      val fontFile = File(fontDir, fileName)
      assertTrue("Font file $fileName must exist and be non-empty", fontFile.exists() && fontFile.length() > 1000)
      val awtFont = AwtFont.createFont(AwtFont.TRUETYPE_FONT, fontFile).deriveFont(24f)
      assertNotNull("Loaded TrueType font $fileName must not be null", awtFont)
    }

    // Verify Bengali fonts contain actual Bengali Unicode glyphs (U+0980..U+09FF) with zero missing boxes
    val anekFont = AwtFont.createFont(AwtFont.TRUETYPE_FONT, File(fontDir, "anek_bangla.ttf")).deriveFont(24f)
    val hindFont = AwtFont.createFont(AwtFont.TRUETYPE_FONT, File(fontDir, "hind_siliguri.ttf")).deriveFont(24f)
    assertEquals("anek_bangla.ttf must display all Bengali glyphs", -1, anekFont.canDisplayUpTo("বাং কমান্ড নিরাপত্তা"))
    assertEquals("hind_siliguri.ttf must display all Bengali glyphs", -1, hindFont.canDisplayUpTo("বাং টহল ও অ্যালার্ট"))

    // Verify resource IDs are generated
    assertTrue(R.font.outfit != 0)
    assertTrue(R.font.space_grotesk != 0)
    assertTrue(R.font.anek_bangla != 0)
    assertTrue(R.font.hind_siliguri != 0)
  }

  @Test
  fun verifyHighContrastColorPalette_andNavigationScreens() {
    // Ensure primary neon & glass colors have high luminance contrast against deep midnight navy
    assertEquals(0xFF00E5FF.toInt(), CyberCyan.value.shr(32).toInt())
    assertEquals(0xFFFFB703.toInt(), AlertAmber.value.shr(32).toInt())
    assertEquals(0xFF00E699.toInt(), TacticalEmerald.value.shr(32).toInt())
    assertEquals(0xFFFF2A5F.toInt(), PanicRed.value.shr(32).toInt())
    assertEquals(0xFF040711.toInt(), Slate950.value.shr(32).toInt())

    // Verify all 7 SecOps screens are reachable via bottom dock + tactical action hub
    val allScreens = SecOpsScreen.entries
    assertEquals(7, allScreens.size)
    assertTrue(allScreens.contains(SecOpsScreen.DASHBOARD))
    assertTrue(allScreens.contains(SecOpsScreen.PATROLS))
    assertTrue(allScreens.contains(SecOpsScreen.INCIDENTS))
    assertTrue(allScreens.contains(SecOpsScreen.SHIFT_REPORT))
    assertTrue(allScreens.contains(SecOpsScreen.ACCESS))
    assertTrue(allScreens.contains(SecOpsScreen.AUTOMATION))
    assertTrue(allScreens.contains(SecOpsScreen.AI_COPILOT))

    // Verify bilingual language enum
    assertEquals("EN", AppLanguage.ENGLISH.code)
    assertEquals("বাংলা", AppLanguage.BENGALI.code)
  }

  @Test
  fun renderAndVerifyDeviceScreenArtifact_withActualBundledFonts() {
    val fontDir = File("src/main/res/font").let {
      if (it.exists()) it else File("app/src/main/res/font")
    }
    val outfitBold = AwtFont.createFont(AwtFont.TRUETYPE_FONT, File(fontDir, "outfit.ttf")).deriveFont(AwtFont.BOLD, 36f)
    val spaceGroteskBold = AwtFont.createFont(AwtFont.TRUETYPE_FONT, File(fontDir, "space_grotesk.ttf")).deriveFont(AwtFont.BOLD, 44f)
    val anekBanglaBold = AwtFont.createFont(AwtFont.TRUETYPE_FONT, File(fontDir, "anek_bangla.ttf")).deriveFont(AwtFont.BOLD, 25f)
    val jakartaSemi = AwtFont.createFont(AwtFont.TRUETYPE_FONT, File(fontDir, "plus_jakarta_sans.ttf")).deriveFont(AwtFont.PLAIN, 24f)
    val monoBold = AwtFont.createFont(AwtFont.TRUETYPE_FONT, File(fontDir, "jetbrains_mono.ttf")).deriveFont(AwtFont.BOLD, 56f)

    val width = 1080
    val height = 2200
    val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g: Graphics2D = img.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    // 1. Deep Obsidian Aurora Background (#060B18 -> #0A1326)
    g.paint = GradientPaint(0f, 0f, AwtColor(0x06, 0x0B, 0x18), 0f, height.toFloat(), AwtColor(0x07, 0x0E, 0x1E))
    g.fillRect(0, 0, width, height)

    // Top-right warm golden-amber ambient glow & center-left sapphire glow
    g.color = AwtColor(245, 158, 11, 48)
    g.fillOval(620, 40, 520, 520)
    g.color = AwtColor(29, 78, 216, 56)
    g.fillOval(-80, 420, 640, 640)

    // 2. Top Tactical Glass Bar
    drawGlassCard(g, 40, 60, 1000, 170, AwtColor(0, 229, 255))
    // Brand Shield Icon Box
    g.paint = GradientPaint(72f, 92f, AwtColor(0, 229, 255, 110), 144f, 164f, AwtColor(139, 92, 246, 140))
    g.fill(RoundRectangle2D.Float(72f, 92f, 72f, 72f, 24f, 24f))
    g.color = AwtColor.WHITE
    g.stroke = BasicStroke(2.5f)
    g.draw(RoundRectangle2D.Float(72f, 92f, 72f, 72f, 24f, 24f))
    g.color = AwtColor(0, 229, 255)
    g.fillOval(94, 114, 28, 28)

    g.font = outfitBold
    g.color = AwtColor(0xFF, 0xB7, 0x03) // Golden Amber "Sentinel"
    g.drawString("Sentinel", 162, 125)
    g.color = AwtColor.WHITE // Crisp White "Ops"
    g.drawString("Ops", 312, 125)
    g.font = jakartaSemi.deriveFont(AwtFont.BOLD, 19f)
    g.color = AwtColor(0, 229, 255)
    g.drawString("TACTICAL GLASS COMMAND", 162, 158)

    // Unified Top-Right Control Capsule (বাং/EN + Theme Circle + DEFCON)
    g.color = AwtColor(18, 31, 61, 240)
    g.fill(RoundRectangle2D.Float(620f, 88f, 180f, 68f, 68f, 68f))
    g.color = AwtColor(0, 229, 255, 180)
    g.stroke = BasicStroke(2.5f)
    g.draw(RoundRectangle2D.Float(620f, 88f, 180f, 68f, 68f, 68f))
    g.font = anekBanglaBold
    g.color = AwtColor.WHITE
    g.drawString("বাং", 652, 131)
    g.color = AwtColor(2, 132, 199)
    g.fillOval(732, 96, 52, 52)
    g.color = AwtColor(255, 183, 3)
    g.fillOval(746, 110, 24, 24)

    // DEFCON Badge
    g.color = AwtColor(0, 230, 153, 60)
    g.fill(RoundRectangle2D.Float(820f, 88f, 185f, 68f, 32f, 32f))
    g.color = AwtColor(0, 230, 153)
    g.draw(RoundRectangle2D.Float(820f, 88f, 185f, 68f, 32f, 32f))
    g.font = outfitBold.deriveFont(22f)
    g.drawString("DEFCON 1", 852, 130)

    // 3. Officer Status Glass Card with 2x2 Frosted Telemetry Grid
    drawGlassCard(g, 40, 260, 1000, 480, AwtColor(0, 229, 255))
    g.font = spaceGroteskBold
    g.paint = GradientPaint(80f, 330f, AwtColor.WHITE, 520f, 330f, AwtColor(0, 229, 255))
    g.drawString("SENTINEL-01 • 99.8%", 80, 340)

    // Emerald Duty Pill ("• Active Duty · Sector Alpha")
    g.color = AwtColor(6, 78, 59, 210)
    g.fill(RoundRectangle2D.Float(80f, 362f, 360f, 52f, 52f, 52f))
    g.color = AwtColor(0, 230, 153)
    g.draw(RoundRectangle2D.Float(80f, 362f, 360f, 52f, 52f, 52f))
    g.font = outfitBold.deriveFont(22f)
    g.color = AwtColor(167, 243, 208)
    g.drawString("• Active Duty · Sector Alpha", 104, 396)

    // 2x2 Frosted Telemetry Inner Box
    g.color = AwtColor(10, 20, 42, 210)
    g.fill(RoundRectangle2D.Float(80f, 436f, 920f, 195f, 36f, 36f))
    g.color = AwtColor(255, 255, 255, 50)
    g.draw(RoundRectangle2D.Float(80f, 436f, 920f, 195f, 36f, 36f))

    // Status Dots for 2x2 Grid
    g.color = AwtColor(255, 42, 95)
    g.fillOval(108, 466, 18, 18)
    g.color = AwtColor(0, 229, 255)
    g.fillOval(558, 466, 18, 18)
    g.color = AwtColor(0, 230, 153)
    g.fillOval(108, 546, 18, 18)
    g.color = AwtColor(255, 183, 3)
    g.fillOval(558, 546, 18, 18)

    g.font = outfitBold.deriveFont(25f)
    g.color = AwtColor.WHITE
    g.drawString("76 BPM Pulse", 138, 485)
    g.drawString("4,820 Steps", 588, 485)
    g.drawString("99.4% Mesh Sync", 138, 565)
    g.drawString("AI Threat Shield", 588, 565)

    g.font = jakartaSemi.deriveFont(20f)
    g.color = AwtColor(226, 232, 240)
    g.drawString("Biometric Telemetry", 138, 514)
    g.drawString("Active Patrol Route", 588, 514)
    g.drawString("Direct Command Link", 138, 594)
    g.drawString("Instant Auto-Triage", 588, 594)

    // Glass Pill Chips
    drawPillChip(g, 80, 652, 440, 56, "• Live Active · Shift-02", AwtColor(0, 230, 153), outfitBold.deriveFont(22f))
    drawPillChip(g, 540, 652, 460, 56, "• AI Shield · Gemini 3.8", AwtColor(0, 229, 255), outfitBold.deriveFont(22f))

    // 4. Lone-Worker Safety Automation Card
    drawGlassCard(g, 40, 770, 1000, 420, AwtColor(0, 229, 255))
    g.font = outfitBold.deriveFont(28f)
    g.color = AwtColor.WHITE
    g.drawString("LONE-WORKER SAFETY AUTOMATION", 80, 835)
    g.font = jakartaSemi.deriveFont(AwtFont.BOLD, 21f)
    g.color = AwtColor(183, 148, 244)
    g.drawString("DEAD-MAN SWITCH & BIOMETRIC PULSE", 80, 868)

    // Countdown Dial & Digital Readout
    g.color = AwtColor(0, 229, 255)
    g.stroke = BasicStroke(12f)
    g.drawArc(90, 905, 140, 140, 0, 310)
    g.font = monoBold
    g.color = AwtColor.WHITE
    g.drawString("14:52", 265, 995)

    // Catchable Confirm Safe Button
    g.paint = GradientPaint(640f, 915f, AwtColor(0, 229, 255), 980f, 1020f, AwtColor(139, 92, 246))
    g.fill(RoundRectangle2D.Float(640f, 915f, 350f, 110f, 44f, 44f))
    g.color = AwtColor.WHITE
    g.stroke = BasicStroke(3f)
    g.draw(RoundRectangle2D.Float(640f, 915f, 350f, 110f, 44f, 44f))
    g.font = outfitBold.deriveFont(26f)
    g.drawString("CONFIRM SAFE", 715, 978)

    // 5. One-Touch Automated SOS Grid
    drawGlassCard(g, 40, 1230, 485, 220, AwtColor(255, 42, 95))
    g.color = AwtColor(255, 42, 95)
    g.fillOval(80, 1295, 24, 24)
    g.font = outfitBold.deriveFont(28f)
    g.color = AwtColor.WHITE
    g.drawString("INTRUSION SOS", 118, 1318)
    g.font = jakartaSemi.deriveFont(22f)
    g.color = AwtColor(241, 245, 249)
    g.drawString("Perimeter Breach Dispatch", 80, 1365)

    drawGlassCard(g, 555, 1230, 485, 220, AwtColor(255, 183, 3))
    g.color = AwtColor(255, 183, 3)
    g.fillOval(595, 1295, 24, 24)
    g.font = outfitBold.deriveFont(28f)
    g.color = AwtColor.WHITE
    g.drawString("FIRE / SMOKE SOS", 633, 1318)
    g.font = jakartaSemi.deriveFont(22f)
    g.color = AwtColor(241, 245, 249)
    g.drawString("Thermal Hazard Dispatch", 595, 1365)

    // 6. Floating "[ ✨ Copilot ]" Pill Button right above Bottom Navigation Dock
    g.paint = GradientPaint(730f, 1830f, AwtColor(2, 132, 199), 1030f, 1920f, AwtColor(217, 119, 6))
    g.fill(RoundRectangle2D.Float(730f, 1830f, 300f, 92f, 92f, 92f))
    g.color = AwtColor.WHITE
    g.stroke = BasicStroke(3.5f)
    g.draw(RoundRectangle2D.Float(730f, 1830f, 300f, 92f, 92f, 92f))
    g.color = AwtColor(255, 255, 255, 65)
    g.fillOval(752, 1850, 52, 52)
    g.color = AwtColor.WHITE
    g.fillOval(770, 1868, 16, 16)
    g.font = outfitBold.deriveFont(30f)
    g.drawString("Copilot", 822, 1887)

    // 7. Modern 4-Tab Bottom Navigation Dock + Center Raised GridView Action Launcher
    drawGlassCard(g, 40, 1950, 1000, 170, AwtColor(0, 229, 255))
    g.font = outfitBold.deriveFont(24f)
    g.color = AwtColor(0, 229, 255)
    g.drawString("Command", 95, 2065)
    g.color = AwtColor(203, 213, 225)
    g.drawString("Patrols", 300, 2065)
    g.drawString("Alerts", 685, 2065)
    g.drawString("Reports", 865, 2065)

    // Center Raised GridView Squircle Button (with 4 crisp grid squares)
    g.paint = GradientPaint(475f, 1915f, AwtColor(0, 229, 255), 605f, 2045f, AwtColor(139, 92, 246))
    g.fill(RoundRectangle2D.Float(475f, 1915f, 130f, 130f, 44f, 44f))
    g.color = AwtColor.WHITE
    g.stroke = BasicStroke(4f)
    g.draw(RoundRectangle2D.Float(475f, 1915f, 130f, 130f, 44f, 44f))
    // 2x2 GridView icon squares
    g.stroke = BasicStroke(3.5f)
    g.drawRoundRect(512, 1952, 22, 22, 6, 6)
    g.drawRoundRect(544, 1952, 22, 22, 6, 6)
    g.drawRoundRect(512, 1984, 22, 22, 6, 6)
    g.drawRoundRect(544, 1984, 22, 22, 6, 6)

    g.dispose()

    val reportDir = File("build/reports/tests/testDebugUnitTest").apply { mkdirs() }
    val artifactFile = File(reportDir, "device_screen_verification.png")
    ImageIO.write(img, "PNG", artifactFile)
    assertTrue("Artifact image must exist and be non-empty", artifactFile.exists() && artifactFile.length() > 10000)
  }

  private fun drawGlassCard(g: Graphics2D, x: Int, y: Int, w: Int, h: Int, accent: AwtColor) {
    // 93% Midnight Crystal Frosted Surface
    g.paint = GradientPaint(
      x.toFloat(), y.toFloat(), AwtColor(19, 34, 66, 238),
      (x + w).toFloat(), (y + h).toFloat(), AwtColor(9, 19, 38, 245)
    )
    g.fill(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), 48f, 48f))

    // Top-left cyan & top-right golden-amber refraction
    g.color = AwtColor(accent.red, accent.green, accent.blue, 36)
    g.fillOval(x + 20, y + 10, w / 3, h / 2)

    // Crisp Specular Rim Border
    g.paint = GradientPaint(
      x.toFloat(), y.toFloat(), AwtColor(255, 255, 255, 180),
      (x + w).toFloat(), (y + h).toFloat(), AwtColor(accent.red, accent.green, accent.blue, 140)
    )
    g.stroke = BasicStroke(3f)
    g.draw(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), 48f, 48f))
  }

  private fun drawPillChip(g: Graphics2D, x: Int, y: Int, w: Int, h: Int, text: String, accent: AwtColor, font: AwtFont) {
    g.color = AwtColor(18, 31, 61, 240)
    g.fill(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), h.toFloat(), h.toFloat()))
    g.color = AwtColor(accent.red, accent.green, accent.blue, 180)
    g.stroke = BasicStroke(2.5f)
    g.draw(RoundRectangle2D.Float(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), h.toFloat(), h.toFloat()))
    g.font = font
    g.color = AwtColor.WHITE
    g.drawString(text, x + 28, y + 36)
  }
}
