package glowie.glowie_app

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GlowieApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        AppContextProvider.init(this)
        appContainer = AppContainer()

        GlobalScope.launch {
            appContainer.headlessApp()
        }
    }
}
