package fr.robar.android

import android.app.Application
import fr.robar.android.robarBLE.RobarDevice

class RobarApplication : Application() {
    var robarDevice: RobarDevice? = null

    fun sendBytes(array: ByteArray): Boolean {
        // Retrieve the characteristic
        val characteristic = robarDevice
            ?.gattDevice
            ?.getService(RobarDevice.SERVICE_UUID)
            ?.characteristics?.first()
            ?: return false // Or exit
        characteristic.value = array
        val written = robarDevice?.gattDevice?.writeCharacteristic(characteristic) // Try to send the data
        if (written != true) return false
        return true
    }
}