package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Win11Blue
import com.example.ui.theme.Win11DarkFlyout
import com.example.ui.theme.Win11LightFlyout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WindowsCalendarFlyout(
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isDarkMode) Win11DarkFlyout else Win11LightFlyout
    val textColor = if (isDarkMode) Color.White else Color(0xFF1C1C1C)
    val cardBg = if (isDarkMode) Color(0xFF2C2C2C) else Color.White
    val borderColor = if (isDarkMode) Color(0x33FFFFFF) else Color(0x1A000000)

    val calendar = remember { Calendar.getInstance() }
    val monthName = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time) }
    val todayDay = remember { calendar.get(Calendar.DAY_OF_MONTH) }

    val daysOfWeek = remember { listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su") }
    val daysInMonth = remember { (1..31).toList() }

    Surface(
        modifier = modifier
            .width(360.dp)
            .shadow(24.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .testTag("windows_calendar_flyout"),
        color = bg,
        tonalElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Notifications Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Win11Blue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notifications", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Text("Clear all", color = Win11Blue, fontSize = 12.sp, modifier = Modifier.clickable { })
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notification Card Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(cardBg)
                    .padding(12.dp)
            ) {
                Column {
                    Text("Windows Security", color = Win11Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("No actions needed. Smart App Control is on.", color = textColor, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = borderColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(monthName, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row {
                    IconButton(onClick = { }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = textColor, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = { }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = textColor, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Days of Week Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        color = textColor.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(daysInMonth) { day ->
                    val isToday = day == todayDay
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isToday) Win11Blue else Color.Transparent)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            color = if (isToday) Color.White else textColor,
                            fontSize = 12.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
