package com.example.data.ai

import android.util.Log
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "SentinelAiService"

class SentinelAiService {

  // Initialized lazily via `by lazy` so that Firebase.ai is evaluated upon first inference
  private val generativeModel by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      systemInstruction = content {
        text(
          """
You are Sentinel AI, the elite tactical Security Operations Dispatcher and Automation Copilot for SentinelOps.
You provide on-duty security officers, lone guards, and command controllers with:
1. Standard Operating Procedures (SOPs) & Checklists:
   - Perimeter breach & motion alert containment
   - Unauthorized access & forced door response
   - Fire & smoke emergency evacuation
   - Lone-worker safety check & duress response
   - Suspicious vehicle / ANPR flag inspection
   - VIP close protection & escort protocol
2. Tactical Guidance & Threat Prioritization:
   - Categorize threats into DEFCON/Priority levels (P1 Critical, P2 High, P3 Medium, P4 Low)
   - Step-by-step immediate actionable commands for officers on the ground
3. Automated Shift Summaries & Incident Documentation:
   - Structured handover logs, legal timestamp records, and radio dispatch transmissions
4. Bilingual Fluency (English & বাংলা):
   - You are fully fluent in both English and Bengali (বাংলা).
   - If the user asks in Bengali or requests Bengali assistance, respond in natural, professional, tactical Bengali.
5. Tone:
   - Authoritative, clear, calm under pressure, and formatted with clean bullet points and numbered steps.
          """.trimIndent()
        )
      }
    )
  }

  suspend fun streamResponse(
    userPrompt: String,
    conversationHistory: List<ChatMessage>
  ): Flow<String> = flow {
    try {
      withTimeout(60_000L) {
        val responseStream = generativeModel.generateContentStream(
          content {
            conversationHistory.takeLast(6).forEach { msg ->
              when (msg.sender) {
                MessageSender.USER -> text("Officer: ${msg.text}")
                MessageSender.SENTINEL_AI -> text("Sentinel AI: ${msg.text}")
              }
            }
            text("Officer Query: $userPrompt")
          }
        )

        responseStream.collect { chunk ->
          val chunkText = chunk.text.orEmpty()
          if (chunkText.isNotEmpty()) {
            emit(chunkText)
          }
        }
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "Sentinel AI generation timed out", e)
      emit("\n[ERROR: Tactical link timed out after 60s. Please retry query.]")
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Sentinel AI generation failed: ${e.message}", e)
      val errorMsg = e.localizedMessage ?: "Tactical transmission failed"
      emit("\n[AI DISPATCH NOTICE: $errorMsg. Please check network connection.]")
    }
  }
}
