package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.components.GlassCard
import com.example.ui.components.InteractiveGradientText
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.PanicRed
import com.example.ui.theme.tr
import com.example.ui.theme.Slate950
import com.example.ui.theme.TacticalEmerald
import com.example.ui.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatbotScreen(
  viewModel: SecurityViewModel,
  modifier: Modifier = Modifier
) {
  val isDark = LocalIsDarkTheme.current
  val context = LocalContext.current
  val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
  val isGenerating by viewModel.isAiGenerating.collectAsStateWithLifecycle()
  val listState = rememberLazyListState()

  var inputText by remember { mutableStateOf("") }

  // Auto-scroll on new message or generation chunk
  LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val quickPrompts = listOf(
    tr("🚨 Perimeter Breach SOP", "🚨 সীমানা লঙ্ঘন এসওপি"),
    tr("🛡️ Lone-Worker Safety SOP", "🛡️ একক কর্মী সুরক্ষা প্রোটোকল"),
    tr("📋 Draft Shift Handover", "📋 শিফট হ্যান্ডওভার রিপোর্ট"),
    tr("🚗 Vehicle Gate Inspection", "🚗 গেট চেকপয়েন্ট পরিদর্শন"),
    tr("🔥 Fire Evacuation Steps", "🔥 অগ্নি নির্বাপণ জরুরি এসওপি"),
    tr("🌐 বাংলায় বিস্তারিত নিরাপত্তা রিপোর্ট দিন", "🌐 বাংলায় বিস্তারিত নিরাপত্তা রিপোর্ট দিন")
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .imePadding()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("chatbot_screen")
  ) {
    // Header Glass Banner
    GlassCard(
      accentColor = NeonViolet,
      cornerRadius = 20.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
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
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              InteractiveGradientText(
                text = tr("Sentinel AI Copilot", "সেন্টিনেল এআই কো-পাইলট"),
                colors = if (isDark) {
                  listOf(Color.White, CyberCyan, AlertAmber)
                } else {
                  listOf(Color(0xFF071124), Color(0xFF0284C7), Color(0xFF6D28D9))
                },
                fontSize = 18.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              PulsingOnlineDot()
            }
            Text(
              text = tr("Tactical SecOps • Gemini 3.8 Flash", "ট্যাকটিক্যাল ডিসপ্যাচ • জেমিনি ৩.৮ ফ্ল্যাশ"),
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDark) CyberCyan else NeonViolet,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp
              )
            )
          }
        }

        IconButton(
          onClick = { viewModel.clearChat() },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "Clear Chat",
            tint = if (isDark) Color.White.copy(alpha = 0.7f) else Slate950.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Horizontal Quick Prompt Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(vertical = 4.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)
    ) {
      items(quickPrompts) { prompt ->
        val promptClean = prompt.substringAfter(" ")
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
              if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.85f)
            )
            .border(
              width = 1.dp,
              brush = Brush.horizontalGradient(
                listOf(NeonViolet.copy(alpha = 0.6f), CyberCyan.copy(alpha = 0.6f))
              ),
              shape = RoundedCornerShape(16.dp)
            )
            .clickable {
              viewModel.sendChatMessage(promptClean)
            }
            .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
          Text(
            text = prompt,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = if (isDark) Color.White else Slate950,
              fontSize = 11.sp
            )
          )
        }
      }
    }

    val copiedBannerText = tr("✓ Message copied to clipboard", "✓ বার্তা ক্লিপবোর্ডে কপি করা হয়েছে")

    // Scrollable Chat Thread
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages, key = { it.id }) { message ->
        ChatMessageBubble(
          message = message,
          isDark = isDark,
          onCopy = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("SentinelOps AI Dispatch", message.text)
            clipboard.setPrimaryClip(clip)
            viewModel.showBanner(copiedBannerText)
          }
        )
      }

      if (isGenerating && messages.lastOrNull()?.isStreaming != true) {
        item {
          GeneratingIndicator(isDark = isDark)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Crystal Input Bar
    GlassCard(
      accentColor = CyberCyan,
      cornerRadius = 24.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          placeholder = {
            Text(
              text = tr("Type tactical query or SOP request...", "ট্যাকটিক্যাল প্রশ্ন বা এসওপি অনুরোধ লিখুন..."),
              style = MaterialTheme.typography.bodySmall.copy(
                color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray
              )
            )
          },
          modifier = Modifier
            .weight(1f)
            .testTag("chat_input_field"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = if (isDark) Color.White else Slate950,
            unfocusedTextColor = if (isDark) Color.White else Slate950,
            cursorColor = CyberCyan
          ),
          maxLines = 4,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(
            onSend = {
              if (inputText.isNotBlank() && !isGenerating) {
                viewModel.sendChatMessage(inputText)
                inputText = ""
              }
            }
          )
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Send Button with Neon Glow
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
              if (inputText.isNotBlank() && !isGenerating) {
                Brush.linearGradient(listOf(CyberCyan, NeonViolet))
              } else {
                Brush.linearGradient(
                  listOf(
                    Color.Gray.copy(alpha = 0.3f),
                    Color.Gray.copy(alpha = 0.3f)
                  )
                )
              }
            )
            .border(
              width = 1.dp,
              color = Color.White.copy(alpha = 0.6f),
              shape = CircleShape
            )
            .clickable(
              enabled = inputText.isNotBlank() && !isGenerating,
              onClick = {
                viewModel.sendChatMessage(inputText)
                inputText = ""
              }
            )
            .testTag("send_chat_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
fun ChatMessageBubble(
  message: ChatMessage,
  isDark: Boolean,
  onCopy: () -> Unit
) {
  val isUser = message.sender == MessageSender.USER
  val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
  val timeStr = remember(message.timestampEpochMs) { timeFormat.format(Date(message.timestampEpochMs)) }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isUser) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(listOf(NeonViolet, CyberCyan))
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.SmartToy,
          contentDescription = "AI",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Box(
      modifier = Modifier
        .fillMaxWidth(0.86f)
        .clip(
          RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (isUser) 18.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 18.dp
          )
        )
        .background(
          if (isUser) {
            if (isDark) {
              Brush.linearGradient(
                listOf(
                  CyberCyan.copy(alpha = 0.28f),
                  NeonViolet.copy(alpha = 0.22f)
                )
              )
            } else {
              Brush.linearGradient(
                listOf(
                  CyberCyan.copy(alpha = 0.18f),
                  NeonViolet.copy(alpha = 0.14f)
                )
              )
            }
          } else {
            if (isDark) {
              Brush.linearGradient(
                listOf(
                  Color(0xFF13203C).copy(alpha = 0.85f),
                  Color(0xFF0F182E).copy(alpha = 0.90f)
                )
              )
            } else {
              Brush.linearGradient(
                listOf(
                  Color.White.copy(alpha = 0.94f),
                  Color(0xFFF1F6FF).copy(alpha = 0.94f)
                )
              )
            }
          }
        )
        .border(
          width = 1.dp,
          brush = Brush.linearGradient(
            if (isUser) {
              listOf(CyberCyan.copy(alpha = 0.7f), Color.White.copy(alpha = 0.4f))
            } else {
              listOf(NeonViolet.copy(alpha = 0.6f), Color.White.copy(alpha = 0.3f))
            }
          ),
          shape = RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (isUser) 18.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 18.dp
          )
        )
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isUser) tr("SECURITY OFFICER", "নিরাপত্তা কর্মকর্তা") else tr("SENTINEL DISPATCH", "সেন্টিনেল ডিসপ্যাচ"),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = if (isUser) CyberCyan else (if (isDark) NeonViolet else Color(0xFF6B21A8)),
              fontSize = 10.sp
            )
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = timeStr,
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray,
                fontSize = 9.sp
              )
            )

            if (!isUser && message.text.isNotBlank()) {
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray,
                modifier = Modifier
                  .size(14.dp)
                  .clickable { onCopy() }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (message.text.isBlank() && message.isStreaming) {
          GeneratingDots()
        } else {
          Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium.copy(
              color = if (message.isError) PanicRed else (if (isDark) Color.White else Slate950),
              lineHeight = 21.sp,
              fontSize = 13.5.sp
            )
          )
        }
      }
    }

    if (isUser) {
      Spacer(modifier = Modifier.width(8.dp))
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(listOf(CyberCyan, TacticalEmerald))
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "Officer",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
fun PulsingOnlineDot() {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_online")
  val scale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot_scale"
  )

  Box(
    modifier = Modifier
      .size(8.dp)
      .scale(scale)
      .clip(CircleShape)
      .background(TacticalEmerald)
  )
}

@Composable
fun GeneratingDots() {
  val infiniteTransition = rememberInfiniteTransition(label = "dots")
  val alpha1 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
    label = "d1"
  )
  val alpha2 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), RepeatMode.Reverse),
    label = "d2"
  )
  val alpha3 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), RepeatMode.Reverse),
    label = "d3"
  )

  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 4.dp)
  ) {
    Text(
      text = tr("Generating tactical response", "ট্যাকটিক্যাল নির্দেশিকা তৈরি হচ্ছে"),
      style = MaterialTheme.typography.labelSmall.copy(color = CyberCyan, fontSize = 11.sp)
    )
    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(CyberCyan.copy(alpha = alpha1)))
    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(CyberCyan.copy(alpha = alpha2)))
    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(CyberCyan.copy(alpha = alpha3)))
  }
}

@Composable
fun GeneratingIndicator(isDark: Boolean) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(
        if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.8f)
      )
      .border(
        width = 1.dp,
        color = CyberCyan.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp)
      )
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.AutoAwesome,
      contentDescription = "AI",
      tint = CyberCyan,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    GeneratingDots()
  }
}
