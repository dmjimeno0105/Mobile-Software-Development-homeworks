package edu.vt.cs3714.retrofitrecyclerviewguide

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import edu.vt.cs3714.retrofitrecyclerviewguide.databinding.FragmentDetailScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * DetailScreen fragment class
 */
class DetailScreen : Fragment() {
    private var binding: FragmentDetailScreenBinding? = null
    private val model: MovieViewModel by activityViewModels()
    var movie: MovieItem? = null
    private lateinit var movieDao: MovieItemDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDetailScreenBinding.inflate(layoutInflater)
        movie = arguments?.getParcelable("movie_key")
        movieDao = MovieRoomDatabase.getDatabase(requireContext().applicationContext).movieDao()
        if (movie!!.liked) {
            binding?.likeButton?.text = "Liked"
        } else {
            binding?.likeButton?.text = "Unliked"
        }

        binding?.likeButton?.setOnClickListener {
            movie?.let {
                it.liked = !it.liked
            }

            // Update the database
            lifecycleScope.launch(Dispatchers.IO) {
                movieDao.updateLikeStatus(movie!!.id, movie!!.liked)
            }

            // Update UI should be on the Main thread
            lifecycleScope.launch(Dispatchers.Main) {
                if (movie!!.liked) {
                    binding?.likeButton?.text = "Liked"
                } else {
                    binding?.likeButton?.text = "Unliked"
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        model.allMovies.observe(viewLifecycleOwner) { movies ->
            // Update the RecyclerView adapter here
            movies?.let {
                Log.d("title type", "${this.arguments?.getString("title")!!::class.simpleName}")
                Log.d("release date", "${this.arguments?.getString("release")}")
                val posterPath = this.arguments?.getString("poster_path")
                if (!posterPath.isNullOrEmpty()) {
                    Glide.with(this).load(resources.getString(R.string.picture_base_url) + posterPath).into(binding?.detailScreenPoster!!)
                }
                binding?.detailScreenTitle?.text = this.arguments?.getString("title")
                binding?.detailScreenRelease?.text = "Release date: ${this.arguments?.getString("release_date")}"
                binding?.detailScreenOverview?.text = "Overview: ${this.arguments?.getString("overview")}"
            }
        }
        return binding?.root
    }
}