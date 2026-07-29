package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorApp() {
    var displayValue by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableDoubleStateOf(0.0) }
    var pendingOperation by remember { mutableStateOf<String?>(null) }
    var isNewOperand by remember { mutableStateOf(true) }

    fun onDigit(digit: String) {
        if (displayValue == "0" || isNewOperand) {
            displayValue = digit
            isNewOperand = false
        } else {
            displayValue += digit
        }
    }

    fun onOp(op: String) {
        firstOperand = displayValue.toDoubleOrNull() ?: 0.0
        pendingOperation = op
        isNewOperand = true
    }

    fun onEquals() {
        val secondOperand = displayValue.toDoubleOrNull() ?: 0.0
        val result = when (pendingOperation) {
            "+" -> firstOperand + secondOperand
            "-" -> firstOperand - secondOperand
            "×" -> firstOperand * secondOperand
            "÷" -> if (secondOperand != 0.0) firstOperand / secondOperand else Double.NaN
            else -> secondOperand
        }
        displayValue = if (result % 1.0 == 0.0 && !result.isNaN()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
        pendingOperation = null
        isNewOperand = true
    }

    fun onClear() {
        displayValue = "0"
        firstOperand = 0.0
        pendingOperation = null
        isNewOperand = true
    }

    val orangeColor = Color(0xFFFF9F0A)
    val darkGray = Color(0xFF333333)
    val lightGray = Color(0xFFA5A5A5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Calculator Screen Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = displayValue,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Buttons Layout
        val buttons = listOf(
            listOf("C" to lightGray, "±" to lightGray, "%" to lightGray, "÷" to orangeColor),
            listOf("7" to darkGray, "8" to darkGray, "9" to darkGray, "×" to orangeColor),
            listOf("4" to darkGray, "5" to darkGray, "6" to darkGray, "-" to orangeColor),
            listOf("1" to darkGray, "2" to darkGray, "3" to darkGray, "+" to orangeColor),
            listOf("0" to darkGray, "." to darkGray, "=" to orangeColor)
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { (label, bg) ->
                    val weight = if (label == "0") 2.1f else 1f
                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .padding(4.dp)
                            .height(52.dp)
                            .clip(CircleShape)
                            .background(bg)
                            .clickable {
                                when (label) {
                                    "C" -> onClear()
                                    "±" -> {
                                        displayValue = if (displayValue.startsWith("-")) displayValue.drop(1) else "-$displayValue"
                                    }
                                    "%" -> {
                                        val v = displayValue.toDoubleOrNull() ?: 0.0
                                        displayValue = (v / 100.0).toString()
                                    }
                                    "+", "-", "×", "÷" -> onOp(label)
                                    "=" -> onEquals()
                                    else -> onDigit(label)
                                }
                            }
                            .testTag("calc_btn_$label"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (bg == lightGray) Color.Black else Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
