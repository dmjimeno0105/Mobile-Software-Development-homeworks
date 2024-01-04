package com.example.hw02_stopwatch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.hw02_stopwatch.databinding.FragmentControlBinding

/**
 * Displays stopwatch time and gives user control
 */
class ControlFragment : Fragment() {
    private var binding: FragmentControlBinding? = null
    private val model: StopwatchModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentControlBinding.inflate(layoutInflater)

        binding?.toFragmentListB?.setOnClickListener {
            binding?.root?.findNavController()?.navigate(R.id.action_controlFragment_to_listFragment)
        }
        binding?.startB?.setOnClickListener { model.startStopwatch() }
        binding?.stopB?.setOnClickListener { model.stopStopwatch() }
        binding?.resetB?.setOnClickListener { model.resetStopwatch() }
        binding?.lapB?.setOnClickListener { model.lapStopwatch() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        model.stopwatchTimeLive().observe(viewLifecycleOwner) { stopwatchTimeLive ->
            binding?.stopwatchTimeTv?.text =
                String.format("%02d:%02d", stopwatchTimeLive / 60, stopwatchTimeLive % 60)
        }

        return binding?.root
    }
}