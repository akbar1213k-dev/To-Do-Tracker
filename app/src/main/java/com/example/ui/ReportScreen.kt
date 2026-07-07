package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReportScreen(viewModel: TaskViewModel) {
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportData(context, it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importData(context, it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Daily Report summary", style = MaterialTheme.typography.headlineMedium)

        val totalTasks = allTasks.size
        val completedTasks = allTasks.count { it.isCompleted }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Overall Statistics", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total Tasks: $totalTasks")
                Text("Completed: $completedTasks", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Pending: ${totalTasks - completedTasks}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Backup & Restore", style = MaterialTheme.typography.titleLarge)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { exportLauncher.launch("todotracker_backup.json") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.FileDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.FileUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Import")
            }
        }
    }
}
