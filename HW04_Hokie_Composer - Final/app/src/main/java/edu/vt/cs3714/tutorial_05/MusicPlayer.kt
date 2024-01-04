package edu.vt.cs3714.tutorial_05

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import java.io.IOException

class MusicPlayer(private val musicService: MusicService) : MediaPlayer.OnCompletionListener {
    private val MAIN_TRACK_PATHS =
        arrayOf(R.raw.gotechgo, R.raw.enter_sandman_intro, R.raw.texh_triumph_mvs)
    private val OVERLAPPING_SOUNDS_PATHS =
        arrayOf(R.raw.clapping, R.raw.cheering, R.raw.lestgohokies)
    private lateinit var mainTrackPlayer: MediaPlayer
    lateinit var overlappingSoundsPlayer: SoundPool
    private var mainTrackPlayerIsInitialized = false
    private var overlappingSoundsPlayerIsInitialized = false
    private var mainTrackStatus = 0 // 0: before initial play 1: playing 2: paused
    private var duration: Int = 0
    private var streamId1: Int = 0
    private var streamId2: Int = 0
    private var streamId3: Int = 0
    var overlappingSoundId1: Int = 0
    var overlappingSoundId2: Int = 0
    var overlappingSoundId3: Int = 0
    var soundIndex1: Int = 0
    var soundIndex2: Int = 0
    var soundIndex3: Int = 0
    var mainTrackIndex = 0
    var seeker1Progress: Int = 0
    var seeker2Progress: Int = 0
    var seeker3Progress: Int = 0

    fun getMainTrackStatus(): Int {
        return mainTrackStatus
    }

    fun setMainTrackStatus(status: Int) {
        mainTrackStatus = status
    }

    fun setupOverlappingSounds() {
        if (overlappingSoundsPlayerIsInitialized) {
            overlappingSoundsPlayer.release()
            overlappingSoundsPlayerIsInitialized = false
        }
        overlappingSoundsPlayer = SoundPool.Builder().setMaxStreams(3).setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        ).build()
        overlappingSoundsPlayerIsInitialized = true
        overlappingSoundId1 = overlappingSoundsPlayer.load(
            musicService.applicationContext,
            OVERLAPPING_SOUNDS_PATHS[soundIndex1],
            1
        )
        overlappingSoundId2 = overlappingSoundsPlayer.load(
            musicService.applicationContext,
            OVERLAPPING_SOUNDS_PATHS[soundIndex2],
            1
        )
        overlappingSoundId3 = overlappingSoundsPlayer.load(
            musicService.applicationContext,
            OVERLAPPING_SOUNDS_PATHS[soundIndex3],
            1
        )
    }

    fun playOverlappingSound(overlappingSound: Int) {
        when (overlappingSound) {
            1 -> {
                streamId1 = overlappingSoundsPlayer.play(overlappingSoundId1, 1f, 1f, 0, 0, 1f)
            }

            2 -> {
                streamId2 = overlappingSoundsPlayer.play(overlappingSoundId2, 1f, 1f, 0, 0, 1f)
            }

            3 -> {
                streamId3 = overlappingSoundsPlayer.play(overlappingSoundId3, 1f, 1f, 0, 0, 1f)
            }
        }
    }

    fun pauseOverlappingSound(overlappingSound: Int) {
        when (overlappingSound) {
            1 -> {
                overlappingSoundsPlayer.pause(streamId1)
            }

            2 -> {
                overlappingSoundsPlayer.pause(streamId2)
            }

            3 -> {
                overlappingSoundsPlayer.pause(streamId3)
            }
        }
    }

    fun resetOverlappingSounds() {
        if (overlappingSoundsPlayerIsInitialized) {
            overlappingSoundsPlayer.release()
            overlappingSoundsPlayerIsInitialized = false
        }
    }

    fun playMainTrack(): Int {
        if (mainTrackPlayerIsInitialized) {
            mainTrackPlayer.stop()
            mainTrackPlayer.reset()
            mainTrackPlayer.release()
        }
        mainTrackPlayer = MediaPlayer()
        mainTrackPlayerIsInitialized = true
        mainTrackPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        try {
            // It's a raw resource ID, so we need to handle it differently
            val afd =
                musicService.resources.openRawResourceFd(MAIN_TRACK_PATHS[mainTrackIndex])
            mainTrackPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mainTrackPlayer.prepare()
            mainTrackPlayer.setOnCompletionListener(this)
            mainTrackPlayer.start()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        mainTrackStatus = 1

        duration = mainTrackPlayer.duration
        return duration
    }

    fun pauseMainTrack() {
        if (mainTrackPlayerIsInitialized && mainTrackPlayer.isPlaying) {
            mainTrackPlayer.pause()
            mainTrackStatus = 2
        }
        else {
            playMainTrack()
        }
    }

    fun resumeMainTrack() {
        mainTrackPlayer.start()
        mainTrackStatus = 1
    }

    fun resetMainTrack() {
        // Check if player is initialized and playing, then stop and reset
        if (mainTrackPlayerIsInitialized && mainTrackPlayer.isPlaying) {
            mainTrackPlayer.stop()
        }

        if (mainTrackPlayerIsInitialized) {
            mainTrackPlayer.reset()
            // Setup the player again with the desired track
            val afd =
                musicService.resources.openRawResourceFd(MAIN_TRACK_PATHS[mainTrackIndex])
            mainTrackPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mainTrackPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mainTrackPlayer.prepare() // Prepare the player
        }

        mainTrackStatus = 0 // Update the music status as reset
    }


    override fun onCompletion(mp: MediaPlayer?) {
        mp?.release()
        if (mp == mainTrackPlayer) {
            mainTrackPlayerIsInitialized = false
            mainTrackStatus = 0
            musicService.mainTrackCompleted()
        }
        if (overlappingSoundsPlayerIsInitialized) {
            overlappingSoundsPlayer.release()
            overlappingSoundsPlayerIsInitialized = false
        }
    }

    fun releaseAll() {
        if (mainTrackPlayerIsInitialized) {
            if (mainTrackPlayer.isPlaying) {
                mainTrackPlayer.stop()
            }
            mainTrackPlayer.release()
            mainTrackPlayerIsInitialized = false
        }
//        if (overlappingSoundsPlayerIsInitialized) {
//            overlappingSoundsPlayer.release()
//            overlappingSoundsPlayerIsInitialized = false
//        }
    }
}