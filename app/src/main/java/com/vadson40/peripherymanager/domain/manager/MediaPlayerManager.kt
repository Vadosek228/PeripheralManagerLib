package com.vadson40.peripherymanager.domain.manager

import kotlinx.coroutines.flow.StateFlow

/**
 * @author Akulinin Vladislav
 * @since 13.03.2026
 */
interface MediaPlayerManager {

    val playerState: StateFlow<Boolean>

    fun play()

    fun stop()

}