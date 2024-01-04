package edu.vt.cs3714.retrofitrecyclerviewguide

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.vt.cs3714.retrofitrecyclerviewguide.databinding.FragmentListScreenBinding
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ListScreen fragment class
 */
class ListScreen : Fragment(), AdapterView.OnItemSelectedListener {
    private val movies = ArrayList<MovieItem>()
//    private lateinit var job: Job
    private val apiKey by lazy {
        resources.getString(R.string.api_key)
    }
    private val retrofitService by lazy {
        RetrofitService.create(resources.getString(R.string.base_url))
    }
    private var binding: FragmentListScreenBinding? = null
    private val model: MovieViewModel by activityViewModels()
    var spinner: Spinner? = null
    val adapter = MovieListAdapter()

    fun movies() : ArrayList<MovieItem> {
        return movies
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListScreenBinding.inflate(inflater, container, false)
        spinner = binding!!.sortOptions
        spinner!!.onItemSelectedListener = this@ListScreen
        val recyclerView = binding?.movieList
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        model.allMovies.observe(viewLifecycleOwner) { movies ->
            // Update the RecyclerView adapter here
            movies?.let {
                adapter.setMovies(it)
            }
        }

        /*binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchTerm ->
                    val filteredMovies = movies.filter { it.title.contains(searchTerm, ignoreCase = true) }
                    adapter.setMovies(filteredMovies)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val moviesString = movies.joinToString(separator = "\n") { it.title }
                newText?.let { query ->
                    val filteredMovies = movies.filter { it.title.contains(query, ignoreCase = true) }
                    adapter.setMovies(filteredMovies)
                    val filteredMoviesString = filteredMovies.joinToString(separator = "\n") { it.title }
                    Log.d("filtered movies", "list: $filteredMoviesString")
                }
                return true
            }

        })*/

        binding?.refresh?.setOnClickListener{
            // Display a short toast message
            Toast.makeText(context, "Refresh clicked", Toast.LENGTH_SHORT).show()
            model.refreshMovies(1)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sorting_options,  // Your spinner item layout
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.adapter = adapter
        }

        Log.d("ListScreen", "Movies: $movies")
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assuming you added a Switch in your layout with the id "switchFilterLiked"
        binding?.filter?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Remove observer from allMovies to avoid multiple observers
                model.allMovies.removeObservers(viewLifecycleOwner)

                model.likedMovies.observe(viewLifecycleOwner, { movies ->
                    // Assuming you have a method in your adapter called 'submitList' or 'updateList' to update the dataset.
                    adapter.setMovies(movies)
                })
            } else {
                // Remove observer from likedMovies to avoid multiple observers
                model.likedMovies.removeObservers(viewLifecycleOwner)

                model.allMovies.observe(viewLifecycleOwner, { movies ->
                    // Assuming you have a method in your adapter called 'submitList' or 'updateList' to update the dataset.
                    adapter.setMovies(movies)
                })
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
        Toast.makeText(context, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()

        // If you want to perform any action based on the selected item:
        when (selectedItem) {
            "Title" -> {
                // Sort movies by title or any other action you want
                movies.sortBy { it.title }
                binding?.movieList?.adapter?.notifyDataSetChanged()
            }
            "Rating" -> {
                // Sort movies by rating or any other action you want
                movies.sortBy { it.vote_average }
                binding?.movieList?.adapter?.notifyDataSetChanged()
            }
            // Add more conditions if needed
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    /**
     * A RecyclerView adapter class. Provides the list of items to be displayed there.
     */
    inner class MovieListAdapter : RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {
        inner class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            fun bindItems(movieItem: MovieItem) {
                itemView.setOnClickListener {
                    Log.d("retrofit_demo", "list tap ")
                    // Convert the date to string
                    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                    val dateString = dateFormat.format(movieItem.release_date)
                    val bundle = bundleOf(
                        "poster_path" to movieItem.poster_path,
                        "title" to movieItem.title,
                        "release_date" to dateString,
                        "overview" to movieItem.overview
                    )
                    bundle.putParcelable("movie_key", movieItem)
                    Log.d("release date type", "${(movieItem.release_date)::class.simpleName ?: "Unknown"}")
                    binding?.root?.findNavController()?.navigate(R.id.action_listScreen_to_detailScreen, bundle)
                }
            }
        }

        internal fun setMovies(movies: List<MovieItem>) {
            this@ListScreen.movies.clear()
            this@ListScreen.movies.addAll(movies)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return movies.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
            return MovieViewHolder(v)
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            Glide.with(this@ListScreen)
                .load(resources.getString(R.string.picture_base_url) + movies[position].poster_path)
                .apply(RequestOptions().override(128, 128))
                .into(holder.view.findViewById(R.id.poster))

            holder.view.findViewById<TextView>(R.id.title).text = movies[position].title

            holder.view.findViewById<TextView>(R.id.rating).text = movies[position].vote_average.toString()

            holder.bindItems(movies[position])
        }
    }
}