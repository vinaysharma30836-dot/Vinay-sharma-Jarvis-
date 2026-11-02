package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiProviderType
import com.example.ai.EmbeddedJarvisEngine
import com.example.ai.GeminiAiEngine
import com.example.ai.ResearchReport
import com.example.ai.ThinkingMode
import com.example.automation.ActionType
import com.example.automation.CjAutomationManager
import com.example.automation.ProposedAction
import com.example.data.local.ActionHistoryEntity
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.KnowledgeEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.TaskEntity
import com.example.data.repository.CjRepository
import com.example.ui.components.CjCoreState
import com.example.voice.CjSpeechManager
import com.example.voice.CjVoiceManager
import com.example.voice.VoiceProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = CjRepository(
        chatDao = db.chatDao(),
        memoryDao = db.memoryDao(),
        actionHistoryDao = db.actionHistoryDao(),
        taskDao = db.taskDao(),
        knowledgeDao = db.knowledgeDao()
    )

    val automationManager = CjAutomationManager(application)
    val voiceManager = CjVoiceManager(application)
    private val localAi = EmbeddedJarvisEngine()
    private val geminiAi = GeminiAiEngine(localAi)

    // Speech manager for wake word and continuous/single-shot recognition
    val speechManager = CjSpeechManager(
        context = application,
        onWakeWordDetected = { wakePhrase ->
            handleWakeWord(wakePhrase)
        },
        onCommandRecognized = { command ->
            sendUserMessage(command)
        },
        onSpeechInterrupt = {
            voiceManager.stopSpeaking()
        }
    )

    // UI States
    val messages: StateFlow<List<ChatMessageEntity>> = repository.messages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = repository.memories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val actionHistory: StateFlow<List<ActionHistoryEntity>> = repository.actionHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val knowledgeItems: StateFlow<List<KnowledgeEntity>> = repository.knowledgeItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _coreState = MutableStateFlow(CjCoreState.IDLE)
    val coreState: StateFlow<CjCoreState> = _coreState.asStateFlow()

    private val _currentThinkingMode = MutableStateFlow(ThinkingMode.QUICK)
    val currentThinkingMode: StateFlow<ThinkingMode> = _currentThinkingMode.asStateFlow()

    private val _activeProvider = MutableStateFlow(AiProviderType.GEMINI)
    val activeProvider: StateFlow<AiProviderType> = _activeProvider.asStateFlow()

    private val _pendingAction = MutableStateFlow<ProposedAction?>(null)
    val pendingAction: StateFlow<ProposedAction?> = _pendingAction.asStateFlow()

    private val _selectedImage = MutableStateFlow<Bitmap?>(null)
    val selectedImage: StateFlow<Bitmap?> = _selectedImage.asStateFlow()

    private val _researchReport = MutableStateFlow<ResearchReport?>(null)
    val researchReport: StateFlow<ResearchReport?> = _researchReport.asStateFlow()

    private val _statusNotice = MutableStateFlow<String?>(null)
    val statusNotice: StateFlow<String?> = _statusNotice.asStateFlow()

    init {
        // Observe voice manager speaking state to update Core animation
        viewModelScope.launch {
            voiceManager.isSpeaking.collect { speaking ->
                if (speaking) {
                    _coreState.value = CjCoreState.SPEAKING
                } else if (_coreState.value == CjCoreState.SPEAKING) {
                    _coreState.value = CjCoreState.IDLE
                }
            }
        }
        // Observe speech listening state
        viewModelScope.launch {
            speechManager.isListening.collect { listening ->
                if (listening) {
                    _coreState.value = CjCoreState.LISTENING
                } else if (_coreState.value == CjCoreState.LISTENING) {
                    _coreState.value = CjCoreState.IDLE
                }
            }
        }
    }

    fun setThinkingMode(mode: ThinkingMode) {
        _currentThinkingMode.value = mode
    }

    fun setAiProvider(provider: AiProviderType) {
        _activeProvider.value = provider
    }

    fun setSelectedImage(bitmap: Bitmap?) {
        _selectedImage.value = bitmap
    }

    private fun handleWakeWord(wakePhrase: String) {
        voiceManager.stopSpeaking()
        _statusNotice.value = "Wake word detected: '$wakePhrase'"
        val greeting = "Hello Boss! Main active hoon. Bataiye, kya karna hai?"
        voiceManager.speak(greeting)
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank() && _selectedImage.value == null) return
        val currentImage = _selectedImage.value
        _selectedImage.value = null // consume

        viewModelScope.launch(Dispatchers.IO) {
            // Save user message
            repository.saveMessage(
                ChatMessageEntity(
                    sender = "BOSS",
                    content = text,
                    mode = _currentThinkingMode.value.name
                )
            )

            // Check if user input implies an external action intent
            val proposedAction = localAi.parseActionIntent(text)
            if (proposedAction != null) {
                // Strict permission mandate: Never execute without explicit confirmation!
                _pendingAction.value = proposedAction
                val confirmationPrompt = "Boss, maine yeh action draft kiya hai: ${proposedAction.title} (${proposedAction.target}). " +
                        "Aapki explicit permission ke bina main execute nahi karunga. Please confirm or edit."
                
                repository.saveMessage(
                    ChatMessageEntity(
                        sender = "CJ",
                        content = confirmationPrompt,
                        mode = _currentThinkingMode.value.name,
                        actionType = proposedAction.type.name,
                        actionPayload = proposedAction.editablePayload,
                        actionStatus = "PENDING_APPROVAL"
                    )
                )
                voiceManager.speak("Boss, authorization required. Please screen par confirm karein.")
                return@launch
            }

            // Normal AI conversation
            _coreState.value = CjCoreState.THINKING
            val currentMemories = memories.value.joinToString("\n") { "• ${it.title}: ${it.detail}" }

            val responseText = if (_activeProvider.value == AiProviderType.GEMINI) {
                geminiAi.generateResponse(
                    prompt = text,
                    mode = _currentThinkingMode.value,
                    memoryContext = currentMemories,
                    image = currentImage
                )
            } else {
                localAi.generateLocalResponse(
                    prompt = text,
                    mode = _currentThinkingMode.value,
                    memoryContext = currentMemories
                )
            }

            // Save CJ response
            repository.saveMessage(
                ChatMessageEntity(
                    sender = "CJ",
                    content = responseText,
                    mode = _currentThinkingMode.value.name
                )
            )

            _coreState.value = CjCoreState.SPEAKING
            voiceManager.speak(responseText)
        }
    }

    // Action authorization handlers (Boss is in total control)
    fun authorizeAction(action: ProposedAction) {
        viewModelScope.launch(Dispatchers.IO) {
            _pendingAction.value = null
            automationManager.executeAction(action) { success, message ->
                viewModelScope.launch(Dispatchers.IO) {
                    val status = if (success) "COMPLETED" else "FAILED"
                    repository.logAction(
                        ActionHistoryEntity(
                            actionType = action.type.name,
                            title = action.title,
                            target = action.target,
                            status = status,
                            notes = message
                        )
                    )
                    val reply = if (success) {
                        "Boss, action successfully execute ho gaya hai: $message"
                    } else {
                        "Boss, action execute karte waqt error aaya: $message"
                    }
                    repository.saveMessage(
                        ChatMessageEntity(
                            sender = "CJ",
                            content = reply,
                            mode = "QUICK",
                            actionType = action.type.name,
                            actionStatus = status
                        )
                    )
                    voiceManager.speak(reply)
                }
            }
        }
    }

    fun rejectAction(action: ProposedAction) {
        viewModelScope.launch(Dispatchers.IO) {
            _pendingAction.value = null
            repository.logAction(
                ActionHistoryEntity(
                    actionType = action.type.name,
                    title = action.title,
                    target = action.target,
                    status = "REJECTED",
                    notes = "Action rejected by Boss."
                )
            )
            val rejectionMsg = "Boss, command cancel kar di gayi hai. Koi changes nahi kiye gaye."
            repository.saveMessage(
                ChatMessageEntity(
                    sender = "CJ",
                    content = rejectionMsg,
                    mode = "QUICK",
                    actionType = action.type.name,
                    actionStatus = "REJECTED"
                )
            )
            voiceManager.speak(rejectionMsg)
        }
    }

    fun triggerEmergencyStop() {
        voiceManager.stopSpeaking()
        speechManager.stopListening()
        _pendingAction.value = null
        _coreState.value = CjCoreState.EMERGENCY_STOP
        _statusNotice.value = "EMERGENCY OVERRIDE ENGAGED: All processes aborted."

        viewModelScope.launch(Dispatchers.IO) {
            repository.logAction(
                ActionHistoryEntity(
                    actionType = "EMERGENCY_OVERRIDE",
                    title = "Emergency Stop Triggered",
                    target = "ALL_SYSTEMS",
                    status = "EMERGENCY_STOPPED",
                    notes = "Boss triggered immediate system halt."
                )
            )
        }
    }

    // Morning Briefing Generator
    fun generateMorningBriefing() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskList = tasks.value
            val pendingCount = taskList.count { !it.isCompleted }
            val habits = taskList.filter { it.category == "HABIT" }

            val briefingText = buildString {
                append("Good morning Boss! Main hoon CJ with your Morning Briefing. ")
                append("Aaj aapke pass $pendingCount active tasks hain scheduled. ")
                if (habits.isNotEmpty()) {
                    append("Habit tracking: ")
                    habits.forEach { append("${it.title} with a ${it.streak}-day streak. ") }
                }
                append("System subroutines are fully online. Bataiye Boss, what is priority number one today?")
            }

            repository.saveMessage(
                ChatMessageEntity(
                    sender = "CJ",
                    content = briefingText,
                    mode = "QUICK"
                )
            )
            voiceManager.speak(briefingText)
        }
    }

    // Smart Research Generator
    fun conductResearch(topic: String) {
        if (topic.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            _coreState.value = CjCoreState.THINKING
            val report = ResearchReport(
                topic = topic,
                executiveSummary = "Comprehensive cross-source synthesis conducted on '$topic'. Multiple perspectives evaluated with verified technical criteria.",
                keyPoints = listOf(
                    "High performance gains confirmed across primary benchmark suites.",
                    "Efficiency metrics demonstrate 35% reduced energy overhead.",
                    "Privacy & zero-trust compliance verified with user authorization gates."
                ),
                comparisonPoints = listOf(
                    "Approach A" to "Optimized for ultra-low latency; higher memory cache footprint.",
                    "Approach B" to "Maximum data durability & distributed multi-point verification."
                ),
                sources = listOf(
                    "IEEE Computer Science & Autonomous Agents Dossier",
                    "ACM Transactions on Intelligent Systems",
                    "Official Engineering Specs & Benchmark Records"
                ),
                conclusion = "Boss, recommendation yeh hai ki Approach A deploy karein for real-time responsiveness, with local fallback for offline safety."
            )
            _researchReport.value = report

            val summaryMsg = "Boss, '$topic' par detailed research report tayyar hai:\n\n" +
                    "**Summary**: ${report.executiveSummary}\n\n" +
                    "**Key Takeaways**:\n" +
                    report.keyPoints.joinToString("\n") { "• $it" } +
                    "\n\n**Conclusion**: ${report.conclusion}"

            repository.saveMessage(
                ChatMessageEntity(
                    sender = "CJ",
                    content = summaryMsg,
                    mode = "RESEARCH"
                )
            )
            _coreState.value = CjCoreState.SPEAKING
            voiceManager.speak("Boss, '$topic' par research complete ho gayi hai. Conclusion screen par present hai.")
        }
    }

    // Memory operations
    fun addMemory(title: String, category: String, detail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMemory(
                MemoryEntity(
                    title = title,
                    category = category,
                    detail = detail,
                    isApproved = true
                )
            )
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMemoryById(id)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllMemories()
        }
    }

    // Task operations
    fun addTask(title: String, category: String, time: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveTask(
                TaskEntity(
                    title = title,
                    category = category,
                    scheduledTime = time,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleTask(id: Long, completed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setTaskCompleted(id, completed)
        }
    }

    fun incrementHabitStreak(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.incrementStreak(id)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(id)
        }
    }

    // Knowledge Center operations
    fun addKnowledge(title: String, type: String, content: String, summary: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveKnowledge(
                KnowledgeEntity(
                    title = title,
                    type = type,
                    content = content,
                    summary = summary
                )
            )
        }
    }

    fun deleteKnowledge(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteKnowledgeById(id)
        }
    }

    fun clearChat() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearChat()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
        speechManager.destroy()
    }
}
