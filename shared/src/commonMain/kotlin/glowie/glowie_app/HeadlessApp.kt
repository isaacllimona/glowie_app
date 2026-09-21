package glowie.glowie_app

import glowie.glowie_app.control.domain.ControlDispatcher
import glowie.glowie_app.devices.domain.DeviceManager
import glowie.glowie_app.devices.domain.DeviceWatcher
import glowie.glowie_app.devices.domain.GlowieDiscovery
import glowie.glowie_app.scripts.domain.ImportBuiltinScripts
import glowie.glowie_app.syncClock.domain.SyncClockBroadcaster
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HeadlessApp (
    private val glowieDiscovery: GlowieDiscovery,
    private val deviceWatcher: DeviceWatcher,
    private val controlDispatcher: ControlDispatcher,
    private val syncClockBroadcaster: SyncClockBroadcaster,
    private val deviceManager: DeviceManager,
    private val importBuiltinScripts: ImportBuiltinScripts,
) {
    suspend operator fun invoke() : Nothing = coroutineScope {
        try {
            launch { importBuiltinScripts() }
            launch { controlDispatcher.run() }
            launch { syncClockBroadcaster.run() }
            launch { deviceWatcher.run() }
            launch { glowieDiscovery.run() }
            awaitCancellation()
        }
        finally {
            withContext(NonCancellable) {
                deviceManager.applyToAll { disconnect() }
            }
        }
    }
}
