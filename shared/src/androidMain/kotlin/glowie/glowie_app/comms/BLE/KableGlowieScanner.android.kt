package glowie.glowie_app.comms.BLE

import com.juul.kable.PeripheralBuilder

actual fun PeripheralBuilder.platformPeripheralBuilder() {
    onServicesDiscovered {
        requestMtu(517)
    }
}