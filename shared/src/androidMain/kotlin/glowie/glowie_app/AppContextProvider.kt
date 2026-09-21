package glowie.glowie_app

import android.content.Context

object AppContextProvider {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun get(): Context {
        return appContext ?: throw IllegalStateException("AppContextProvider not initialized")
    }
}