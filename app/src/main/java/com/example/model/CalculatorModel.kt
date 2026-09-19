package com.example.model

data class CalculationHistoryItem(
  val id: Long = System.currentTimeMillis() + (0..999).random(),
  val expression: String,
  val result: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class CalculatorUiState(
  val expression: String = "",
  val livePreview: String = "",
  val isResultCalculated: Boolean = false,
  val lastResultValue: Double? = null,
  val errorMessage: String? = null,
  val isRadMode: Boolean = false,
  val isScientificExpanded: Boolean = false,
  val isInverseMode: Boolean = false,
  val history: List<CalculationHistoryItem> = emptyList(),
  val isHistoryVisible: Boolean = false,
  val memoryValue: Double = 0.0,
  val hasMemory: Boolean = false,
  val showCopiedFeedback: Boolean = false
)

enum class KeypadButtonType {
  DIGIT,
  OPERATOR,
  ACTION,
  EQUALS,
  SCIENTIFIC
}
