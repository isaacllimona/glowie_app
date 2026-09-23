package glowie.glowie_app.core.devices.control.syncClock

import glowie.glowie_app.core.devices.management.DeviceManager
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val sharedClockBroadcastPeriod = 2000.milliseconds

class SyncClockBroadcaster (
    private val deviceManager: DeviceManager,
    private val syncClock: SyncClock = SyncClock(),
){
    suspend fun run() {
        while (true) {
            deviceManager.applyToAll { setTime(syncClock.getTime()) }
            delay(sharedClockBroadcastPeriod)
        }
    }
}