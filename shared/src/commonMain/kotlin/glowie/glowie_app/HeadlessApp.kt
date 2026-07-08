package glowie.glowie_app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HeadlessApp (
    private val appContainer: AppContainer,
    private val headlessScope: CoroutineScope
) {
    fun start() {
        appContainer.deviceManager.startGlowieDiscovery()
        headlessScope.launch { appContainer.syncClockBroadcaster.runPeriodicBroadcaster() }
    }

    suspend fun exit() {
        appContainer.deviceManager.stopGlowieDiscoveryAndJoin()
        appContainer.deviceManager.applyToAll { disconnect() }.join()
    }
}
