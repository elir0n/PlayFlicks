package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.model.Movie;
import com.example.myapplication.repository.MoviesRepository;

import java.io.File;
import java.util.List;

/**
 * MoviesViewModel is responsible for interacting with the MoviesRepository to fetch and manipulate movie data.
 * It provides methods to retrieve movies by various criteria, such as category ID, movie ID, title, and recommended movies.
 * Additionally, it supports creating, editing, and updating movie information.
 */
public class MoviesViewModel extends AndroidViewModel {

    private final MoviesRepository moviesRepository;

    /**
     * Constructs a MoviesViewModel.
     *
     * @param application the application context
     */
    public MoviesViewModel(Application application) {
        super(application);
        moviesRepository = MoviesRepository.getInstance(application);
    }

    /**
     * Retrieves all movies belonging to a specified category.
     *
     * @param categoryId the ID of the category
     * @return a LiveData object containing a list of movies in the specified category
     */
    public LiveData<List<Movie>> getMoviesByCategoryId(String categoryId) {
        return moviesRepository.getMoviesByCategoryId(categoryId);
    }

    /**
     * Retrieves a movie by its unique ID.
     *
     * @param movieId the ID of the movie
     * @return a LiveData object containing the movie with the specified ID
     */
    public LiveData<Movie> getMovieById(long movieId) {
        return moviesRepository.getMovieById(movieId);
    }

    /**
     * Retrieves a movie by its title.
     *
     * @param title the title of the movie
     * @return a LiveData object containing the movie with the specified title
     */
    public LiveData<Movie> getMovieByTitle(String title) {
        return moviesRepository.getMovieByTitle(title);
    }

    /**
     * Retrieves a list of recommended movies based on the provided movie ID.
     *
     * @param movieId the ID of the movie used for recommendations
     * @return a LiveData object containing a list of recommended movies
     */
    public LiveData<List<Movie>> getRecommendedMovies(long movieId) {
        return moviesRepository.getRecommendedMovies(movieId);
    }

    /**
     * Retrieves a list of movies that match a given search query.
     *
     * @param query the search query string
     * @return a LiveData object containing a list of movies that match the query
     */
    public LiveData<List<Movie>> getQueryMovies(String query) {
        return moviesRepository.getQueryMovies(query);
    }

    /**
     * Creates a new movie entry with the specified details.
     *
     * @param movieTitle       the title of the movie
     * @param movieDescription the description of the movie
     * @param image            a file representing the movie's image
     * @param video            a file representing the movie's video
     * @param categories       a list of category IDs associated with the movie
     * @return a LiveData object containing the result of the movie creation (typically a status code)
     */
    public LiveData<Integer> createMovie(String movieTitle, String movieDescription, File image, File video, List<String> categories) {
        return moviesRepository.createMovie(movieTitle, movieDescription, image, video, categories);
    }

    /**
     * Edits an existing movie entry with the specified details.
     *
     * @param movieTitle       the new title of the movie
     * @param movieDescription the new description of the movie
     * @param image            a new file representing the movie's image
     * @param video            a new file representing the movie's video
     * @param categories       a new list of category IDs associated with the movie
     * @param oldMovieTitle    the title of the movie to be edited (used to identify the movie to update)
     * @return a LiveData object containing the result of the movie update (typically a status code)
     */
    public LiveData<Integer> editMovie(String movieTitle, String movieDescription, File image, File video, List<String> categories, String oldMovieTitle) {
        return moviesRepository.editMovie(movieTitle, movieDescription, image, video, categories, oldMovieTitle);
    }

    /**
     * Adds a movie to the user's watched list.
     *
     * @param movieId the ID of the movie to be added to the watched list
     */
    public void addMovieToUser(long movieId) {
        moviesRepository.addMovieToUserWatchedList(movieId);
    }
}
