package com.example.intervalalarm.model.module.timer

import android.os.CountDownTimer
import android.util.Log

class CurrentAlarmTimer (var totalTime: Long = 1000, val onTicked:(Long)->Unit, val onFinished: () -> Unit): CountDownTimer(totalTime, 1000) {
    override fun onTick(millisUntilFinished: Long) {
        onTicked(millisUntilFinished)
        Log.d("main timer test", "tick from instance")
    }

    override fun onFinish() {
        onFinished()
        cancel()
        Log.d("main timer test", "vm timer from its instance")
    }
}