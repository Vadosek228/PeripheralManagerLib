package com.vadson40.peripheral.api.model

/**
 * Result of operation for switching peripherals.
 */
sealed interface PeripheralResult {
    /**
     * Result of switching audio output
     */
    sealed interface AudioOutput : PeripheralResult {
        /**
         * Completed successfully
         */
        companion object Success : AudioOutput

        /**
         * An error occurred
         */
        data object Error : AudioOutput
    }


    /**
     * Result of switching the microphone
     */
    sealed interface Microphone : PeripheralResult {
        /**
         * Completed successfully
         */
        companion object Success : Microphone

        /**
         * It is necessary to request permission to use a mic
         */
        data class PermissionRequired(val permission: String): Microphone

        /**
         * An error occurred
         */
        data object Error : Microphone
    }
}