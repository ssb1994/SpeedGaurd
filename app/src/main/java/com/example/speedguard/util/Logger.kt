package com.example.speedguard.util

import android.util.Log

object Logger {

    enum class Level {
        VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, NONE
    }

   private var isEnabled: Boolean = true
   private var minLogLevel: Level = Level.DEBUG

    fun v(tag: String, message: String) = log(Level.VERBOSE, tag, message)
    fun d(tag: String, message: String) = log(Level.DEBUG, tag, message)
    fun i(tag: String, message: String) = log(Level.INFO, tag, message)
    fun w(tag: String, message: String) = log(Level.WARN, tag, message)
    fun e(tag: String, message: String, throwable: Throwable? = null) =
        log(Level.ERROR, tag, message, throwable)

    private fun log(level: Level, tag: String, message: String, throwable: Throwable? = null) {
        if (!isEnabled || level.ordinal < minLogLevel.ordinal) return

        when (level) {
            Level.VERBOSE -> Log.v(tag, message)
            Level.DEBUG -> Log.d(tag, message)
            Level.INFO -> Log.i(tag, message)
            Level.WARN -> Log.w(tag, message)
            Level.ERROR -> Log.e(tag, message, throwable)
            Level.ASSERT -> Log.wtf(tag, message)
            else -> {}
        }
    }

    inline fun <reified T> T.logd(message: String) {
        d(T::class.java.simpleName, message)
    }

    inline fun <reified T> T.loge(message: String, throwable: Throwable? = null) {
        e(T::class.java.simpleName, message, throwable)
    }

}