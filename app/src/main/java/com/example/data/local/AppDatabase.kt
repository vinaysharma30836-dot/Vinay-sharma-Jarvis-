package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatMessageEntity::class,
        MemoryEntity::class,
        ActionHistoryEntity::class,
        TaskEntity::class,
        KnowledgeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun memoryDao(): MemoryDao
    abstract fun actionHistoryDao(): ActionHistoryDao
    abstract fun taskDao(): TaskDao
    abstract fun knowledgeDao(): KnowledgeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cj_assistant_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate default Boss memories and starter schedule
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            prepopulateDefaults(database)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDefaults(db: AppDatabase) {
            // Initial Welcome Message
            db.chatDao().insertMessage(
                ChatMessageEntity(
                    sender = "CJ",
                    content = "System Online. Hello Boss! Main hoon CJ, aapka personal AI assistant. All sub-systems operational: Riyu voice profile active, phone automation ready, memory vault secured. Aap batayein Boss, aaj kya karna hai?",
                    mode = "QUICK"
                )
            )

            // Initial Boss Memories
            db.memoryDao().insertMemory(
                MemoryEntity(
                    category = "PREFERENCE",
                    title = "Title & Addressing",
                    detail = "User prefers to be addressed with high respect as 'Boss' in natural Hindi, Hinglish, or English.",
                    isApproved = true
                )
            )
            db.memoryDao().insertMemory(
                MemoryEntity(
                    category = "ROUTINE",
                    title = "Morning Briefing Routine",
                    detail = "Boss likes a structured morning briefing at 8:00 AM covering weather, top priorities, and daily tasks.",
                    isApproved = true
                )
            )
            db.memoryDao().insertMemory(
                MemoryEntity(
                    category = "INSTRUCTION",
                    title = "Zero Assumed Consent",
                    detail = "Never send messages, make calls, or modify external settings without explicit user review and confirmation.",
                    isApproved = true
                )
            )

            // Starter Daily Tasks & Habits
            db.taskDao().insertTask(
                TaskEntity(
                    title = "Morning Vision & Daily Briefing with CJ",
                    category = "SCHEDULE",
                    scheduledTime = "08:30 AM",
                    isCompleted = false,
                    streak = 12
                )
            )
            db.taskDao().insertTask(
                TaskEntity(
                    title = "Deep Work: Project Architecture & Engineering",
                    category = "TASK",
                    scheduledTime = "10:30 AM",
                    isCompleted = false,
                    streak = 5
                )
            )
            db.taskDao().insertTask(
                TaskEntity(
                    title = "Review WhatsApp Messages & Pending Action Confirmations",
                    category = "TASK",
                    scheduledTime = "02:00 PM",
                    isCompleted = false,
                    streak = 3
                )
            )
            db.taskDao().insertTask(
                TaskEntity(
                    title = "Hydration & Health Check",
                    category = "HABIT",
                    scheduledTime = "All Day",
                    isCompleted = true,
                    streak = 14
                )
            )

            // Starter Knowledge items
            db.knowledgeDao().insertKnowledge(
                KnowledgeEntity(
                    title = "CJ 2.0 Operational Manual",
                    type = "DOCUMENT",
                    content = "CJ (Cybernetic Jarvis) 2.0 is designed as a private, high-intelligence personal assistant. It adheres to strict user permission protocols, voice cloning authorized as Riyu Voice, and full Android automation via Android Intents.",
                    summary = "Official operational doctrine and zero-assumed consent guidelines.",
                    tags = "cj,manual,security,voice"
                )
            )
        }
    }
}
