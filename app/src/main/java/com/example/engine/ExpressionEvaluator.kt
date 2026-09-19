package com.example.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

sealed class EvaluationResult {
  data class Success(val value: Double, val formattedText: String) : EvaluationResult()
  data class Error(val message: String) : EvaluationResult()
}

object ExpressionEvaluator {

  private const val PRECISION_THRESHOLD = 1e-11

  fun evaluate(rawExpression: String, isRadMode: Boolean = false): EvaluationResult {
    val clean = sanitizeExpression(rawExpression)
    if (clean.isBlank()) {
      return EvaluationResult.Success(0.0, "0")
    }

    return try {
      val parser = Parser(clean, isRadMode)
      val value = parser.parse()
      if (value.isNaN()) {
        EvaluationResult.Error("Invalid input")
      } else if (value.isInfinite()) {
        EvaluationResult.Error("Cannot divide by 0")
      } else {
        EvaluationResult.Success(value, formatNumber(value))
      }
    } catch (e: ArithmeticException) {
      EvaluationResult.Error(e.message ?: "Math error")
    } catch (e: Exception) {
      EvaluationResult.Error("Invalid format")
    }
  }

  private fun sanitizeExpression(expr: String): String {
    var s = expr
      .replace("×", "*")
      .replace("÷", "/")
      .replace("−", "-")
      .replace(",", "") // strip display comma formatting
      .replace(" ", "")

    // Automatically complete unbalanced opening parentheses
    var openCount = 0
    for (ch in s) {
      if (ch == '(') openCount++
      else if (ch == ')') openCount--
    }
    while (openCount > 0) {
      s += ")"
      openCount--
    }

    return s
  }

  fun formatNumber(number: Double): String {
    if (number.isNaN()) return "Error"
    if (number.isInfinite()) return if (number > 0) "Infinity" else "-Infinity"

    // Fix floating point inaccuracy around 0
    val normalized = if (abs(number) < PRECISION_THRESHOLD) 0.0 else number

    // If extremely large or small, use scientific notation
    if (abs(normalized) >= 1e12 || (abs(normalized) > 0 && abs(normalized) < 1e-7)) {
      val symbols = DecimalFormatSymbols(Locale.US)
      val sciFormat = DecimalFormat("0.######E0", symbols)
      return sciFormat.format(normalized)
    }

    // Check if it is integer-like
    val rounded = normalized.roundToLong()
    if (abs(normalized - rounded.toDouble()) < PRECISION_THRESHOLD) {
      val symbols = DecimalFormatSymbols(Locale.US)
      val intFormat = DecimalFormat("#,##0", symbols)
      return intFormat.format(rounded)
    }

    // High precision decimal formatting with comma grouping and up to 10 decimal digits
    val symbols = DecimalFormatSymbols(Locale.US)
    val decFormat = DecimalFormat("#,##0.##########", symbols)
    return decFormat.format(normalized)
  }

  private class Parser(private val input: String, private val isRadMode: Boolean) {
    private var pos = 0

    private fun peek(): Char = if (pos < input.length) input[pos] else '\u0000'
    private fun get(): Char = if (pos < input.length) input[pos++] else '\u0000'

    fun parse(): Double {
      val result = parseExpression()
      if (pos < input.length) {
        throw IllegalArgumentException("Unexpected character at pos $pos: ${peek()}")
      }
      return result
    }

    // Expression: Term (('+' | '-') Term)*
    private fun parseExpression(): Double {
      var x = parseTerm()
      while (true) {
        when (peek()) {
          '+' -> {
            get()
            x += parseTerm()
          }
          '-' -> {
            get()
            x -= parseTerm()
          }
          else -> return x
        }
      }
    }

    // Term: Factor (('*' | '/') Factor)*
    private fun parseTerm(): Double {
      var x = parseFactor()
      while (true) {
        when (peek()) {
          '*' -> {
            get()
            x *= parseFactor()
          }
          '/' -> {
            get()
            val divisor = parseFactor()
            if (abs(divisor) < 1e-15) {
              throw ArithmeticException("Cannot divide by 0")
            }
            x /= divisor
          }
          else -> return x
        }
      }
    }

    // Factor: Power
    private fun parseFactor(): Double {
      var base = parseUnary()
      if (peek() == '^') {
        get()
        val exponent = parseUnary()
        base = base.pow(exponent)
      }
      return base
    }

    // Unary: ('+' | '-')? Primary ('%' | '!')*
    private fun parseUnary(): Double {
      if (peek() == '+') {
        get()
        return applyPostOps(parsePrimary())
      }
      if (peek() == '-') {
        get()
        return applyPostOps(-parsePrimary())
      }
      return applyPostOps(parsePrimary())
    }

    private fun applyPostOps(initial: Double): Double {
      var v = initial
      while (peek() == '%' || peek() == '!') {
        if (peek() == '%') {
          get()
          v /= 100.0
        } else if (peek() == '!') {
          get()
          v = factorial(v)
        }
      }
      return v
    }

    private fun factorial(n: Double): Double {
      if (n < 0 || n != n.roundToLong().toDouble()) {
        throw IllegalArgumentException("Factorial only for non-negative integers")
      }
      if (n > 170) return Double.POSITIVE_INFINITY // overflow for 64-bit float
      var res = 1.0
      val intN = n.toInt()
      for (i in 2..intN) {
        res *= i
      }
      return res
    }

    // Primary: Number | '(' Expression ')' | Function '(' Expression ')' | Constants (π, e)
    private fun parsePrimary(): Double {
      val c = peek()

      if (c == '(') {
        get() // consume '('
        val result = parseExpression()
        if (peek() == ')') {
          get() // consume ')'
        }
        return result
      }

      if (c == 'π') {
        get()
        return Math.PI
      }

      if (c == 'e' && (pos + 1 == input.length || !input[pos + 1].isLetter())) {
        get()
        return Math.E
      }

      if (c == '√') {
        get()
        val operand = if (peek() == '(') parsePrimary() else parseUnary()
        if (operand < 0) throw ArithmeticException("Invalid input for square root")
        return sqrt(operand)
      }

      // Check for named functions: sin, cos, tan, asin, acos, atan, ln, log, sqrt, abs
      if (c.isLetter()) {
        val start = pos
        while (pos < input.length && input[pos].isLetter()) {
          pos++
        }
        val func = input.substring(start, pos)
        val arg = parsePrimary()

        return when (func.lowercase(Locale.ROOT)) {
          "sin" -> {
            val radians = if (isRadMode) arg else Math.toRadians(arg)
            sin(radians)
          }
          "cos" -> {
            val radians = if (isRadMode) arg else Math.toRadians(arg)
            cos(radians)
          }
          "tan" -> {
            val radians = if (isRadMode) arg else Math.toRadians(arg)
            tan(radians)
          }
          "asin" -> {
            if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Invalid input for asin")
            val rad = asin(arg)
            if (isRadMode) rad else Math.toDegrees(rad)
          }
          "acos" -> {
            if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Invalid input for acos")
            val rad = acos(arg)
            if (isRadMode) rad else Math.toDegrees(rad)
          }
          "atan" -> {
            val rad = atan(arg)
            if (isRadMode) rad else Math.toDegrees(rad)
          }
          "ln" -> {
            if (arg <= 0) throw ArithmeticException("Invalid input for ln")
            ln(arg)
          }
          "log" -> {
            if (arg <= 0) throw ArithmeticException("Invalid input for log")
            log10(arg)
          }
          "sqrt" -> {
            if (arg < 0) throw ArithmeticException("Invalid input for sqrt")
            sqrt(arg)
          }
          "abs" -> abs(arg)
          else -> throw IllegalArgumentException("Unknown function $func")
        }
      }

      // Number literal
      val start = pos
      while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) {
        pos++
      }
      if (start == pos) {
        throw IllegalArgumentException("Expected number at $pos")
      }
      val numStr = input.substring(start, pos)
      return numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number $numStr")
    }
  }
}
