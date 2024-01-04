package edu.vt.cs3714.tutorial_05

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicCompletionReceiver(private val playingFragment: PlayingFragment? = null) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            MusicService.MAIN_TRACK_COMPLETED -> {
                playingFragment?.resetPlayPauseButtonToPlay()
            }
        }
    }
}