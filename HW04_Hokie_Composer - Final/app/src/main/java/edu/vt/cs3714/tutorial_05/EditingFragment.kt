package edu.vt.cs3714.tutorial_05

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import edu.vt.cs3714.tutorial_05.databinding.FragmentEditingBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditingFragment : Fragment(), AdapterView.OnItemSelectedListener {
    interface ViewPagerController {
        fun navigateToPage(pageIndex: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewPagerController) {
            viewPagerController = context
        } else {
            throw RuntimeException("$context must implement ViewPagerController")
        }
    }

    override fun onDetach() {
        super.onDetach()
        viewPagerController = null
    }

    private var viewPagerController: ViewPagerController? = null
    private var binding: FragmentEditingBinding? = null
    private var bgMusicSelector: Spinner? = null
    private var overlapSoundSelector1: Spinner? = null
    private var overlapSoundSelector2: Spinner? = null
    private var overlapSoundSelector3: Spinner? = null
    private var musicService: MusicService? = null
    private var isBound = false
    private lateinit var viewModel: SharedViewModel

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Cast the IBinder and get the MusicService instance
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true
            // Now you can use musicService to interact with MusicPlayer
        }

        override fun onServiceDisconnected(className: ComponentName) {
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

    override fun onStart() {
        super.onStart()
        // Bind to MusicService
        Intent(context, MusicService::class.java).also { intent ->
            context?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            context?.unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditingBinding.inflate(inflater, container, false)

        bgMusicSelector = binding!!.mainTrackSelector
        bgMusicSelector!!.onItemSelectedListener = this
        overlapSoundSelector1 = binding!!.overlappingSoundtrackEditor1
        overlapSoundSelector1!!.onItemSelectedListener = this
        overlapSoundSelector2 = binding!!.overlappingSoundtrackEditor2
        overlapSoundSelector2!!.onItemSelectedListener = this
        overlapSoundSelector3 = binding!!.overlappingSoundtrackEditor3
        overlapSoundSelector3!!.onItemSelectedListener = this

        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.background_music,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                bgMusicSelector!!.adapter = adapter
            }
            ArrayAdapter.createFromResource(
                it,
                R.array.overlapping_soundtracks,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                overlapSoundSelector1!!.adapter = adapter
                overlapSoundSelector2!!.adapter = adapter
                overlapSoundSelector3!!.adapter = adapter
            }
        }

        binding?.overlappingSoundtrackSeeker1?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Here, 'progress' is the current value of the SeekBar
                musicService?.updateSeeker1Progress(progress)
                Log.d("seeker1", "updated")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                appendEvent("Editing screen seek bar 1 touched")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // You can also implement this method if needed
            }
        })
        binding?.overlappingSoundtrackSeeker2?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Here, 'progress' is the current value of the SeekBar
                musicService?.updateSeeker2Progress(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                appendEvent("Editing screen seek bar 2 touched")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // You can also implement this method if needed
            }
        })
        binding?.overlappingSoundtrackSeeker3?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Here, 'progress' is the current value of the SeekBar
                musicService?.updateSeeker3Progress(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                appendEvent("Editing screen seek bar 3 touched")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // You can also implement this method if needed
            }
        })

        binding?.toPlayingScreenButton?.setOnClickListener {
            // binding?.root?.findNavController()?.navigate(R.id.action_editingFragment_to_playingFragment)
            viewPagerController?.navigateToPage(1)
            appendEvent("Editing screen play button pressed")
        }

        return binding?.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            binding?.mainTrackSelector?.id -> {
                // Handle background music selection
                when (position) {
                    0 -> { // Go Tech Go!
                        viewModel.requestToRemoveCallback()
                        musicService?.resetMainTrackAndOverlappingSounds()
                        musicService?.musicPlayer()?.mainTrackIndex = 0
                        viewModel.setAudioFileName("Go Tech Go!")
                        viewModel.setSoundtrackPic(R.drawable.gotechgoimage)
                        viewModel.mainTrackSelected(position)
                    }

                    1 -> {
                        viewModel.requestToRemoveCallback()
                        musicService?.resetMainTrackAndOverlappingSounds()
                        musicService?.musicPlayer()?.mainTrackIndex = 1
                        viewModel.setAudioFileName("Enter Sandman")
                        viewModel.setSoundtrackPic(R.drawable.entersandmanimage)
                        viewModel.mainTrackSelected(position)
                    }

                    2 -> {
                        viewModel.requestToRemoveCallback()
                        musicService?.resetMainTrackAndOverlappingSounds()
                        musicService?.musicPlayer()?.mainTrackIndex = 2
                        viewModel.setAudioFileName("Tech Triumph")
                        viewModel.setSoundtrackPic(R.drawable.techtriumpimage)
                        viewModel.mainTrackSelected(position)
                    }
                }
            }

            binding?.overlappingSoundtrackEditor1?.id -> {
                // Handle overlapping sound selection for each spinner
                when (position) {
                    0 -> { // Clapping
                        // resetThisSound()
                        musicService?.musicPlayer()?.soundIndex1 = 0
                    }

                    1 -> { // Cheering
                        musicService?.musicPlayer()?.soundIndex1 = 1
                    }

                    2 -> { // Go Hokies!
                        musicService?.musicPlayer()?.soundIndex1 = 2
                    }
                }
            }

            binding?.overlappingSoundtrackEditor2?.id -> {
                // Handle overlapping sound selection for each spinner
                when (position) {
                    0 -> { // Clapping
                        // resetThisSound()
                        musicService?.musicPlayer()?.soundIndex2 = 0
                    }

                    1 -> { // Cheering
                        musicService?.musicPlayer()?.soundIndex2 = 1
                    }

                    2 -> { // Go Hokies!
                        musicService?.musicPlayer()?.soundIndex2 = 2
                    }
                }
            }

            binding?.overlappingSoundtrackEditor3?.id -> {
                // Handle overlapping sound selection for each spinner
                when (position) {
                    0 -> { // Clapping
                        // resetThisSound()
                        musicService?.musicPlayer()?.soundIndex3 = 0
                    }

                    1 -> { // Cheering
                        musicService?.musicPlayer()?.soundIndex3 = 1
                    }

                    2 -> { // Go Hokies!
                        musicService?.musicPlayer()?.soundIndex3 = 2
                    }
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}