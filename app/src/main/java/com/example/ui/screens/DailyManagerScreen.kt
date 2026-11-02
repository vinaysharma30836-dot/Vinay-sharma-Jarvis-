package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.TaskEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGradient
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.HolographicCyan
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack

@Composable
fun DailyManagerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskCategory by remember { mutableStateOf("TASK") } // TASK, SCHEDULE, HABIT
    var newTaskTime by remember { mutableStateOf("11:00 AM") }

    val scheduleList = remember(tasks) { tasks.filter { it.category == "SCHEDULE" } }
    val todoList = remember(tasks) { tasks.filter { it.category == "TASK" } }
    val habitList = remember(tasks) { tasks.filter { it.category == "HABIT" } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
    ) {
        // Morning Briefing Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorderGradient, shape = RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FEATURE C: PERSONAL DAILY MANAGER",
                        color = HolographicCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Interactive Morning Briefing",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Hear CJ compile today's weather, priority agenda & habits in Riyu Voice.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { viewModel.generateMorningBriefing() },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = VoidBlack),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("play_morning_briefing_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Briefing",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Proactive Intelligence Suggestion Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface, shape = RoundedCornerShape(10.dp))
                .border(1.dp, CyberBorder, shape = RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TipsAndUpdates,
                contentDescription = "Proactive Insight",
                tint = NeonAmber,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Proactive Tip: 3 tasks remain uncompleted today. Would you like CJ to schedule focus time?",
                color = TextWhite,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section Headers & Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TODAY'S TIMELINE & HABITS",
                color = HolographicCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Button(
                onClick = { showAddTaskDialog = !showAddTaskDialog },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_task_toggle_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showAddTaskDialog) "Close" else "Add Item", fontSize = 11.sp, color = TextWhite)
            }
        }

        // Add task expanded form
        if (showAddTaskDialog) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, shape = RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g. Architecture Sync") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_task_title_input"),
                        colors = outlineColors()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTaskTime,
                            onValueChange = { newTaskTime = it },
                            label = { Text("Time") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("new_task_time_input"),
                            colors = outlineColors()
                        )
                        Button(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    viewModel.addTask(newTaskTitle, newTaskCategory, newTaskTime)
                                    newTaskTitle = ""
                                    showAddTaskDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = VoidBlack),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .testTag("save_task_button")
                        ) {
                            Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Habits with Streaks
            if (habitList.isNotEmpty()) {
                item {
                    Text(
                        text = "🔥 Daily Habits & Streaks",
                        color = NeonOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                items(habitList, key = { it.id }) { habit ->
                    HabitCardItem(
                        habit = habit,
                        onIncrementStreak = { viewModel.incrementHabitStreak(habit.id) },
                        onDelete = { viewModel.deleteTask(habit.id) }
                    )
                }
            }

            // Tasks and Schedule
            item {
                Text(
                    text = "📋 Agenda & Tasks (${tasks.count { it.category != "HABIT" }})",
                    color = ElectricBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                )
            }

            items(tasks.filter { it.category != "HABIT" }, key = { it.id }) { task ->
                TaskCardItem(
                    task = task,
                    onToggle = { viewModel.toggleTask(task.id, !task.isCompleted) },
                    onDelete = { viewModel.deleteTask(task.id) }
                )
            }
        }
    }
}

@Composable
fun HabitCardItem(
    habit: TaskEntity,
    onIncrementStreak: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface, shape = RoundedCornerShape(10.dp))
            .border(1.dp, CyberBorder, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
            .testTag("habit_item_${habit.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak",
                tint = NeonOrange,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = habit.title,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${habit.streak} Days Active Streak",
                    color = HolographicCyan,
                    fontSize = 11.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onIncrementStreak,
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("increment_streak_${habit.id}")
            ) {
                Text("+1 Day", fontSize = 11.sp, color = NeonOrange)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun TaskCardItem(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface, shape = RoundedCornerShape(10.dp))
            .border(1.dp, CyberBorder, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("task_item_${task.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = ElectricBlue,
                    checkmarkColor = VoidBlack,
                    uncheckedColor = CyberBorder
                ),
                modifier = Modifier.testTag("checkbox_${task.id}")
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = task.title,
                    color = if (task.isCompleted) TextMuted else TextWhite,
                    fontSize = 13.sp,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.scheduledTime.isNotBlank()) {
                    Text(
                        text = "Scheduled: ${task.scheduledTime}",
                        color = HolographicCyan,
                        fontSize = 10.sp
                    )
                }
            }
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
}
