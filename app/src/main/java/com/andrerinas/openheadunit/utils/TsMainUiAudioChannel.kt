package com.andrerinas.openheadunit.utils

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

/**
 * TopWay units (`com.ts.MainUI`) route their speakers per "work mode". Opening the stock car screen
 * (factory OEM audio, work mode 12) moves the speakers to the car's own source, and MainUI only
 * moves them back when one of its own apps comes forward, so Open Headunit plays into nothing.
 *
 * MainUI listens for `forfan.intent.action.CLOSEMEDIA`: it sets work mode 0 (Android audio), and
 * with `mode` = 1 it stays where it is rather than going to its launcher. The extra is read with
 * getIntExtra, so it has to be an int.
 */
object TsMainUiAudioChannel {

    const val MAINUI_PACKAGE = "com.ts.MainUI"
    private const val ACTION_CLOSE_MEDIA = "forfan.intent.action.CLOSEMEDIA"
    private const val EXTRA_MODE = "mode"
    private const val MODE_STAY = 1
    /** MainUI can still be finishing its own screen when the projection resumes; ask once more. */
    private const val RETRY_DELAY_MS = 1000L

    private var present: Boolean? = null
    private val handler = Handler(Looper.getMainLooper())

    fun isPresent(context: Context): Boolean = present ?: runCatching {
        context.packageManager.getPackageInfo(MAINUI_PACKAGE, 0)
    }.isSuccess.also { present = it }

    /** Asks MainUI to hand the speakers back to Android. Does nothing on other units. */
    fun restore(context: Context, reason: String) {
        if (!isPresent(context)) return
        val app = context.applicationContext
        send(app)
        handler.postDelayed({ send(app) }, RETRY_DELAY_MS)
        AppLog.i("TsMainUiAudioChannel: asked $MAINUI_PACKAGE for Android audio ($reason)")
    }

    private fun send(context: Context) {
        runCatching {
            context.sendBroadcast(Intent(ACTION_CLOSE_MEDIA).putExtra(EXTRA_MODE, MODE_STAY))
        }.onFailure { AppLog.w("TsMainUiAudioChannel: broadcast failed: ${it.message}") }
    }
}
