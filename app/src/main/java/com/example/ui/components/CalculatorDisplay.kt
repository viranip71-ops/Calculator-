package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorUiState
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextErrorDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun CalculatorDisplay(
  uiState: CalculatorUiState,
  onBackspace: () -> Unit,
  onToggleRadDeg: () -> Unit,
  onToggleScientific: () -> Unit,
  onToggleHistory: () -> Unit,
  onCopyTriggered: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  // Auto-scroll expression to end whenever it updates
  LaunchedEffect(uiState.expression) {
    if (uiState.expression.isNotEmpty()) {
      scrollState.animateScrollTo(scrollState.maxValue)
    }
  }

  // Dynamic text size for expression
  val expressionFontSize = when {
    uiState.expression.length > 20 -> 24.sp
    uiState.expression.length > 14 -> 32.sp
    uiState.expression.length > 8 -> 38.sp
    else -> 46.sp
  }

  val isDark = MaterialTheme.colorScheme.background.red < 0.2f

  val containerBg = if (isDark) {
    Brush.verticalGradient(
      colors = listOf(
        ObsidianSurface,
        Color(0xFF0F121A)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color.White,
        Color(0xFFF9FAFD)
      )
    )
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(26.dp))
      .background(containerBg)
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = if (isDark) {
            listOf(ChampagneGold.copy(alpha = 0.25f), ObsidianBorder)
          } else {
            listOf(ChampagneGold.copy(alpha = 0.35f), Color(0xFFE2E7F0))
          }
        ),
        shape = RoundedCornerShape(26.dp)
      )
      .padding(horizontal = 20.dp, vertical = 16.dp)
      .testTag("calculator_display")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      // Top status & tool bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Mode Badges: RAD/DEG, Scientific toggle, Memory badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // RAD/DEG pill
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (uiState.isRadMode) ChampagneGold.copy(alpha = 0.15f) else if (isDark) Color(0xFF1E2330) else Color(0xFFEAEFF8),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (uiState.isRadMode) ChampagneGold.copy(alpha = 0.5f) else Color.Transparent
            ),
            onClick = onToggleRadDeg,
            modifier = Modifier.testTag("button_rad_deg")
          ) {
            Text(
              text = if (uiState.isRadMode) "RAD" else "DEG",
              color = if (uiState.isRadMode) ChampagneGold else if (isDark) TextSecondaryDark else Color(0xFF4A5568),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          // Scientific (fx) pill
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (uiState.isScientificExpanded) ChampagneGold.copy(alpha = 0.2f) else if (isDark) Color(0xFF1E2330) else Color(0xFFEAEFF8),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (uiState.isScientificExpanded) ChampagneGold.copy(alpha = 0.5f) else Color.Transparent
            ),
            onClick = onToggleScientific,
            modifier = Modifier.testTag("button_toggle_scientific")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Science,
                contentDescription = "Scientific functions",
                tint = if (uiState.isScientificExpanded) ChampagneGold else if (isDark) TextSecondaryDark else Color(0xFF4A5568),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "fx",
                color = if (uiState.isScientificExpanded) ChampagneGold else if (isDark) TextSecondaryDark else Color(0xFF4A5568),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Memory indicator
          if (uiState.hasMemory) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = ChampagneGold.copy(alpha = 0.15f),
              border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
            ) {
              Text(
                text = "M",
                color = ChampagneGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }
        }

        // Action tools: History, Copy, Backspace
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // History Button with badge count
          IconButton(
            onClick = onToggleHistory,
            modifier = Modifier
              .size(36.dp)
              .testTag("button_history")
          ) {
            if (uiState.history.isNotEmpty()) {
              BadgedBox(
                badge = {
                  Badge(
                    containerColor = ChampagneGold,
                    contentColor = Color(0xFF0C0E12)
                  ) {
                    Text(text = uiState.history.size.coerceAtMost(99).toString(), fontSize = 9.sp)
                  }
                }
              ) {
                Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = "Calculation history",
                  tint = if (isDark) TextSecondaryDark else Color(0xFF4A5568),
                  modifier = Modifier.size(20.dp)
                )
              }
            } else {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Calculation history",
                tint = if (isDark) TextSecondaryDark else Color(0xFF4A5568),
                modifier = Modifier.size(20.dp)
              )
            }
          }

          // Copy button
          IconButton(
            onClick = {
              val textToCopy = if (uiState.isResultCalculated) {
                uiState.expression
              } else if (uiState.livePreview.isNotEmpty()) {
                uiState.livePreview.removePrefix("= ")
              } else {
                uiState.expression
              }
              if (textToCopy.isNotBlank()) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Calculation", textToCopy)
                clipboard.setPrimaryClip(clip)
                onCopyTriggered()
              }
            },
            enabled = uiState.expression.isNotBlank() || uiState.livePreview.isNotBlank(),
            modifier = Modifier
              .size(36.dp)
              .testTag("button_copy")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy result",
              tint = if (uiState.expression.isNotBlank()) ChampagneGold else if (isDark) Color(0xFF333B4E) else Color(0xFFBAC2D1),
              modifier = Modifier.size(18.dp)
            )
          }

          // Backspace button
          IconButton(
            onClick = onBackspace,
            enabled = uiState.expression.isNotEmpty(),
            modifier = Modifier
              .size(36.dp)
              .testTag("button_backspace")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Backspace,
              contentDescription = "Delete last character",
              tint = if (uiState.expression.isNotEmpty()) {
                if (isDark) TextSecondaryDark else Color(0xFF3B4459)
              } else {
                if (isDark) Color(0xFF282F40) else Color(0xFFD0D7E5)
              },
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Main expression display line
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val displayText = if (uiState.expression.isEmpty()) "0" else uiState.expression
        Text(
          text = displayText,
          color = if (uiState.errorMessage != null) {
            TextErrorDark
          } else if (uiState.expression.isEmpty()) {
            if (isDark) TextSecondaryDark.copy(alpha = 0.5f) else Color.LightGray
          } else {
            if (isDark) TextPrimaryDark else Color(0xFF14171E)
          },
          fontSize = expressionFontSize,
          fontWeight = FontWeight.Light,
          fontFamily = FontFamily.Default,
          textAlign = TextAlign.End,
          maxLines = 1,
          modifier = Modifier.testTag("display_expression")
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Secondary live preview / error / calculated label line
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(30.dp),
        contentAlignment = Alignment.CenterEnd
      ) {
        if (uiState.errorMessage != null) {
          Text(
            text = uiState.errorMessage,
            color = TextErrorDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.testTag("display_error")
          )
        } else if (uiState.livePreview.isNotEmpty()) {
          Text(
            text = uiState.livePreview,
            color = ChampagneGold,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            modifier = Modifier.testTag("display_preview")
          )
        }
      }
    }

    // Copied to clipboard floating toast badge inside display
    AnimatedVisibility(
      visible = uiState.showCopiedFeedback,
      enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
      modifier = Modifier.align(Alignment.TopCenter)
    ) {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = ChampagneGold,
        shadowElevation = 6.dp
      ) {
        Text(
          text = "Copied to clipboard",
          color = Color(0xFF0C0E12),
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }
    }
  }
}
