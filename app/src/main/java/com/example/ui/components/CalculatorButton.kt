package com.example.ui.components

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeypadButtonType
import com.example.ui.theme.ButtonActionBgDark
import com.example.ui.theme.ButtonActionTextDark
import com.example.ui.theme.ButtonDigitBgDark
import com.example.ui.theme.ButtonDigitBorderDark
import com.example.ui.theme.ButtonDigitTextDark
import com.example.ui.theme.ButtonScientificBgDark
import com.example.ui.theme.ButtonScientificTextDark
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.ChampagneGoldLight

@Composable
fun CalculatorButton(
  text: String,
  type: KeypadButtonType,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(22.dp),
  fontSize: TextUnit = 24.sp,
  fontWeight: FontWeight = FontWeight.Normal,
  testTag: String = "btn_$text",
  elevation: Dp = 2.dp
) {
  val context = LocalContext.current
  val vibrator = remember {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getSystemService(Vibrator::class.java)
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
      }
    } catch (_: Exception) {
      null
    }
  }

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.93f else 1.0f,
    animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
    label = "button_scale"
  )

  // Style configurations according to KeypadButtonType
  val isDark = MaterialTheme.colorScheme.background.red < 0.2f

  val (bgColor, textColor, borderColor, gradientBrush) = when (type) {
    KeypadButtonType.EQUALS -> {
      val brush = Brush.linearGradient(
        colors = listOf(ChampagneGoldLight, ChampagneGold, ChampagneGoldDark)
      )
      Quad(ChampagneGold, Color(0xFF0E1118), ChampagneGoldLight.copy(alpha = 0.5f), brush)
    }
    KeypadButtonType.OPERATOR -> {
      if (isDark) {
        val brush = Brush.linearGradient(
          colors = listOf(Color(0xFF2E2416), Color(0xFF201A10))
        )
        Quad(Color(0xFF241C10), ChampagneGold, ChampagneGold.copy(alpha = 0.35f), brush)
      } else {
        Quad(ChampagneGold.copy(alpha = 0.15f), ChampagneGoldDark, ChampagneGold.copy(alpha = 0.4f), null)
      }
    }
    KeypadButtonType.ACTION -> {
      if (isDark) {
        Quad(ButtonActionBgDark, ButtonActionTextDark, Color(0xFF333B4E), null)
      } else {
        Quad(Color(0xFFE5E9F2), Color(0xFF283144), Color(0xFFD0D7E5), null)
      }
    }
    KeypadButtonType.SCIENTIFIC -> {
      if (isDark) {
        Quad(ButtonScientificBgDark, ButtonScientificTextDark, Color(0xFF202533), null)
      } else {
        Quad(Color(0xFFEDF0F6), Color(0xFF4A5568), Color(0xFFD6DBE5), null)
      }
    }
    KeypadButtonType.DIGIT -> {
      if (isDark) {
        val brush = Brush.verticalGradient(
          colors = listOf(ButtonDigitBgDark, Color(0xFF13161E))
        )
        Quad(ButtonDigitBgDark, ButtonDigitTextDark, ButtonDigitBorderDark, brush)
      } else {
        Quad(Color.White, Color(0xFF14171E), Color(0xFFE2E6EE), null)
      }
    }
  }

  val finalBgModifier = if (gradientBrush != null) {
    Modifier.background(gradientBrush, shape)
  } else {
    Modifier.background(bgColor, shape)
  }

  Box(
    modifier = modifier
      .scale(scale)
      .shadow(
        elevation = if (isPressed) 0.dp else elevation,
        shape = shape,
        clip = false,
        ambientColor = if (type == KeypadButtonType.EQUALS) ChampagneGold.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.2f),
        spotColor = if (type == KeypadButtonType.EQUALS) ChampagneGold else Color.Black.copy(alpha = 0.3f)
      )
      .then(finalBgModifier)
      .border(
        width = 1.dp,
        color = if (isPressed) borderColor.copy(alpha = 0.9f) else borderColor,
        shape = shape
      )
      .clip(shape)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = {
          // Tactile haptic tick
          try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
              @Suppress("DEPRECATION")
              vibrator?.vibrate(12)
            }
          } catch (_: Exception) {}
          onClick()
        }
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = textColor,
      fontSize = fontSize,
      fontWeight = if (type == KeypadButtonType.EQUALS) FontWeight.Bold else fontWeight,
      maxLines = 1
    )
  }
}

private data class Quad<A, B, C, D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)
