package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Task
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasksForDate.collectAsStateWithLifecycle()
    val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, modifier = Modifier.testTag("add_task_fab")) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            HeaderSection(currentDate, onDateChange = { viewModel.setDate(it) })
            
            Spacer(modifier = Modifier.height(16.dp))

            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks for today. Enjoy your day!", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, recurringDays ->
                viewModel.addTask(name, recurringDays)
                showDialog = false
            }
        )
    }
}

@Composable
fun HeaderSection(currentDate: String, onDateChange: (String) -> Unit) {
    // Simple date navigation
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = format.parse(currentDate) ?: Date()
    val calendar = Calendar.getInstance().apply { time = date }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(onClick = {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            onDateChange(format.format(calendar.time))
        }) {
            Text("< Prev")
        }
        
        Text(currentDate, style = MaterialTheme.typography.titleMedium)

        OutlinedButton(onClick = {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            onDateChange(format.format(calendar.time))
        }) {
            Text("Next >")
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                modifier = Modifier.testTag("checkbox_${task.id}")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_${task.id}")) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Task")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onConfirm: (String, Set<Int>) -> Unit) {
    var text by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }
    val selectedDays = remember { mutableStateListOf<Int>() }

    val daysOfWeek = listOf(
        "Senin" to java.util.Calendar.MONDAY,
        "Selasa" to java.util.Calendar.TUESDAY,
        "Rabu" to java.util.Calendar.WEDNESDAY,
        "Kamis" to java.util.Calendar.THURSDAY,
        "Jumat" to java.util.Calendar.FRIDAY,
        "Sabtu" to java.util.Calendar.SATURDAY,
        "Minggu" to java.util.Calendar.SUNDAY
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Task Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("task_name_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = !isRecurring, onClick = { isRecurring = false })
                    Text("Sekali")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = isRecurring, onClick = { isRecurring = true })
                    Text("Beberapa Hari")
                }
                if (isRecurring) {
                    Spacer(modifier = Modifier.height(8.dp))
                    daysOfWeek.forEach { (name, dayValue) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = selectedDays.contains(dayValue),
                                onCheckedChange = { checked ->
                                    if (checked) selectedDays.add(dayValue)
                                    else selectedDays.remove(dayValue)
                                }
                            )
                            Text(name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text, if (isRecurring) selectedDays.toSet() else emptySet()) },
                modifier = Modifier.testTag("confirm_task_btn")
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}