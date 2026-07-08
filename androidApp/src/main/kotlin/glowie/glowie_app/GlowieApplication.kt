package glowie.glowie_app

import android.app.Application
import kotlinx.coroutines.GlobalScope

class GlowieApplication : Application() {
    private val appScope = GlobalScope

    val appContainer = AppContainer(appScope)
    val headlessApp = HeadlessApp(appContainer, appScope)

    override fun onCreate() {
        super.onCreate()
        headlessApp.start()
    }
}
