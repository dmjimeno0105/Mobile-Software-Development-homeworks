package com.example.hw02_stopwatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Timer
import java.util.TimerTask

class StopwatchModel : ViewModel() {
    private val stopwatchTimeLive: MutableLiveData<Int> = MutableLiveData<Int>()
    private var stopwatchTimeLocal = 0
    private var timer = Timer()
    private var stopwatchCancelled = false
    private var startAlreadyClicked = false
    private val savedStopwatchTimesLocal: MutableList<Int> = mutableListOf()
    private val savedStopwatchTimesLive: MutableLiveData<MutableList<Int>> =
        MutableLiveData<MutableList<Int>>()

    fun startStopwatch() {
        // to resume the stopwatch, must reinitialize timer
        if (stopwatchCancelled) {
            timer = Timer()
        }
        if (!startAlreadyClicked) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    stopwatchTimeLocal++
                    stopwatchTimeLive.postValue(stopwatchTimeLocal)
                }
            }, 0, 1000)
            stopwatchCancelled = false
            startAlreadyClicked = true
        }
    }

    fun stopStopwatch() {
        timer.cancel()
        stopwatchCancelled = true
        startAlreadyClicked = false
    }

    fun resetStopwatch() {
        stopStopwatch()
        stopwatchTimeLocal = 0
        savedStopwatchTimesLocal.clear()
        stopwatchTimeLive.postValue(stopwatchTimeLocal)
        savedStopwatchTimesLive.postValue(savedStopwatchTimesLocal)
    }

    fun lapStopwatch() {
        savedStopwatchTimesLocal.add(stopwatchTimeLocal)
        savedStopwatchTimesLive.postValue(savedStopwatchTimesLocal)
    }

    fun stopwatchTimeLive(): LiveData<Int> {
        return stopwatchTimeLive
    }

    fun savedStopwatchTimesLive(): LiveData<MutableList<Int>> {
        return savedStopwatchTimesLive
    }
}