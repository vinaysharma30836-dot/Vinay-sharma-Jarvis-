package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SettingsVoice
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.CoreDashboardScreen
import com.example.ui.screens.DailyManagerScreen
import com.example.ui.screens.MemoryKnowledgeScreen
import com.example.ui.screens.VisionResearchScreen
import com.example.ui.screens.VoiceSettingsScreen
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HolographicCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack

enum class CjNavDestination(val label: String, val icon: ImageVector, val tag: String) {
    CORE("Core", Icons.Default.SmartToy, "nav_core"),
    AUTOMATION("Auto", Icons.Default.Hub, "nav_automation"),
    VISION("Vision", Icons.Default.Visibility, "nav_vision"),
    DAILY("Daily", Icons.Default.CalendarMonth, "nav_daily"),
    MEMORY("Memory", Icons.Default.VpnKey, "nav_memory"),
    SETTINGS("Voice", Icons.Default.SettingsVoice, "nav_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val mainViewModel: MainViewModel = viewModel()
                CjAppRoot(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun CjAppRoot(viewModel: MainViewModel) {
    var currentDestination by remember { mutableStateOf(CjNavDestination.CORE) }

    // BackHandler: return to Core Dashboard when pressing back on sub-screens
    BackHandler(enabled = currentDestination != CjNavDestination.CORE) {
        currentDestination = CjNavDestination.CORE
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .border(0.5.dp, CyberBorder)
                    .testTag("cj_bottom_nav"),
                containerColor = CyberSurface,
                tonalElevation = 8.dp
            ) {
                CjNavDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VoidBlack,
                            selectedTextColor = HolographicCyan,
                            indicatorColor = ElectricBlue,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag(destination.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                CjNavDestination.CORE -> CoreDashboardScreen(viewModel = viewModel)
                CjNavDestination.AUTOMATION -> AutomationScreen(viewModel = viewModel)
                CjNavDestination.VISION -> VisionResearchScreen(viewModel = viewModel)
                CjNavDestination.DAILY -> DailyManagerScreen(viewModel = viewModel)
                CjNavDestination.MEMORY -> MemoryKnowledgeScreen(viewModel = viewModel)
                CjNavDestination.SETTINGS -> VoiceSettingsScreen(viewModel = viewModel)
            }
        }
    }
}
