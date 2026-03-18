package com.vadson40.peripherymanager

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

/**
 * @author Akulinin Vladislav
 * @since 30.01.2026
 */
class MediaPlayerMy(
    val context: Context
) {
    var mediaPlayer: MediaPlayer? = null

    fun playTestSound() {
        // Останавливаем текущий звук, если есть
        stopSound()

        mediaPlayer = MediaPlayer.create(context, R.raw.test_sound)
        mediaPlayer?.setOnCompletionListener { stopSound() }
        mediaPlayer?.start()
        Log.d("AUDIO", "Test sound started")
    }

    fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}