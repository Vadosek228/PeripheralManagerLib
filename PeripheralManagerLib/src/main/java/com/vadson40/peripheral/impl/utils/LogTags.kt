package com.vadson40.peripheral.impl.utils

import android.util.Log

private const val TAG_COMMON = "LIB"
internal const val TAG_PERIPHERAL = "$TAG_COMMON:PERIPHERAL"

/**
 * Custom log, analog Timber.
 */
internal object Log {

    data class Tag(
        val tag: String
    )

    fun tag(tag: String): Tag = Tag(tag)

    fun Tag.i(msg: String) {
        Log.i(tag, msg)
    }

    fun Tag.d(msg: String) {
        Log.d(tag, msg)
    }

    fun Tag.w(msg: String) {
        Log.w(tag, msg)
    }

    fun Tag.e(msg: String) {
        Log.e(tag, msg)
    }
}