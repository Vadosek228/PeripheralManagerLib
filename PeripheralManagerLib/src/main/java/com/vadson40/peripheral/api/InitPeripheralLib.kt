package com.vadson40.peripheral.api

import android.content.Context
import com.vadson40.peripheral.impl.PeripheralManagerFactory
import com.vadson40.peripheral.impl.permission.PermissionManagerImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * @author Akulinin Vladislav
 * @since 12.03.2026
 */
object InitPeripheralLib {

    private lateinit var _peripheralManager: PeripheralManager

    val peripheralManager: PeripheralManager
        get() {
            if (!::_peripheralManager.isInitialized) throw PeripheralManagerInitException()
            return _peripheralManager
        }

    fun init(
        context: Context,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
    ): PeripheralManager {
        val permissionManager = PermissionManagerImpl(context)
        val peripheralManager = PeripheralManagerFactory.create(context, coroutineDispatcher, permissionManager)
        _peripheralManager = peripheralManager
        return peripheralManager
    }
}

/**
 * Error if [Peripheral Manager] is not initialized via init.
 */
class PeripheralManagerInitException(message: String = "The PeripheralManager was not initialized via InitPeripheralLib using init()"): Exception(message)