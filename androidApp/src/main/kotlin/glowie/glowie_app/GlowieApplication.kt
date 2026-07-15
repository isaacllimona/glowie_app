package glowie.glowie_app

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GlowieApplication : Application() {
    val appContainer = AppContainer()

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            appContainer.headlessApp()
        }
    }
}
