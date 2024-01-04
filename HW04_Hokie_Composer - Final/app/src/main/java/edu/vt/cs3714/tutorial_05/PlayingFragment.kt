package edu.vt.cs3714.tutorial_05

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import edu.vt.cs3714.tutorial_05.databinding.FragmentPlayingBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PlayingFragment : Fragment(), View.OnClickListener {
    private var binding: FragmentPlayingBinding? = null
    private var trackName: TextView? = null
    private var restartButton: Button? = null
    private var musicCompletionReceiver: MusicCompletionReceiver? = null
    private var startMusicServiceIntent: Intent? = null
    private var isInitialized = false
    private lateinit var viewModel: SharedViewModel
    private var mainTrackDuration = 0
    private var sound1Duration: Long = 0
    private var sound2Duration: Long = 0
    private var sound3Duration: Long = 0
    private val clappingSoundDuration: Long = 5000
    private val cheeringSoundDuration: Long = 6000
    private val goHokiesSoundDuration: Long = 8000
    private var startTime = 0L
    private var pauseTime = 0L
    private var mainTrackSelected = -1
    private val handler = Handler(Looper.getMainLooper())
    private val startOverlappingSound1Runnable = Runnable {
        if (isAdded && view != null) {
            when (musicService?.musicPlayer()?.soundIndex1) {
                0 -> { // Clapping
                    binding?.soundtrackPic?.setImageResource(R.drawable.clappingimage)
                }

                1 -> { // Cheering
                    binding?.soundtrackPic?.setImageResource(R.drawable.cheeringimage)
                }

                2 -> { // Go Hokies!
                    binding?.soundtrackPic?.setImageResource(R.drawable.letsgohokiesimage)
                }
            }
            musicService?.startOverlappingSound(1)
            when (musicService?.musicPlayer()?.soundIndex1) {
                0 -> { // Clapping
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, clappingSoundDuration)
                }

                1 -> { // Cheering
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, cheeringSoundDuration)
                }

                2 -> { // Go Hokies!
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, goHokiesSoundDuration)
                }
            }
        }
    }
    private val startOverlappingSound2Runnable = Runnable {
        if (isAdded && view != null) {
            when (musicService?.musicPlayer()?.soundIndex2) {
                0 -> { // Clapping
                    binding?.soundtrackPic?.setImageResource(R.drawable.clappingimage)
                }

                1 -> { // Cheering
                    binding?.soundtrackPic?.setImageResource(R.drawable.cheeringimage)
                }

                2 -> { // Go Hokies!
                    binding?.soundtrackPic?.setImageResource(R.drawable.letsgohokiesimage)
                }
            }
            musicService?.startOverlappingSound(2)
            when (musicService?.musicPlayer()?.soundIndex2) {
                0 -> { // Clapping
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, clappingSoundDuration)
                }

                1 -> { // Cheering
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, cheeringSoundDuration)
                }

                2 -> { // Go Hokies!
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, goHokiesSoundDuration)
                }
            }
        }
    }
    private val startOverlappingSound3Runnable = Runnable {
        if (isAdded && view != null) {
            when (musicService?.musicPlayer()?.soundIndex3) {
                0 -> { // Clapping
                    binding?.soundtrackPic?.setImageResource(R.drawable.clappingimage)
                }

                1 -> { // Cheering
                    binding?.soundtrackPic?.setImageResource(R.drawable.cheeringimage)
                }

                2 -> { // Go Hokies!
                    binding?.soundtrackPic?.setImageResource(R.drawable.letsgohokiesimage)
                }
            }
            musicService?.startOverlappingSound(3)
            when (musicService?.musicPlayer()?.soundIndex3) {
                0 -> { // Clapping
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, clappingSoundDuration)
                }

                1 -> { // Cheering
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, cheeringSoundDuration)
                }

                2 -> { // Go Hokies!
                    Handler(Looper.getMainLooper()).postDelayed({
                        when (mainTrackSelected) {
                            0 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.gotechgoimage)
                            }

                            1 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.entersandmanimage)
                            }

                            2 -> {
                                binding?.soundtrackPic?.setImageResource(R.drawable.techtriumpimage)
                            }
                        }
                    }, goHokiesSoundDuration)
                }
            }
        }
    }
    var playPauseButton: Button? = null
    var musicService: MusicService? = null
    var isBound = false

    private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            val binder = iBinder as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true
            when (musicService?.getMainTrackStatus()) {
                0 -> playPauseButton?.text = getString(R.string.play)
                1 -> playPauseButton?.text = getString(R.string.pause)
                2 -> playPauseButton?.text = getString(R.string.resume)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            musicService = null
            isBound = false
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        return currentDate.format(formatter)
    }

    private fun appendEvent(event: String) {
        val inputData = workDataOf("date" to getCurrentDate(), "userID" to MainActivity.USER_ID, "event" to event)
        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(requireActivity().applicationContext).enqueue(uploadWorkRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        startMusicServiceIntent = Intent(context, MusicService::class.java)
        if (!isInitialized) {
            activity?.startService(startMusicServiceIntent)
            isInitialized = true
        }
        musicCompletionReceiver = MusicCompletionReceiver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayingBinding.inflate(inflater, container, false)

        trackName = binding?.audioFileName
        viewModel.audioFileName.observe(viewLifecycleOwner) { fileName ->
            // Update your UI or logic with the new fileName
            binding?.audioFileName?.text = fileName
        }
        viewModel.soundtrackPic.observe(viewLifecycleOwner) { picResourceId ->
            // Update your ImageView with the new image
            binding?.soundtrackPic?.setImageResource(picResourceId)
        }
        viewModel.shouldRemoveCallback.observe(viewLifecycleOwner) { shouldRemove ->
            if (shouldRemove) {
                handler.removeCallbacks(startOverlappingSound1Runnable)
                handler.removeCallbacks(startOverlappingSound2Runnable)
                handler.removeCallbacks(startOverlappingSound3Runnable)
                viewModel.callbackRemoved() // Notify that callback has been removed
            }
        }
        viewModel.mainTrack.observe(viewLifecycleOwner) { mainTrack ->
            mainTrackSelected = mainTrack
        }

        playPauseButton = binding?.playPauseButton
        restartButton = binding?.restartMusicButton
        restartButton?.isEnabled = false

        playPauseButton?.setOnClickListener(this)
        restartButton?.setOnClickListener {
            appendEvent("Playing screen restart button pressed")
            playPauseButton?.text = getString(R.string.pause)
            musicService?.startMainTrack() // restart main

            musicService?.setupOverlappingSounds() // restart sounds
            musicService?.musicPlayer()?.overlappingSoundsPlayer?.setOnLoadCompleteListener { _, soundId, status ->
                if (status == 0) { // status 0 indicates success
                    when (soundId) {
                        musicService?.musicPlayer()!!.overlappingSoundId1 -> { // first sound loaded
                            sound1Duration =
                                (musicService?.musicPlayer()?.seeker1Progress!! / 100.0 * mainTrackDuration).toLong()
                            handler.postDelayed(startOverlappingSound1Runnable, sound1Duration)
                        }

                        musicService?.musicPlayer()!!.overlappingSoundId2 -> { // second loaded
                            sound2Duration =
                                (musicService?.musicPlayer()?.seeker2Progress!! / 100.0 * mainTrackDuration).toLong()
                            handler.postDelayed(startOverlappingSound2Runnable, sound2Duration)
                        }

                        musicService?.musicPlayer()!!.overlappingSoundId3 -> { // third
                            sound3Duration =
                                (musicService?.musicPlayer()?.seeker3Progress!! / 100.0 * mainTrackDuration).toLong()
                            handler.postDelayed(startOverlappingSound3Runnable, sound3Duration)
                        }
                    }
                }
            }
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(startOverlappingSound1Runnable)
        handler.removeCallbacks(startOverlappingSound2Runnable)
        handler.removeCallbacks(startOverlappingSound3Runnable)
        musicService?.musicPlayer()?.setMainTrackStatus(0)
    }

    fun resetPlayPauseButtonToPlay() {
        playPauseButton?.text = getString(R.string.play)
    }

    override fun onClick(v: View?) {
        if (isBound) {
            when (musicService?.getMainTrackStatus()) {
                0 -> { // stopped
                    appendEvent("Playing screen play button pressed")
                    mainTrackDuration = musicService?.startMainTrack()!!
                    musicService?.setupOverlappingSounds()
                    musicService?.musicPlayer()?.overlappingSoundsPlayer?.setOnLoadCompleteListener { _, soundId, status ->
                        if (status == 0) { // status 0 indicates success
                            startTime = System.currentTimeMillis()
                            when (soundId) {
                                musicService?.musicPlayer()!!.overlappingSoundId1 -> { // first sound loaded
                                    sound1Duration =
                                        (musicService?.musicPlayer()?.seeker1Progress!! / 100.0 * mainTrackDuration).toLong()
                                    handler.postDelayed(
                                        startOverlappingSound1Runnable,
                                        sound1Duration
                                    )
                                }

                                musicService?.musicPlayer()!!.overlappingSoundId2 -> { // second loaded
                                    sound2Duration =
                                        (musicService?.musicPlayer()?.seeker2Progress!! / 100.0 * mainTrackDuration).toLong()
                                    handler.postDelayed(
                                        startOverlappingSound2Runnable,
                                        sound2Duration
                                    )
                                }

                                musicService?.musicPlayer()!!.overlappingSoundId3 -> { // third
                                    sound3Duration =
                                        (musicService?.musicPlayer()?.seeker3Progress!! / 100.0 * mainTrackDuration).toLong()
                                    handler.postDelayed(
                                        startOverlappingSound3Runnable,
                                        sound3Duration
                                    )
                                }
                            }
                        }
                    }
                    playPauseButton?.text = getString(R.string.pause)
                    restartButton?.isEnabled = true
                }

                1 -> { // playing
                    appendEvent("Playing screen pause button pressed")
                    musicService?.pauseMainTrack()
                    pauseTime = System.currentTimeMillis()
                    musicService?.pauseOverlappingSound(1)
                    handler.removeCallbacks(startOverlappingSound1Runnable)
                    musicService?.pauseOverlappingSound(2)
                    handler.removeCallbacks(startOverlappingSound2Runnable)
                    musicService?.pauseOverlappingSound(3)
                    handler.removeCallbacks(startOverlappingSound3Runnable)
                    playPauseButton?.text = getString(R.string.resume)
                    restartButton?.isEnabled = true
                }

                2 -> { // paused
                    appendEvent("Playing screen resume button pressed")
                    musicService?.resumeMainTrack()
                    sound1Duration -= pauseTime - startTime
                    if (sound1Duration > 0)
                        handler.postDelayed(startOverlappingSound1Runnable, sound1Duration)
                    sound2Duration -= pauseTime - startTime
                    if (sound2Duration > 0)
                        handler.postDelayed(startOverlappingSound2Runnable, sound2Duration)
                    sound3Duration -= pauseTime - startTime
                    if (sound3Duration > 0)
                        handler.postDelayed(startOverlappingSound3Runnable, sound3Duration)
                    playPauseButton?.text = getString(R.string.pause)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isBound) {
            activity?.unbindService(musicServiceConnection)
            isBound = false
        }
        activity?.unregisterReceiver(musicCompletionReceiver)
    }

    override fun onResume() {
        super.onResume()
        if (isInitialized && !isBound) {
            startMusicServiceIntent?.let {
                activity?.bindService(
                    it,
                    musicServiceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.MAIN_TRACK_COMPLETED)
        }
        activity?.registerReceiver(musicCompletionReceiver, intentFilter)
    }

    // Make sure we clean up and remove receiver as the app is being destroyed.
    override fun onDestroy() {
        super.onDestroy()
        musicService?.musicPlayer()?.releaseAll()
        if (isBound) {
            activity?.unbindService(musicServiceConnection)
            isBound = false
        }
//        activity?.unregisterReceiver(musicCompletionReceiver)
    }
}