package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.EvaluationResult
import com.example.engine.ExpressionEvaluator
import com.example.model.CalculationHistoryItem
import com.example.model.CalculatorUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(CalculatorUiState())
  val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

  fun onDigitClick(digit: String) {
    _uiState.update { current ->
      val newExpr = if (current.isResultCalculated || current.errorMessage != null) {
        digit
      } else {
        if (current.expression == "0" && digit != "00") {
          digit
        } else if (current.expression == "0" && digit == "00") {
          "0"
        } else {
          current.expression + digit
        }
      }
      val preview = calculateLivePreview(newExpr, current.isRadMode)
      current.copy(
        expression = newExpr,
        livePreview = preview,
        isResultCalculated = false,
        errorMessage = null
      )
    }
  }

  fun onDecimalClick() {
    _uiState.update { current ->
      if (current.isResultCalculated || current.errorMessage != null || current.expression.isEmpty()) {
        val newExpr = "0."
        current.copy(
          expression = newExpr,
          livePreview = "",
          isResultCalculated = false,
          errorMessage = null
        )
      } else {
        val lastNumberChunk = getLastNumberChunk(current.expression)
        if (!lastNumberChunk.contains('.')) {
          val newExpr = if (isLastCharOperator(current.expression)) {
            current.expression + "0."
          } else {
            current.expression + "."
          }
          current.copy(
            expression = newExpr,
            livePreview = calculateLivePreview(newExpr, current.isRadMode),
            isResultCalculated = false,
            errorMessage = null
          )
        } else {
          current
        }
      }
    }
  }

  fun onOperatorClick(op: String) {
    _uiState.update { current ->
      val baseExpr = if (current.isResultCalculated && current.lastResultValue != null) {
        ExpressionEvaluator.formatNumber(current.lastResultValue).replace(",", "")
      } else if (current.errorMessage != null) {
        "0"
      } else {
        current.expression
      }

      if (baseExpr.isEmpty()) {
        if (op == "−") {
          return@update current.copy(
            expression = "−",
            livePreview = "",
            isResultCalculated = false,
            errorMessage = null
          )
        }
        return@update current
      }

      val newExpr = if (isLastCharOperator(baseExpr)) {
        baseExpr.dropLast(1) + op
      } else {
        baseExpr + op
      }

      current.copy(
        expression = newExpr,
        livePreview = "",
        isResultCalculated = false,
        errorMessage = null
      )
    }
  }

  fun onEqualsClick() {
    val state = _uiState.value
    if (state.expression.isBlank()) return

    when (val result = ExpressionEvaluator.evaluate(state.expression, state.isRadMode)) {
      is EvaluationResult.Success -> {
        val historyItem = CalculationHistoryItem(
          expression = state.expression,
          result = result.formattedText
        )
        _uiState.update { current ->
          current.copy(
            expression = result.formattedText,
            livePreview = "",
            isResultCalculated = true,
            lastResultValue = result.value,
            errorMessage = null,
            history = listOf(historyItem) + current.history.take(49)
          )
        }
      }
      is EvaluationResult.Error -> {
        _uiState.update { current ->
          current.copy(
            errorMessage = result.message,
            livePreview = ""
          )
        }
      }
    }
  }

  fun onClearAll() {
    _uiState.update {
      it.copy(
        expression = "",
        livePreview = "",
        isResultCalculated = false,
        lastResultValue = null,
        errorMessage = null
      )
    }
  }

  fun onBackspace() {
    _uiState.update { current ->
      if (current.isResultCalculated || current.errorMessage != null) {
        return@update current.copy(
          expression = "",
          livePreview = "",
          isResultCalculated = false,
          errorMessage = null
        )
      }
      if (current.expression.isEmpty()) return@update current

      // Check multi-character function deletions like "sin(", "cos(", "tan(", "ln(", "log("
      val expr = current.expression
      val newExpr = when {
        expr.endsWith("asin(") || expr.endsWith("acos(") || expr.endsWith("atan(") -> expr.dropLast(5)
        expr.endsWith("sin(") || expr.endsWith("cos(") || expr.endsWith("tan(") || expr.endsWith("log(") -> expr.dropLast(4)
        expr.endsWith("ln(") || expr.endsWith("√(") -> expr.dropLast(expr.length - expr.lastIndexOfAny(listOf("ln(", "√(")))
        else -> expr.dropLast(1)
      }

      current.copy(
        expression = newExpr,
        livePreview = calculateLivePreview(newExpr, current.isRadMode),
        errorMessage = null
      )
    }
  }

  fun onPercentClick() {
    _uiState.update { current ->
      if (current.expression.isEmpty() || isLastCharOperator(current.expression)) return@update current
      val newExpr = current.expression + "%"
      current.copy(
        expression = newExpr,
        livePreview = calculateLivePreview(newExpr, current.isRadMode),
        isResultCalculated = false,
        errorMessage = null
      )
    }
  }

  fun onParenthesisClick() {
    _uiState.update { current ->
      val expr = current.expression
      var openCount = 0
      for (ch in expr) {
        if (ch == '(') openCount++
        else if (ch == ')') openCount--
      }

      val lastChar = expr.lastOrNull()
      val toAppend = if (openCount > 0 && lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '%')) {
        ")"
      } else {
        if (lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e')) {
          "×("
        } else {
          "("
        }
      }

      val newExpr = expr + toAppend
      current.copy(
        expression = newExpr,
        livePreview = calculateLivePreview(newExpr, current.isRadMode),
        isResultCalculated = false,
        errorMessage = null
      )
    }
  }

  fun onFunctionClick(funcName: String) {
    _uiState.update { current ->
      val expr = if (current.isResultCalculated && current.lastResultValue != null) {
        ExpressionEvaluator.formatNumber(current.lastResultValue).replace(",", "")
      } else if (current.errorMessage != null) {
        ""
      } else {
        current.expression
      }

      val newExpr = when (funcName) {
        "sin" -> appendFunction(expr, if (current.isInverseMode) "asin(" else "sin(")
        "cos" -> appendFunction(expr, if (current.isInverseMode) "acos(" else "cos(")
        "tan" -> appendFunction(expr, if (current.isInverseMode) "atan(" else "tan(")
        "ln" -> appendFunction(expr, if (current.isInverseMode) "e^(" else "ln(")
        "log" -> appendFunction(expr, if (current.isInverseMode) "10^(" else "log(")
        "√" -> appendFunction(expr, "√(")
        "x²" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr^2" else "$expr"
        "x^y" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr^" else "$expr"
        "1/x" -> appendFunction(expr, "1/(")
        "x!" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr!" else "$expr"
        "π" -> appendConstant(expr, "π")
        "e" -> appendConstant(expr, "e")
        else -> expr
      }

      current.copy(
        expression = newExpr,
        livePreview = calculateLivePreview(newExpr, current.isRadMode),
        isResultCalculated = false,
        errorMessage = null
      )
    }
  }

  fun onPlusMinusClick() {
    _uiState.update { current ->
      if (current.expression.isEmpty()) {
        return@update current.copy(expression = "−")
      }
      val expr = current.expression
      val lastChunk = getLastNumberChunk(expr)
      if (lastChunk.isEmpty()) return@update current

      val prefix = expr.substring(0, expr.length - lastChunk.length)
      val newChunk = if (prefix.endsWith("−")) {
        // Remove unary minus
        prefix.dropLast(1) + lastChunk
      } else if (prefix.endsWith("-")) {
        prefix.dropLast(1) + lastChunk
      } else {
        prefix + "−" + lastChunk
      }

      current.copy(
        expression = newChunk,
        livePreview = calculateLivePreview(newChunk, current.isRadMode),
        isResultCalculated = false,
        errorMessage = null
      )
    }
  }

  fun onToggleRadDeg() {
    _uiState.update { current ->
      val newMode = !current.isRadMode
      current.copy(
        isRadMode = newMode,
        livePreview = calculateLivePreview(current.expression, newMode)
      )
    }
  }

  fun onToggleScientific() {
    _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
  }

  fun onToggleInverse() {
    _uiState.update { it.copy(isInverseMode = !it.isInverseMode) }
  }

  fun onToggleHistory(visible: Boolean? = null) {
    _uiState.update { it.copy(isHistoryVisible = visible ?: !it.isHistoryVisible) }
  }

  fun onRestoreHistoryItem(item: CalculationHistoryItem) {
    _uiState.update { current ->
      current.copy(
        expression = item.result.replace(",", ""),
        livePreview = "",
        isResultCalculated = true,
        errorMessage = null,
        isHistoryVisible = false
      )
    }
  }

  fun onClearHistory() {
    _uiState.update { it.copy(history = emptyList()) }
  }

  // Memory operations
  fun onMemoryClear() {
    _uiState.update { it.copy(memoryValue = 0.0, hasMemory = false) }
  }

  fun onMemoryRecall() {
    _uiState.update { current ->
      if (!current.hasMemory) return@update current
      val memStr = ExpressionEvaluator.formatNumber(current.memoryValue).replace(",", "")
      val newExpr = if (current.isResultCalculated || current.expression.isEmpty()) {
        memStr
      } else if (isLastCharOperator(current.expression)) {
        current.expression + memStr
      } else {
        current.expression + "×" + memStr
      }
      current.copy(
        expression = newExpr,
        livePreview = calculateLivePreview(newExpr, current.isRadMode),
        isResultCalculated = false
      )
    }
  }

  fun onMemoryAdd() {
    evaluateCurrentValue()?.let { value ->
      _uiState.update { current ->
        val newMem = current.memoryValue + value
        current.copy(memoryValue = newMem, hasMemory = true)
      }
    }
  }

  fun onMemorySubtract() {
    evaluateCurrentValue()?.let { value ->
      _uiState.update { current ->
        val newMem = current.memoryValue - value
        current.copy(memoryValue = newMem, hasMemory = true)
      }
    }
  }

  fun triggerCopiedFeedback() {
    viewModelScope.launch {
      _uiState.update { it.copy(showCopiedFeedback = true) }
      delay(2000)
      _uiState.update { it.copy(showCopiedFeedback = false) }
    }
  }

  private fun evaluateCurrentValue(): Double? {
    val state = _uiState.value
    if (state.lastResultValue != null && state.isResultCalculated) {
      return state.lastResultValue
    }
    if (state.expression.isBlank()) return null
    return when (val res = ExpressionEvaluator.evaluate(state.expression, state.isRadMode)) {
      is EvaluationResult.Success -> res.value
      is EvaluationResult.Error -> null
    }
  }

  private fun calculateLivePreview(expr: String, isRadMode: Boolean): String {
    if (expr.isBlank()) return ""
    // Avoid evaluating partial expressions ending with an operator
    val lastChar = expr.lastOrNull()
    if (lastChar != null && (lastChar == '+' || lastChar == '−' || lastChar == '×' || lastChar == '÷' || lastChar == '^' || lastChar == '(')) {
      return ""
    }
    // Also don't show preview if it's just a simple single number with no operations
    if (!expr.any { it == '+' || it == '−' || it == '×' || it == '÷' || it == '^' || it == '%' || it == '√' || it == '!' || it == '(' }) {
      return ""
    }

    return when (val result = ExpressionEvaluator.evaluate(expr, isRadMode)) {
      is EvaluationResult.Success -> "= ${result.formattedText}"
      is EvaluationResult.Error -> ""
    }
  }

  private fun appendFunction(expr: String, funcPrefix: String): String {
    val lastChar = expr.lastOrNull()
    return if (lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e')) {
      "$expr×$funcPrefix"
    } else {
      "$expr$funcPrefix"
    }
  }

  private fun appendConstant(expr: String, constant: String): String {
    val lastChar = expr.lastOrNull()
    return if (lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e')) {
      "$expr×$constant"
    } else {
      "$expr$constant"
    }
  }

  private fun getLastNumberChunk(expr: String): String {
    val lastIndex = expr.lastIndexOfAny(charArrayOf('+', '−', '×', '÷', '^', '(', ')', '%'))
    return if (lastIndex == -1) expr else expr.substring(lastIndex + 1)
  }

  private fun isLastCharOperator(expr: String): Boolean {
    val last = expr.lastOrNull() ?: return false
    return last == '+' || last == '−' || last == '×' || last == '÷' || last == '^'
  }
}
