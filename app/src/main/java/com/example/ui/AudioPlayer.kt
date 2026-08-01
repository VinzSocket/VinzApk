package com.example.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun play(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            setOnPreparedListener { 
                it.start() 
                _isPlaying.update { true }
            }
            setOnCompletionListener {
                _isPlaying.update { false }
            }
            prepareAsync() // might take long! (for buffering, etc)
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.update { false }
    }
    
    fun resume() {
        mediaPlayer?.start()
        _isPlaying.update { true }
    }
    
    fun toggle() {
        if (_isPlaying.value) pause() else resume()
    }
}
