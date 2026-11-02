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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.KnowledgeEntity
import com.example.data.local.MemoryEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderGradient
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.HolographicCyan
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack

@Composable
fun MemoryKnowledgeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.memories.collectAsStateWithLifecycle()
    val knowledgeItems by viewModel.knowledgeItems.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("MEMORY") } // MEMORY or KNOWLEDGE
    var showAddMemoryDialog by remember { mutableStateOf(false) }
    var showAddKnowledgeDialog by remember { mutableStateOf(false) }

    // Memory form
    var memoryTitle by remember { mutableStateOf("") }
    var memoryCategory by remember { mutableStateOf("PREFERENCE") }
    var memoryDetail by remember { mutableStateOf("") }

    // Knowledge form
    var knowledgeTitle by remember { mutableStateOf("") }
    var knowledgeContent by remember { mutableStateOf("") }
    var knowledgeSummary by remember { mutableStateOf("") }
    var knowledgeSearchQuery by remember { mutableStateOf("") }

    val filteredKnowledge = remember(knowledgeItems, knowledgeSearchQuery) {
        if (knowledgeSearchQuery.isBlank()) knowledgeItems
        else knowledgeItems.filter {
            it.title.contains(knowledgeSearchQuery, true) || it.content.contains(knowledgeSearchQuery, true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(16.dp)
    ) {
        // Tab Selector: Personal Memory vs Knowledge Center
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface, shape = RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (activeTab == "MEMORY") ElectricBlue else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { activeTab = "MEMORY" }
                    .padding(vertical = 8.dp)
                    .testTag("tab_personal_memory"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (activeTab == "MEMORY") VoidBlack else TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Memory Vault (${memories.size})",
                        color = if (activeTab == "MEMORY") VoidBlack else TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (activeTab == "KNOWLEDGE") ElectricBlue else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { activeTab = "KNOWLEDGE" }
                    .padding(vertical = 8.dp)
                    .testTag("tab_knowledge_center"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = if (activeTab == "KNOWLEDGE") VoidBlack else TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Knowledge Center (${knowledgeItems.size})",
                        color = if (activeTab == "KNOWLEDGE") VoidBlack else TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == "MEMORY") {
            // Memory Vault
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SECURE LONG-TERM MEMORY",
                        color = HolographicCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Only memories explicitly approved by Boss are retained.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showAddMemoryDialog = !showAddMemoryDialog },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_memory_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showAddMemoryDialog) "Close" else "Add Memory", fontSize = 11.sp, color = TextWhite)
                }
            }

            if (showAddMemoryDialog) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = memoryTitle,
                            onValueChange = { memoryTitle = it },
                            label = { Text("Memory Key / Title") },
                            placeholder = { Text("e.g. Favorite Language") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("memory_title_input"),
                            colors = outlineColors()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = memoryDetail,
                            onValueChange = { memoryDetail = it },
                            label = { Text("Detail / Preference") },
                            placeholder = { Text("Boss prefers Kotlin and Python") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("memory_detail_input"),
                            colors = outlineColors()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (memoryTitle.isNotBlank() && memoryDetail.isNotBlank()) {
                                    viewModel.addMemory(memoryTitle, memoryCategory, memoryDetail)
                                    memoryTitle = ""
                                    memoryDetail = ""
                                    showAddMemoryDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = VoidBlack),
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("save_memory_button")
                        ) {
                            Text("Store in Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(memories, key = { it.id }) { memory ->
                    MemoryCardItem(
                        memory = memory,
                        onDelete = { viewModel.deleteMemory(memory.id) }
                    )
                }
            }
        } else {
            // Knowledge Center
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FEATURE E: PRIVATE KNOWLEDGE VAULT",
                        color = NeonAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Store manuals, research notes, and code snippets.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showAddKnowledgeDialog = !showAddKnowledgeDialog },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_knowledge_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showAddKnowledgeDialog) "Close" else "Add Doc", fontSize = 11.sp, color = TextWhite)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = knowledgeSearchQuery,
                onValueChange = { knowledgeSearchQuery = it },
                label = { Text("Search Vault Documents") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = HolographicCyan) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("knowledge_search_input"),
                colors = outlineColors()
            )

            if (showAddKnowledgeDialog) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = knowledgeTitle,
                            onValueChange = { knowledgeTitle = it },
                            label = { Text("Document / Snippet Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlineColors()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = knowledgeContent,
                            onValueChange = { knowledgeContent = it },
                            label = { Text("Content / Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlineColors(),
                            maxLines = 5
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = knowledgeSummary,
                            onValueChange = { knowledgeSummary = it },
                            label = { Text("Quick Summary") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = outlineColors()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (knowledgeTitle.isNotBlank()) {
                                    viewModel.addKnowledge(knowledgeTitle, "DOCUMENT", knowledgeContent, knowledgeSummary)
                                    knowledgeTitle = ""
                                    knowledgeContent = ""
                                    knowledgeSummary = ""
                                    showAddKnowledgeDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = VoidBlack),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Save to Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredKnowledge, key = { it.id }) { item ->
                    KnowledgeCardItem(
                        item = item,
                        onAskCJ = {
                            viewModel.sendUserMessage("CJ, is document ko samjhao: '${item.title}' - ${item.content}")
                        },
                        onDelete = { viewModel.deleteKnowledge(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MemoryCardItem(
    memory: MemoryEntity,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface, shape = RoundedCornerShape(10.dp))
            .border(1.dp, CyberBorder, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
            .testTag("memory_item_${memory.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = memory.title,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(HolographicCyan.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = memory.category,
                        color = HolographicCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = memory.detail,
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Memory", tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun KnowledgeCardItem(
    item: KnowledgeEntity,
    onAskCJ: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            .testTag("knowledge_item_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
            if (item.summary.isNotBlank()) {
                Text(
                    text = item.summary,
                    color = HolographicCyan,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.content,
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAskCJ,
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Ask CJ about this", fontSize = 11.sp, color = ElectricBlue)
            }
        }
    }
}
