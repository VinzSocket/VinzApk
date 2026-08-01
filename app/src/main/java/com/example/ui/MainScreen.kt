package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.border

@Composable
fun MainScreen(viewModel: MainViewModel, onLogout: () -> Unit) {
    var currentTab by remember { mutableStateOf(0) }
    val uiState by viewModel.uiState.collectAsState()
    val isPlaying by AudioPlayer.isPlaying.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        TimeBasedBackground()
        
        // Content overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF02040A).copy(alpha = 0.85f))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MELOFY",
                    color = Color(0xFF38BDF8),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                when (currentTab) {
                    0 -> DashboardScreen(viewModel, uiState)
                    1 -> SearchScreen(viewModel, uiState)
                    2 -> CollectionScreen(viewModel, uiState)
                    3 -> PremiumScreen(viewModel, uiState)
                    4 -> AccountScreen(viewModel, uiState, onLogout)
                }
            }
            
            // Spacer for mini player and bottom nav
            Spacer(modifier = Modifier.height(160.dp))
        }

        // Mini Player
        if (uiState.currentSongTitle != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 95.dp)
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder for cover
                    Box(modifier = Modifier.size(44.dp).background(Color.Gray, RoundedCornerShape(12.dp)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(uiState.currentSongTitle ?: "Loading...", color = Color.White, fontSize = 14.sp, maxLines = 1)
                        Text(uiState.currentSongArtist ?: "...", color = Color.LightGray, fontSize = 12.sp, maxLines = 1)
                    }
                    TextButton(onClick = { viewModel.togglePlay() }) {
                        Text(if (isPlaying) "Pause" else "Play", color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                }
            }
        }

        // Bottom Nav
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(15.dp)
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0A0F1A).copy(alpha = 0.88f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItem(Icons.Default.Home, "Home", currentTab == 0) { currentTab = 0 }
                NavBarItem(Icons.Default.Search, "Cari", currentTab == 1) { currentTab = 1 }
                NavBarItem(Icons.Default.Favorite, "Koleksi", currentTab == 2) { currentTab = 2 }
                NavBarItem(Icons.Default.Star, "Premium", currentTab == 3) { currentTab = 3 }
                NavBarItem(Icons.Default.Person, "Akun", currentTab == 4) { currentTab = 4 }
            }
        }
        
        uiState.toastMessage?.let { msg ->
            Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp).background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp)).padding(16.dp)) {
                Text(msg, color = Color.White)
            }
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(2000)
                viewModel.clearToast()
            }
        }
    }
}

@Composable
fun NavBarItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label, tint = if (isSelected) Color.White else Color.Gray)
            if (isSelected) {
                Text(label, color = Color.White, fontSize = 10.sp)
            }
        }
    }
}
