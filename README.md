# Phone number input panel

An easily configurable library for managing device peripherals. 
With this library, you can effortlessly switch between the earpiece, loudspeaker, Bluetooth, or wired headphones.
This library is designed for media player applications or custom calling apps.

[![](https://jitpack.io/v/Vadosek228/PeripheralManagerLib.svg)](https://jitpack.io/#Vadosek228/PeripheralManagerLib)

The library offers features such as:
- switching audio output devices;
- enabling or disabling the microphone;
- setting the volume level;
- checking user permissions.

<img width="200" height="400" alt="Screenshot_white" src="https://github.com/Vadosek228/PeripheralManagerLib/blob/master/IMAGE%202026-03-25%2012%3A42%3A36.jpg" />
<img width="200" height="400" alt="Screenshot_black" src="https://github.com/Vadosek228/PeripheralManagerLib/blob/master/IMAGE%202026-03-25%2012%3A42%3A37.jpg" />

## Sample project
Familiarize yourself with the test implementation of the [library](https://github.com/Vadosek228/PeripheralManagerLib/tree/master/app).
You can also explore the [PhoneDialPadScreen](https://github.com/Vadosek228/PeripheralManagerLib/tree/master/PeripheralManagerLib) component and see what parameters it accepts for possible customization,
as well as pay attention to the state objects it handles. Documentation is provided for each class.

## Setup

    dependencies {
        // Peripheral Manager Lib
        implementation("com.github.Vadosek228:PeripheralManagerLib:<latest-version>")
    }

## Use

    peripheralManager.peripheralState
        .onEach { result ->
            // Handle result
        }
        .launchIn(viewModelScope)

    peripheralManager.permissionRequest
        .onEach {
        // Permission request
        }
        .launchIn(viewModelScope)

    // Select device
    viewModelScope.launch {
        runCatching {
            peripheralManager.request(PeripheralRequest.AudioOutput(deviceVO.deviceId))
        }.onSuccess {
            // Success
        }.onFailure {
            // Error
        }
    }
