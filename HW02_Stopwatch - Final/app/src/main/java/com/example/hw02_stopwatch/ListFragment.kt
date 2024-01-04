package com.example.hw02_stopwatch

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hw02_stopwatch.databinding.FragmentListBinding

/**
 * Displays the list of lap times
 */
class ListFragment : Fragment() {
    private var binding: FragmentListBinding? = null
    private val model: StopwatchModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentListBinding.inflate(layoutInflater)

        binding?.toFragmentControlB?.setOnClickListener {
            binding?.root?.findNavController()
                ?.navigate(R.id.action_listFragment_to_controlFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val adapter = SavedStopwatchTimesListAdapter()
        val recyclerView = binding?.lapTimesRv
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        model.savedStopwatchTimesLive().observe(viewLifecycleOwner) { savedStopwatchTimesLive ->
            savedStopwatchTimesLive?.let {
                adapter.setSavedStopwatchTimesLive(it)
            }
        }

        return binding?.root
    }

    inner class SavedStopwatchTimesListAdapter :
        RecyclerView.Adapter<SavedStopwatchTimesListAdapter.SavedStopwatchTimeHolder>() {
        private var savedStopwatchTimesLive = emptyList<Int>()

        internal fun setSavedStopwatchTimesLive(savedStopwatchTimesLive: List<Int>) {
            this.savedStopwatchTimesLive = savedStopwatchTimesLive
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SavedStopwatchTimeHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.saved_stopwatch_time_item, parent, false)
            return SavedStopwatchTimeHolder(view)
        }

        override fun getItemCount(): Int {
            return savedStopwatchTimesLive.size
        }

        override fun onBindViewHolder(holder: SavedStopwatchTimeHolder, position: Int) {
            holder.view.findViewById<TextView>(R.id.text_view).text = String.format(
                "%02d:%02d",
                savedStopwatchTimesLive[position] / 60,
                savedStopwatchTimesLive[position] % 60
            )
        }

        inner class SavedStopwatchTimeHolder(val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d("adapter", "holder tapped")
            }
        }
    }

    // tried to use binding for recycler adapter; didn't work - unstable when navigating between fragments
//    inner class SavedStopwatchTimesListAdapter :
//        RecyclerView.Adapter<SavedStopwatchTimesListAdapter.SavedStopwatchTimeHolder>() {
//        private var savedStopwatchTimesLive = emptyList<Int>()
//
//        internal fun setSavedStopwatchTimesLive(savedStopwatchTimesLive: List<Int>) {
//            this.savedStopwatchTimesLive = savedStopwatchTimesLive
//            notifyDataSetChanged()
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedStopwatchTimeHolder {
//            val viewBinding = SavedStopwatchTimeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            return SavedStopwatchTimeHolder(viewBinding)
//        }
//
//        override fun getItemCount(): Int {
//            return savedStopwatchTimesLive.size
//        }
//
//        override fun onBindViewHolder(holder: SavedStopwatchTimeHolder, position: Int) {
//            with(holder) {
//                viewBinding?.textView?.text = savedStopwatchTimesLive.get(position).toString()
//            }
//        }
//
//        inner class SavedStopwatchTimeHolder(val viewBinding: SavedStopwatchTimeItemBinding) : RecyclerView.ViewHolder(binding!!.root) {
//        }
//    }
}