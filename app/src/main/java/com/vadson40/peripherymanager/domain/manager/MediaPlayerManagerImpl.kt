package com.vadson40.peripherymanager.domain.manager

import android.content.Context
import android.media.MediaPlayer
import com.vadson40.peripherymanager.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * @author Akulinin Vladislav
 * @since 13.03.2026
 */
internal class MediaPlayerManagerImpl(
    val context: Context
) : MediaPlayerManager {

    private var mediaPlayer: MediaPlayer? = null

    private val _playerState = MutableStateFlow(false)
    override val playerState = _playerState.asStateFlow()

    private val onCompletionCallback = MediaPlayer.OnCompletionListener {
        stop()
    }

    init {
        mediaPlayer = createMediaPlayer()
        mediaPlayer?.setOnCompletionListener(onCompletionCallback)
    }

    override fun play() {
        // Stop previous sound
        stop()

        // Check and create
        if (mediaPlayer == null) {
            mediaPlayer = createMediaPlayer()
        }

        // Play new
        mediaPlayer?.start()
        _playerState.update { true }
    }

    override fun stop() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            _playerState.update { false }
        }
    }

    private fun createMediaPlayer(): MediaPlayer = MediaPlayer.create(context, R.raw.test_sound)
}