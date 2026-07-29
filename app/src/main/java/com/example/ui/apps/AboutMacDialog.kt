package com.example.ui.apps

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MacOSBlue

@Composable
fun AboutMacDialog(
    isDarkMode: Boolean
) {
    val bg = if (isDarkMode) Color(0xFF2B2B2B) else Color(0xFFF0F0F0)
    val textColor = if (isDarkMode) Color.White else Color(0xFF1D1D1F)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MacOSBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "macOS Sequoia",
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Version 15.0",
            color = textColor.copy(alpha = 0.6f),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDarkMode) Color(0xFF222222) else Color.White)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("MacBook Pro", fontWeight = FontWeight.Bold, color = textColor, fontSize = 13.sp)
                Text("16-inch, 2026", color = textColor.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Chip", fontWeight = FontWeight.Medium, color = textColor, fontSize = 12.sp)
                Text("Apple M3 Max / ARM64 Octa", color = textColor.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Memory", fontWeight = FontWeight.Medium, color = textColor, fontSize = 12.sp)
                Text("16 GB Unified Memory", color = textColor.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Serial Number", fontWeight = FontWeight.Medium, color = textColor, fontSize = 12.sp)
                Text("C02G90XXMD6M", color = textColor.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = MacOSBlue)
        ) {
            Text("System Report...", color = Color.White, fontSize = 12.sp)
        }
    }
}
