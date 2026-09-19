package com.example

import com.example.engine.EvaluationResult
import com.example.engine.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_and_multiplication_precedence() {
    val result = ExpressionEvaluator.evaluate("2+3×4")
    assertTrue(result is EvaluationResult.Success)
    assertEquals("14", (result as EvaluationResult.Success).formattedText)
  }

  @Test
  fun parentheses_evaluation() {
    val result = ExpressionEvaluator.evaluate("(2+3)×4")
    assertTrue(result is EvaluationResult.Success)
    assertEquals("20", (result as EvaluationResult.Success).formattedText)
  }

  @Test
  fun decimal_precision() {
    val result = ExpressionEvaluator.evaluate("0.1+0.2")
    assertTrue(result is EvaluationResult.Success)
    assertEquals("0.3", (result as EvaluationResult.Success).formattedText)
  }

  @Test
  fun percentage_calculation() {
    val result = ExpressionEvaluator.evaluate("200×10%")
    assertTrue(result is EvaluationResult.Success)
    assertEquals("20", (result as EvaluationResult.Success).formattedText)
  }

  @Test
  fun power_and_sqrt() {
    val powerResult = ExpressionEvaluator.evaluate("2^8")
    assertTrue(powerResult is EvaluationResult.Success)
    assertEquals("256", (powerResult as EvaluationResult.Success).formattedText)

    val sqrtResult = ExpressionEvaluator.evaluate("√(16)")
    assertTrue(sqrtResult is EvaluationResult.Success)
    assertEquals("4", (sqrtResult as EvaluationResult.Success).formattedText)
  }

  @Test
  fun factorial_calculation() {
    val result = ExpressionEvaluator.evaluate("5!")
    assertTrue(result is EvaluationResult.Success)
    assertEquals("120", (result as EvaluationResult.Success).formattedText)
  }

  @Test
  fun division_by_zero_error() {
    val result = ExpressionEvaluator.evaluate("10÷0")
    assertTrue(result is EvaluationResult.Error)
    assertEquals("Cannot divide by 0", (result as EvaluationResult.Error).message)
  }

  @Test
  fun trigonometry_degrees() {
    val result = ExpressionEvaluator.evaluate("sin(90)", isRadMode = false)
    assertTrue(result is EvaluationResult.Success)
    assertEquals("1", (result as EvaluationResult.Success).formattedText)
  }
}

