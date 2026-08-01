package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(viewModel: MainViewModel, uiState: UiState) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF38BDF8).copy(alpha = 0.1f))
                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(15.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Limit Harian Anda:", color = Color.LightGray, fontSize = 13.sp)
                Text(if (uiState.plan == "flagship") "UNLIMITED (FLAGSHIP)" else "${uiState.dailyLimit} / ${viewModel.getLimitMax(uiState.plan)}", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Platform Tersedia", color = Color(0xFF38BDF8), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(10.dp))
        PlatformButton("YouTube Downloader", Color(0xFFEF4444))
        Spacer(modifier = Modifier.height(10.dp))
        PlatformButton("Instagram Downloader", Color(0xFFE1306C))
        Spacer(modifier = Modifier.height(10.dp))
        PlatformButton("TikTok Downloader", Color(0xFF00F2FE))
        Spacer(modifier = Modifier.height(20.dp))
        Text("Baru Diputar", color = Color(0xFF38BDF8), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(10.dp))
        if (uiState.currentSongTitle != null) {
            SongCard(
                title = uiState.currentSongTitle ?: "",
                artist = uiState.currentSongArtist ?: "",
                cover = uiState.currentSongCover ?: "",
                onClick = {}
            )
        } else {
            Text("Belum ada riwayat putar.", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun PlatformButton(title: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(24.dp).background(color, RoundedCornerShape(12.dp)))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MainViewModel, uiState: UiState) {
    var query by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Cari Musik", color = Color(0xFF38BDF8), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Judul lagu / Artis...", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
                    focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = { if (query.isNotBlank()) viewModel.searchSpotify(query) }) {
                Text("Cari")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        
        if (uiState.isSearching) {
            CircularProgressIndicator(color = Color(0xFF38BDF8), modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.searchResults) { item ->
                    SongCard(
                        title = item.title,
                        artist = item.artist ?: item.author ?: "Unknown",
                        cover = item.image ?: item.thumbnail ?: "",
                        onClick = { viewModel.playSong(item.url, item.title, item.artist ?: "", item.image ?: "") }
                    )
                }
            }
        }
    }
}

@Composable
fun CollectionScreen(viewModel: MainViewModel, uiState: UiState) {
    val collection by viewModel.collection.collectAsState(initial = emptyList())
    Column {
        Text("Koleksi Tersimpan", color = Color(0xFF38BDF8), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(collection) { item ->
                SongCard(
                    title = item.title,
                    artist = item.artist,
                    cover = item.coverUrl,
                    onClick = { viewModel.playSong(item.originalUrl, item.title, item.artist, item.coverUrl) }
                )
            }
        }
    }
}

@Composable
fun PremiumScreen(viewModel: MainViewModel, uiState: UiState) {
    Column {
        Text("Melofy Premium", color = Color(0xFF38BDF8), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text("Dengarkan musik tanpa batas, kualitas ultra-HD.", color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(20.dp))
        
        PlanCard("Paket Pelajar", "Rp 18.000 / bulan", "Limit 200 unduhan", Color(0xFF38BDF8))
        Spacer(modifier = Modifier.height(10.dp))
        PlanCard("Paket Pro", "Rp 25.000 / bulan", "Limit 500 unduhan", Color(0xFFF59E0B))
        Spacer(modifier = Modifier.height(10.dp))
        PlanCard("Flagship Annual", "Rp 380.000 / tahun", "Unlimited Limit Harian", Color(0xFFFBBF24))
    }
}

@Composable
fun PlanCard(title: String, price: String, desc: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(price, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(desc, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(viewModel: MainViewModel, uiState: UiState, onLogout: () -> Unit) {
    var redeemCode by remember { mutableStateOf("") }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Profile Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(90.dp).background(Color.Gray, RoundedCornerShape(45.dp)))
                Spacer(modifier = Modifier.height(10.dp))
                Text(uiState.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Redeem Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(20.dp)
        ) {
            Column {
                Text("Tukar Kode Redeem", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = redeemCode,
                    onValueChange = { redeemCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = Color.White, focusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { viewModel.redeemCode(redeemCode) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Klaim Kode")
                }
            }
        }
        
        // Latency Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(20.dp)
        ) {
            var latencies by remember { mutableStateOf(mapOf<String, String>("YouTube API" to "--", "TikTok API" to "--", "Spotify API" to "--")) }
            val coroutineScope = rememberCoroutineScope()
            Column {
                Text("Uji Latency API", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                latencies.forEach { (api, ms) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(api, color = Color.White, fontSize = 13.sp)
                        Text("$ms ms", color = if (ms == "--") Color.Gray else Color(0xFF4ADE80), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val apis = listOf(
                                "YouTube API" to "https://api.neoxr.eu/api/youtube?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DfKRtnMYMW08&type=video&quality=1080p&apikey=${com.example.BuildConfig.NEOXR_API_KEY}",
                                "TikTok API" to "https://api.neoxr.eu/api/tiktok?url=https%3A%2F%2Fwww.tiktok.com%2F%40nikenandalusia%2Fvideo%2F7480894024082050309%3Fis_from_webapp%3D1%26sender_device%3Dpc%26web_id%3D7482388089839765012&apikey=${com.example.BuildConfig.NEOXR_API_KEY}",
                                "Spotify API" to "https://api.neoxr.eu/api/spotify-search?q=test&apikey=${com.example.BuildConfig.NEOXR_API_KEY}"
                            )
                            latencies = apis.associate { it.first to "..." }
                            apis.forEach { (name, url) ->
                                val start = System.currentTimeMillis()
                                try {
                                    val request = okhttp3.Request.Builder().url(url).build()
                                    okhttp3.OkHttpClient().newCall(request).execute().use {
                                        val time = System.currentTimeMillis() - start
                                        latencies = latencies.toMutableMap().apply { put(name, time.toString()) }
                                    }
                                } catch (e: Exception) {
                                    latencies = latencies.toMutableMap().apply { put(name, "Error") }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA78BFA).copy(alpha = 0.2f))
                ) {
                    Text("JALANKAN UJI LATENCY", color = Color(0xFFA78BFA))
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.3f)), modifier = Modifier.fillMaxWidth()) {
            Text("Keluar Akun", color = Color.Red)
        }
    }
}

@Composable
fun SongCard(title: String, artist: String, cover: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cover,
            contentDescription = null,
            modifier = Modifier.size(55.dp).clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(title, color = Color.White, fontSize = 15.sp, maxLines = 1)
            Text(artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
        }
    }
}
