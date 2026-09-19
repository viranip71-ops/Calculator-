package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeypadButtonType
import com.example.ui.components.CalculatorButton
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.ScientificKeypad
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
  viewModel: CalculatorViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val isDark = MaterialTheme.colorScheme.background.red < 0.2f

  val backgroundBrush = if (isDark) {
    Brush.verticalGradient(
      colors = listOf(
        Color(0xFF141722),
        ObsidianBackground,
        Color(0xFF07080B)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color(0xFFF9FAFD),
        Color(0xFFF1F3F9),
        Color(0xFFE8ECF5)
      )
    )
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.Transparent
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(backgroundBrush)
        .padding(innerPadding)
        .statusBarsPadding()
        .navigationBarsPadding(),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 520.dp)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // App Header Branding
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = ChampagneGold.copy(alpha = 0.15f),
              modifier = Modifier.size(28.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Calculate,
                  contentDescription = "Calculator app",
                  tint = ChampagneGold,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
            Text(
              text = "PRECISION",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp,
              color = if (isDark) TextPrimaryDark else Color(0xFF14171E)
            )
            Text(
              text = "FX",
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp,
              color = ChampagneGold
            )
          }

          // Subtle indicator
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isDark) Color(0xFF161A24) else Color(0xFFE5EAF5),
            modifier = Modifier.padding(end = 4.dp)
          ) {
            Text(
              text = if (uiState.isRadMode) "RADIANS" else "DEGREES",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = if (isDark) TextSecondaryDark else Color(0xFF657088),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Display Area
        CalculatorDisplay(
          uiState = uiState,
          onBackspace = viewModel::onBackspace,
          onToggleRadDeg = viewModel::onToggleRadDeg,
          onToggleScientific = viewModel::onToggleScientific,
          onToggleHistory = { viewModel.onToggleHistory(true) },
          onCopyTriggered = viewModel::triggerCopiedFeedback,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Scientific Keypad (Animated expansion)
        AnimatedVisibility(
          visible = uiState.isScientificExpanded,
          enter = expandVertically() + fadeIn(),
          exit = shrinkVertically() + fadeOut()
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            ScientificKeypad(
              uiState = uiState,
              onFunctionClick = viewModel::onFunctionClick,
              onParenthesisClick = viewModel::onParenthesisClick,
              onToggleInverse = viewModel::onToggleInverse,
              onMemoryClear = viewModel::onMemoryClear,
              onMemoryRecall = viewModel::onMemoryRecall,
              onMemoryAdd = viewModel::onMemoryAdd,
              onMemorySubtract = viewModel::onMemorySubtract,
              modifier = Modifier.padding(bottom = 8.dp)
            )
          }
        }

        // Standard Keypad Grid (4 columns × 5 rows)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val buttonShape = RoundedCornerShape(22.dp)
          val buttonHeight = 62.dp
          val operatorFontSize = 26.sp
          val digitFontSize = 24.sp

          // Row 1: AC, +/-, %, ÷
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            val clearLabel = if (uiState.expression.isNotEmpty()) "C" else "AC"
            CalculatorButton(
              text = clearLabel,
              type = KeypadButtonType.ACTION,
              onClick = viewModel::onClearAll,
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = 20.sp,
              fontWeight = FontWeight.SemiBold,
              testTag = "btn_clear"
            )
            CalculatorButton(
              text = "±",
              type = KeypadButtonType.ACTION,
              onClick = viewModel::onPlusMinusClick,
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = 22.sp,
              testTag = "btn_plus_minus"
            )
            CalculatorButton(
              text = "%",
              type = KeypadButtonType.ACTION,
              onClick = viewModel::onPercentClick,
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = 20.sp,
              testTag = "btn_percent"
            )
            CalculatorButton(
              text = "÷",
              type = KeypadButtonType.OPERATOR,
              onClick = { viewModel.onOperatorClick("÷") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = operatorFontSize,
              fontWeight = FontWeight.SemiBold,
              testTag = "btn_divide"
            )
          }

          // Row 2: 7, 8, 9, ×
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CalculatorButton(
              text = "7",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("7") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_7"
            )
            CalculatorButton(
              text = "8",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("8") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_8"
            )
            CalculatorButton(
              text = "9",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("9") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_9"
            )
            CalculatorButton(
              text = "×",
              type = KeypadButtonType.OPERATOR,
              onClick = { viewModel.onOperatorClick("×") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = operatorFontSize,
              fontWeight = FontWeight.SemiBold,
              testTag = "btn_multiply"
            )
          }

          // Row 3: 4, 5, 6, −
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CalculatorButton(
              text = "4",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("4") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_4"
            )
            CalculatorButton(
              text = "5",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("5") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_5"
            )
            CalculatorButton(
              text = "6",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("6") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_6"
            )
            CalculatorButton(
              text = "−",
              type = KeypadButtonType.OPERATOR,
              onClick = { viewModel.onOperatorClick("−") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = operatorFontSize,
              fontWeight = FontWeight.SemiBold,
              testTag = "btn_subtract"
            )
          }

          // Row 4: 1, 2, 3, +
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CalculatorButton(
              text = "1",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("1") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_1"
            )
            CalculatorButton(
              text = "2",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("2") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_2"
            )
            CalculatorButton(
              text = "3",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("3") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_3"
            )
            CalculatorButton(
              text = "+",
              type = KeypadButtonType.OPERATOR,
              onClick = { viewModel.onOperatorClick("+") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = operatorFontSize,
              fontWeight = FontWeight.SemiBold,
              testTag = "btn_add"
            )
          }

          // Row 5: 0, 00, ., =
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CalculatorButton(
              text = "0",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("0") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = digitFontSize,
              testTag = "btn_0"
            )
            CalculatorButton(
              text = "00",
              type = KeypadButtonType.DIGIT,
              onClick = { viewModel.onDigitClick("00") },
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = 20.sp,
              testTag = "btn_00"
            )
            CalculatorButton(
              text = ".",
              type = KeypadButtonType.DIGIT,
              onClick = viewModel::onDecimalClick,
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = 26.sp,
              fontWeight = FontWeight.Bold,
              testTag = "btn_dot"
            )
            CalculatorButton(
              text = "=",
              type = KeypadButtonType.EQUALS,
              onClick = viewModel::onEqualsClick,
              modifier = Modifier.weight(1f).height(buttonHeight),
              shape = buttonShape,
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              elevation = 6.dp,
              testTag = "btn_equals"
            )
          }
        }
      }

      // History Bottom Sheet
      if (uiState.isHistoryVisible) {
        HistoryBottomSheet(
          history = uiState.history,
          onDismiss = { viewModel.onToggleHistory(false) },
          onItemClick = viewModel::onRestoreHistoryItem,
          onClearHistory = viewModel::onClearHistory,
          onCopyTriggered = viewModel::triggerCopiedFeedback
        )
      }
    }
  }
}
