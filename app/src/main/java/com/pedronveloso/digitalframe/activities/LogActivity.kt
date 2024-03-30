package com.pedronveloso.digitalframe.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.utils.log.LogEntry
import com.pedronveloso.digitalframe.utils.log.LogLevel
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogActivity : ComponentActivity() {

    private val logger = LogStoreProvider.getLogStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DigitalFrameTheme {
                val context = LocalContext.current

                LogScreen(logEntries = logger.getLogs()) {

                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                    val logText = logger.getLogs().joinToString(separator = "\n\n") { logEntry ->
                        "${logEntry.timestamp} [${logEntry.level}] ${logEntry.tag}: ${logEntry.message}"
                    }
                    val clipData = ClipData.newPlainText("Log Entries", logText)
                    clipboardManager.setPrimaryClip(clipData)

                    Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LogScreen(logEntries: List<LogEntry>, onCopyToClipboard: () -> Unit) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.pref_logs_title)) },
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(id = R.string.pref_logs_privacy_notice),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = onCopyToClipboard,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(stringResource(id = R.string.pref_logs_copy_clipboard))
                    }

                    logEntries.forEach { logEntry ->
                        LogEntryItem(logEntry)
                    }
                }
            }
        )
    }

    @Composable
    fun LogEntryItem(logEntry: LogEntry) {
        val color = if (logEntry.level == LogLevel.ERROR) {
            Color(0xFFFF6B6B)
        } else {
            MaterialTheme.colorScheme.onSurface
        }

        val formattedTimestamp = logEntry.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        Text(
            text = "$formattedTimestamp ${logEntry.tag}: ${logEntry.message}",
            color = color,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }

    @Composable
    @Preview
    fun LogScreenPreview() {
        val logEntries = listOf(
            LogEntry("MainActivity", "App started", LocalDateTime.now(), LogLevel.INFO),
            LogEntry("NetworkService", "Error fetching data", LocalDateTime.now(), LogLevel.ERROR)
        )

        DigitalFrameTheme {
            LogScreen(logEntries) {
                // Do nothing.
            }
        }
    }
}