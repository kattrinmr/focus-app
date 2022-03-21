package com.katerina.focusapp

import android.content.Context

class UserStatistics(context: Context) {

    private val prefs = context.getSharedPreferences("stats", 0)

    fun increment() {
        val current = prefs.getInt(COUNTER_KEY, 0)
        prefs.edit()
            .putInt(COUNTER_KEY, current + 1)
            .apply()
    }

    fun reset() {
        prefs.edit()
            .remove(COUNTER_KEY)
            .apply()
    }

    fun getCounter(): Int = prefs.getInt(COUNTER_KEY, 0)

    companion object {
        private const val COUNTER_KEY = "counter"
    }
}