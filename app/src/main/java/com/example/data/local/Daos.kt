package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(limit: Int): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("UPDATE chat_messages SET actionStatus = :status WHERE id = :id")
    suspend fun updateActionStatus(id: Long, status: String)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM personal_memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM personal_memories WHERE category = :category ORDER BY timestamp DESC")
    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("DELETE FROM personal_memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("DELETE FROM personal_memories")
    suspend fun clearAllMemories()
}

@Dao
interface ActionHistoryDao {
    @Query("SELECT * FROM action_history ORDER BY timestamp DESC")
    fun getAllActionHistory(): Flow<List<ActionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: ActionHistoryEntity): Long

    @Query("DELETE FROM action_history")
    suspend fun clearActionHistory()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks_and_habits ORDER BY isCompleted ASC, timestamp DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks_and_habits SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, isCompleted: Boolean)

    @Query("UPDATE tasks_and_habits SET streak = streak + 1 WHERE id = :id")
    suspend fun incrementStreak(id: Long)

    @Query("DELETE FROM tasks_and_habits WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
}

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge_items ORDER BY timestamp DESC")
    fun getAllKnowledgeItems(): Flow<List<KnowledgeEntity>>

    @Query("SELECT * FROM knowledge_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchKnowledge(query: String): Flow<List<KnowledgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledge(item: KnowledgeEntity): Long

    @Delete
    suspend fun deleteKnowledge(item: KnowledgeEntity)

    @Query("DELETE FROM knowledge_items WHERE id = :id")
    suspend fun deleteKnowledgeById(id: Long)
}
