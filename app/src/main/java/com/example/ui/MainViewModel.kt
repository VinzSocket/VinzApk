package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.api.ApiClient
import com.example.db.AppDatabase
import com.example.db.CollectionItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("melofy_prefs", Context.MODE_PRIVATE)
    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "melofy-db").build()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    val collection: StateFlow<List<CollectionItem>> = db.collectionDao().getAllItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        checkDailyLimit()
        loadUser()
    }

    private fun loadUser() {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val name = prefs.getString("user_name", "") ?: ""
        val plan = prefs.getString("user_plan", "free") ?: "free"
        val limit = prefs.getInt("daily_limit", getLimitMax(plan))
        _uiState.update { it.copy(isLoggedIn = isLoggedIn, userName = name, plan = plan, dailyLimit = limit) }
    }

    private fun checkDailyLimit() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastReset = prefs.getString("last_reset", "")
        if (today != lastReset) {
            val plan = prefs.getString("user_plan", "free") ?: "free"
            prefs.edit()
                .putString("last_reset", today)
                .putInt("daily_limit", getLimitMax(plan))
                .apply()
        }
    }

    fun getLimitMax(plan: String): Int = when (plan) {
        "flagship" -> 999999
        "pro" -> 500
        "pelajar" -> 200
        else -> 10
    }

    fun login(username: String) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_name", username)
            .putString("user_plan", "free")
            .putInt("daily_limit", 10)
            .apply()
        loadUser()
    }
    
    fun logout() {
        prefs.edit().clear().apply()
        loadUser()
    }

    fun redeemCode(code: String) {
        val upperCode = code.uppercase()
        val plan = when (upperCode) {
            "MELOFYPELAJAR" -> {
                // Ensure 1 time per account
                if (prefs.getBoolean("redeemed_pelajar", false)) {
                    _uiState.update { it.copy(toastMessage = "Kode MELOFYPELAJAR sudah digunakan!") }
                    return
                }
                prefs.edit().putBoolean("redeemed_pelajar", true).apply()
                "pelajar"
            }
            "MELOFYPRO" -> "pro"
            "FLAGSHIPVIP" -> "flagship"
            else -> {
                _uiState.update { it.copy(toastMessage = "Kode redeem tidak valid.") }
                return
            }
        }
        
        prefs.edit()
            .putString("user_plan", plan)
            .putInt("daily_limit", getLimitMax(plan))
            .apply()
            
        loadUser()
        _uiState.update { it.copy(toastMessage = "Berhasil redeem kode!") }
    }
    
    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun searchSpotify(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            try {
                val response = ApiClient.neoxrApi.searchSpotify(query)
                _uiState.update { it.copy(searchResults = response.data ?: emptyList(), isSearching = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearching = false, toastMessage = "Gagal mencari: ${e.message}") }
            }
        }
    }

    fun playSong(url: String, title: String, artist: String, coverUrl: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(currentSongTitle = title, currentSongArtist = artist, currentSongCover = coverUrl, streamUrl = null) }
            try {
                val localItem = db.collectionDao().getItemByUrl(url)
                if (localItem != null) {
                    _uiState.update { it.copy(streamUrl = localItem.streamUrl) }
                    AudioPlayer.play(localItem.streamUrl)
                } else {
                    if (consumeLimit()) {
                        val response = ApiClient.botcahxApi.downloadSpotify(url)
                        val stream = response.result?.data?.url
                        if (stream != null) {
                            _uiState.update { it.copy(streamUrl = stream) }
                            AudioPlayer.play(stream)
                        } else {
                            _uiState.update { it.copy(toastMessage = "Gagal memuat lagu") }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(toastMessage = "Error: ${e.message}") }
            }
        }
    }
    
    fun togglePlay() {
        AudioPlayer.toggle()
    }
    
    fun toggleCollection(url: String, title: String, artist: String, coverUrl: String, streamUrl: String) {
        viewModelScope.launch {
            val existing = db.collectionDao().getItemByUrl(url)
            if (existing != null) {
                db.collectionDao().deleteItemByUrl(url)
                _uiState.update { it.copy(toastMessage = "Dihapus dari Koleksi") }
            } else {
                db.collectionDao().insertItem(
                    CollectionItem(
                        title = title,
                        artist = artist,
                        coverUrl = coverUrl,
                        streamUrl = streamUrl,
                        originalUrl = url
                    )
                )
                _uiState.update { it.copy(toastMessage = "Disimpan ke Koleksi") }
            }
        }
    }

    private fun consumeLimit(): Boolean {
        val currentLimit = prefs.getInt("daily_limit", 0)
        if (currentLimit <= 0) {
            _uiState.update { it.copy(toastMessage = "Limit Harian Habis!") }
            return false
        }
        val newLimit = currentLimit - 1
        prefs.edit().putInt("daily_limit", newLimit).apply()
        _uiState.update { it.copy(dailyLimit = newLimit) }
        return true
    }
}

data class UiState(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val plan: String = "free",
    val dailyLimit: Int = 10,
    val searchResults: List<com.example.api.SpotifySearchItem> = emptyList(),
    val isSearching: Boolean = false,
    val currentSongTitle: String? = null,
    val currentSongArtist: String? = null,
    val currentSongCover: String? = null,
    val streamUrl: String? = null,
    val isPlaying: Boolean = false,
    val toastMessage: String? = null
)
