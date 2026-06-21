package glowie.glowie_app

class HeadlessApp (
    private val appContainer: AppContainer,
) {
    fun start() {
         appContainer.deviceManager.startGlowieDiscovery()
    }

    suspend fun exit() {
        appContainer.deviceManager.stopGlowieDiscoveryAndJoin()
        appContainer.deviceManager.applyToAll { disconnect() }.join()
    }
}
