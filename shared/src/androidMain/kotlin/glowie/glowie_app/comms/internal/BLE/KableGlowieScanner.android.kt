package glowie.glowie_app.comms.internal.BLE

import com.juul.kable.PeripheralBuilder

actual fun PeripheralBuilder.platformPeripheralBuilder() {
    onServicesDiscovered {
        requestMtu(517)
    }
}