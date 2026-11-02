package com.example.data.repository

import com.example.data.local.ActionHistoryDao
import com.example.data.local.ActionHistoryEntity
import com.example.data.local.ChatDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.KnowledgeDao
import com.example.data.local.KnowledgeEntity
import com.example.data.local.MemoryDao
import com.example.data.local.MemoryEntity
import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class CjRepository(
    private val chatDao: ChatDao,
    private val memoryDao: MemoryDao,
    private val actionHistoryDao: ActionHistoryDao,
    private val taskDao: TaskDao,
    private val knowledgeDao: KnowledgeDao
) {
    // Chat messages
    val messages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    suspend fun saveMessage(message: ChatMessageEntity): Long = chatDao.insertMessage(message)
    suspend fun updateActionStatus(id: Long, status: String) = chatDao.updateActionStatus(id, status)
    suspend fun clearChat() = chatDao.clearHistory()

    // Memories
    val memories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()
    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>> = memoryDao.getMemoriesByCategory(category)
    suspend fun saveMemory(memory: MemoryEntity): Long = memoryDao.insertMemory(memory)
    suspend fun updateMemory(memory: MemoryEntity) = memoryDao.updateMemory(memory)
    suspend fun deleteMemory(memory: MemoryEntity) = memoryDao.deleteMemory(memory)
    suspend fun deleteMemoryById(id: Long) = memoryDao.deleteMemoryById(id)
    suspend fun clearAllMemories() = memoryDao.clearAllMemories()

    // Action History
    val actionHistory: Flow<List<ActionHistoryEntity>> = actionHistoryDao.getAllActionHistory()
    suspend fun logAction(action: ActionHistoryEntity): Long = actionHistoryDao.insertAction(action)
    suspend fun clearActionHistory() = actionHistoryDao.clearActionHistory()

    // Tasks & Habits
    val tasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    suspend fun saveTask(task: TaskEntity): Long = taskDao.insertTask(task)
    suspend fun setTaskCompleted(id: Long, completed: Boolean) = taskDao.setTaskCompleted(id, completed)
    suspend fun incrementStreak(id: Long) = taskDao.incrementStreak(id)
    suspend fun deleteTask(id: Long) = taskDao.deleteTaskById(id)

    // Knowledge Center
    val knowledgeItems: Flow<List<KnowledgeEntity>> = knowledgeDao.getAllKnowledgeItems()
    fun searchKnowledge(query: String): Flow<List<KnowledgeEntity>> = knowledgeDao.searchKnowledge(query)
    suspend fun saveKnowledge(item: KnowledgeEntity): Long = knowledgeDao.insertKnowledge(item)
    suspend fun deleteKnowledge(item: KnowledgeEntity) = knowledgeDao.deleteKnowledge(item)
    suspend fun deleteKnowledgeById(id: Long) = knowledgeDao.deleteKnowledgeById(id)
}
