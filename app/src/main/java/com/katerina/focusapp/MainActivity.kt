package com.katerina.focusapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.katerina.focusapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var stats: UserStatistics
    private val handler = Handler(Looper.myLooper()!!)
    private var targetTime: Long = -1
    private var hasSaveState = false

    private val timerTick = object : Runnable {
        override fun run() {
            val now = SystemClock.elapsedRealtime()
            val diff = (targetTime - now) / 1000L
            if (diff >= 0L) {
                mBinding.timer.visibility = View.VISIBLE
                mBinding.timer.text = "Осталось ${diff.toLong()} минут(а)"
                handler.postDelayed(this, 500L)
            } else {
                onFocusSuccess()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        targetTime = savedInstanceState?.getLong(STATE_TARGET_TIME, -1L) ?: -1L
        if (targetTime > 0L) {
            hasSaveState = true
            handler.post(timerTick)
        }

        stats = UserStatistics(this)

        showStats()
        updateMinuteValue()

        mBinding.minuteSlider.addOnChangeListener { slider, value, fromUser ->
            updateMinuteValue()
        }

        mBinding.startButton.setOnClickListener {
            mBinding.minuteValue.visibility = View.INVISIBLE
            start() }
    }

    override fun onResume() {
        super.onResume()
        if (!hasSaveState) {
            if (targetTime > 0) {
                val now = SystemClock.elapsedRealtime()
                val diff = (targetTime - now) / 1000L
                if (diff > 0) {
                    onFocusFail()
                } else {
                    onFocusSuccess()
                }
            }
        }
        hasSaveState = false
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(timerTick)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(STATE_TARGET_TIME, targetTime)
    }

    private fun updateMinuteValue() {
        val minute = mBinding.minuteSlider.value
        mBinding.minuteValue.text = "$minute минут(а)"
    }

    private fun onFocusFail() {
        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
        stats.reset()
        showStats()
        targetTime = -1
        mBinding.timer.visibility = View.INVISIBLE
        mBinding.minuteValue.visibility = View.VISIBLE
    }

    private fun onFocusSuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        stats.increment()
        showStats()
        targetTime = -1
        mBinding.timer.visibility = View.INVISIBLE
        mBinding.minuteValue.visibility = View.VISIBLE
    }

    private fun showStats() {
        mBinding.stats.text = "👍 ${stats.getCounter()}"
    }

    private fun start() {
        targetTime = (SystemClock.elapsedRealtime() + (mBinding.minuteSlider.value) * 1000L).toLong()
        handler.post(timerTick)
    }

    private companion object {
        const val STATE_TARGET_TIME = "target_time"
    }
}