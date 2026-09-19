package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorUiState
import com.example.model.KeypadButtonType

@Composable
fun ScientificKeypad(
  uiState: CalculatorUiState,
  onFunctionClick: (String) -> Unit,
  onParenthesisClick: () -> Unit,
  onToggleInverse: () -> Unit,
  onMemoryClear: () -> Unit,
  onMemoryRecall: () -> Unit,
  onMemoryAdd: () -> Unit,
  onMemorySubtract: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isInv = uiState.isInverseMode
  val btnShape = RoundedCornerShape(14.dp)
  val btnHeight = 40.dp
  val btnFontSize = 13.sp

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // Memory row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CalculatorButton(
        text = "MC",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = onMemoryClear,
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_mc"
      )
      CalculatorButton(
        text = "MR",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = onMemoryRecall,
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_mr"
      )
      CalculatorButton(
        text = "M+",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = onMemoryAdd,
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_m_plus"
      )
      CalculatorButton(
        text = "M-",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = onMemorySubtract,
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_m_minus"
      )
      CalculatorButton(
        text = if (isInv) "INV•" else "INV",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = onToggleInverse,
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        fontWeight = if (isInv) FontWeight.Bold else FontWeight.Normal,
        testTag = "btn_inv"
      )
    }

    // Row 1: Trig & Powers
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CalculatorButton(
        text = if (isInv) "asin" else "sin",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("sin") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_sin"
      )
      CalculatorButton(
        text = if (isInv) "acos" else "cos",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("cos") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_cos"
      )
      CalculatorButton(
        text = if (isInv) "atan" else "tan",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("tan") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_tan"
      )
      CalculatorButton(
        text = "√",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("√") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = 15.sp,
        testTag = "btn_sqrt"
      )
      CalculatorButton(
        text = "x²",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("x²") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_x_sq"
      )
    }

    // Row 2: Logs, constants, powers, parentheses
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CalculatorButton(
        text = if (isInv) "e^x" else "ln",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("ln") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_ln"
      )
      CalculatorButton(
        text = if (isInv) "10^x" else "log",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("log") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_log"
      )
      CalculatorButton(
        text = "x^y",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("x^y") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_x_pow_y"
      )
      CalculatorButton(
        text = "π",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("π") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = 15.sp,
        testTag = "btn_pi"
      )
      CalculatorButton(
        text = "e",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("e") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = 15.sp,
        testTag = "btn_e"
      )
    }

    // Row 3: 1/x, x!, ( ), %
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      CalculatorButton(
        text = "1/x",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("1/x") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_one_over_x"
      )
      CalculatorButton(
        text = "x!",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = { onFunctionClick("x!") },
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_fact"
      )
      CalculatorButton(
        text = "(",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = onParenthesisClick,
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = 15.sp,
        testTag = "btn_open_paren"
      )
      CalculatorButton(
        text = ")",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = onParenthesisClick,
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = 15.sp,
        testTag = "btn_close_paren"
      )
      CalculatorButton(
        text = "( )",
        type = KeypadButtonType.SCIENTIFIC,
        onClick = onParenthesisClick,
        modifier = Modifier.weight(1f).height(btnHeight),
        shape = btnShape,
        fontSize = btnFontSize,
        testTag = "btn_paren_smart"
      )
    }
  }
}
