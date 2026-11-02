package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "BOSS" or "CJ"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String = "QUICK", // QUICK, DETAILED, RESEARCH, STUDY, CREATIVE, EXPERT
    val actionType: String? = null,
    val actionPayload: String? = null,
    val actionStatus: String? = null // PENDING_APPROVAL, EXECUTED, CANCELLED
)

@Entity(tableName = "personal_memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // PREFERENCE, ROUTINE, IMPORTANT_DATE, GOAL, CONTACT, INSTRUCTION
    val title: String,
    val detail: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isApproved: Boolean = true
)

@Entity(tableName = "action_history")
data class ActionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionType: String, // WHATSAPP, INSTAGRAM, YOUTUBE, CALL, ALARM, SYSTEM_SETTING, WEB
    val title: String,
    val target: String,
    val status: String, // COMPLETED, REJECTED, EMERGENCY_STOPPED
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "tasks_and_habits")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // TASK, SCHEDULE, HABIT, GOAL
    val scheduledTime: String = "",
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "knowledge_items")
data class KnowledgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // DOCUMENT, NOTE, CODE_SNIPPET, IMAGE_ANALYSIS
    val content: String,
    val summary: String,
    val tags: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
