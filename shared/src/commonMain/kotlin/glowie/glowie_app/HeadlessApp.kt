package glowie.glowie_app

suspend fun headlessApp(appContainer: AppContainer) {
    try {
        appContainer.deviceManager.runGlowieDiscovery()
    }
    finally {
        appContainer.deviceManager.applyToAll { disconnect() }
    }
}