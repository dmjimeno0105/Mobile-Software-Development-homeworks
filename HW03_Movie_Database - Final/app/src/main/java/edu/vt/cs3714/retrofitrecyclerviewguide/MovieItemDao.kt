package edu.vt.cs3714.retrofitrecyclerviewguide

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieItemDao {

    @Query("SELECT * FROM movie_table order BY release_date DESC")
    fun getAllMovies(): LiveData<List<MovieItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: MovieItem)

    @Query("DELETE FROM movie_table")
    fun deleteAll()

    @Query("UPDATE movie_table SET liked = :status WHERE id = :movieId")
    suspend fun updateLikeStatus(movieId: Long, status: Boolean)

    @Query("SELECT * FROM movie_table WHERE liked = 1")
    fun getLikedMovies(): LiveData<List<MovieItem>>
}