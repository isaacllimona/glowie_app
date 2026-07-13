package glowie.glowie_app.comms.internal.BLE

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object UUIDs {
    object Services {
        val control = Uuid.parse("00000000-1fbd-0481-2a47-f3b657518ec1")
    }
    object Chars {
        val cmd_in = Uuid.parse("00000001-1fbd-0481-2a47-f3b657518ec1")
    }
}