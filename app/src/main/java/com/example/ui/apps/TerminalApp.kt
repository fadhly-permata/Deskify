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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class TerminalLine(
    val text: String,
    val color: Color = Color(0xFFD4D4D4),
    val isPrompt: Boolean = false
)

@Composable
fun TerminalApp(
    installedPackages: List<String> = emptyList()
) {
    var inputCommand by remember { mutableStateOf("") }
    val terminalLines = remember {
        mutableStateListOf(
            TerminalLine("Last login: Wed Jul 29 14:18:47 on ttys001"),
            TerminalLine("Type 'help' or 'macfetch' to get started.\n", Color(0xFF4FC3F7))
        )
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    fun executeCommand(cmd: String) {
        val trimmed = cmd.trim()
        terminalLines.add(TerminalLine("user@macbook-pro ~ % $trimmed", Color(0xFF81C784), isPrompt = true))

        if (trimmed.isEmpty()) return

        when {
            trimmed == "clear" -> {
                terminalLines.clear()
            }
            trimmed == "help" -> {
                terminalLines.add(TerminalLine("Available Terminal Commands:", Color(0xFFFFD54F)))
                terminalLines.add(TerminalLine("  macfetch / neofetch : Display macOS system specs & ASCII logo"))
                terminalLines.add(TerminalLine("  ls                  : List files in current directory"))
                terminalLines.add(TerminalLine("  pwd                 : Print working directory"))
                terminalLines.add(TerminalLine("  date                : Display system date and time"))
                terminalLines.add(TerminalLine("  echo <text>         : Print text back to screen"))
                terminalLines.add(TerminalLine("  pm list packages    : List installed Android app packages"))
                terminalLines.add(TerminalLine("  uname -a            : Show Darwin kernel information"))
                terminalLines.add(TerminalLine("  clear               : Clear terminal output"))
            }
            trimmed == "neofetch" || trimmed == "macfetch" -> {
                val asciiLogo = """
                    	                   user@macbook-pro
       .:'                   ------------------
    __ :'_                   OS: macOS Sequoia 15.0 (Android Native)
  .'`__`  '.                 Host: MacBook Pro (ARM64 Octa-Core)
 /  (__)    \                Kernel: Darwin 24.0.0 (Linux 6.1)
|   (__)     |               Uptime: 4 hours, 20 mins
|            |               Packages: ${installedPackages.size} (pm)
 \          /                Shell: zsh 5.9 (x86_64-apple-darwin24.0)
  '.____._.'                 Terminal: macOS Terminal
                             Memory: 8192MB / 16384MB
                """.trimIndent()
                terminalLines.add(TerminalLine(asciiLogo, Color(0xFF64B5F6)))
            }
            trimmed == "ls" -> {
                terminalLines.add(TerminalLine("Applications  Desktop  Documents  Downloads  Library  Movies  Music  Pictures", Color(0xFF81C784)))
            }
            trimmed == "pwd" -> {
                terminalLines.add(TerminalLine("/Users/user", Color(0xFFD4D4D4)))
            }
            trimmed == "date" -> {
                terminalLines.add(TerminalLine(java.util.Date().toString(), Color(0xFFD4D4D4)))
            }
            trimmed.startsWith("echo ") -> {
                terminalLines.add(TerminalLine(trimmed.removePrefix("echo "), Color(0xFFD4D4D4)))
            }
            trimmed == "uname -a" -> {
                terminalLines.add(TerminalLine("Darwin macbook-pro.local 24.0.0 Darwin Kernel Version 24.0.0: arm64", Color(0xFFD4D4D4)))
            }
            trimmed == "pm list packages" -> {
                if (installedPackages.isEmpty()) {
                    terminalLines.add(TerminalLine("package:com.example.macoslauncher", Color(0xFFD4D4D4)))
                    terminalLines.add(TerminalLine("package:com.android.chrome", Color(0xFFD4D4D4)))
                    terminalLines.add(TerminalLine("package:com.google.android.youtube", Color(0xFFD4D4D4)))
                } else {
                    installedPackages.take(20).forEach { pkg ->
                        terminalLines.add(TerminalLine("package:$pkg", Color(0xFFD4D4D4)))
                    }
                }
            }
            else -> {
                terminalLines.add(TerminalLine("zsh: command not found: $trimmed", Color(0xFFE57373)))
            }
        }

        scope.launch {
            if (terminalLines.isNotEmpty()) {
                listState.animateScrollToItem(terminalLines.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(12.dp)
            .testTag("terminal_app")
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(terminalLines) { line ->
                Text(
                    text = line.text,
                    color = line.color,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (line.isPrompt) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        // Active Input Prompt Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "user@macbook-pro ~ % ",
                color = Color(0xFF81C784),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            BasicTextField(
                value = inputCommand,
                onValueChange = { inputCommand = it },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        executeCommand(inputCommand)
                        inputCommand = ""
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_command_input")
            )
        }
    }
}
