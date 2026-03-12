package com.vadson40.peripheral.api

import com.vadson40.peripheral.api.model.PeripheralRequest
import com.vadson40.peripheral.api.model.PeripheralResult
import com.vadson40.peripheral.api.model.PeripheralState
import com.vadson40.peripheral.api.model.PermissionRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * A manager for controlling the device's peripherals
 */
interface PeripheralManager {

    /**
     * Requests to change peripheral state
     */
    val permissionRequest: Flow<PermissionRequest>

    /**
     * Current state of peripherals.
     */
    val peripheralState: StateFlow<PeripheralState>

    /**
     * Request to change audio input/output device.
     *
     * @param change request to modify
     */
    suspend fun <R : PeripheralResult> request(change: PeripheralRequest<R>): R

}