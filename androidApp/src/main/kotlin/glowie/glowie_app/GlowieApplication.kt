package glowie.glowie_app

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GlowieApplication : Application() {
    private val appScope = GlobalScope

    val appContainer = AppContainer(
        appScope = appScope,
    )

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            headlessApp(appContainer)
        }
    }
}
