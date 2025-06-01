package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Movie;
import com.example.myapplication.view.MovieDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final List<Movie> movies; // List to hold movie data
    private final Context context;   // Context for UI-related operations
    //
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    /**
     * ViewHolder class for the movie card layout.
     * Holds references to UI elements and binds data to views.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final CardView cardView;


        public MovieViewHolder(@NonNull View view) {
            super(view);
            cardView = view.findViewById(R.id.movieCard);
            imageView = view.findViewById(R.id.movieImage);
            textView = view.findViewById(R.id.movieTitle);
        }


        /**
         * Binds movie data to UI components.
         *
         * @param movie   The movie object to bind
         * @param context The context for launching activities and loading images
         */
        public void bind(@NonNull Movie movie, Context context) {
            textView.setText(movie.getTitle());

            // Load movie image using Glide
            Glide.with(context)
                    .load(context.getString(R.string.BASE_URL) + movie.getImage())
                    .into(imageView);

            cardView.setOnClickListener(v -> listener.onItemClick(movie));
        }
    }

    /**
     * Constructor for MoviesAdapter.
     * Ensures movies list is never null by using an empty ArrayList if null is passed.
     *
     * @param movies  List of movies to display
     * @param context Context for UI interactions
     */
    public MoviesAdapter(List<Movie> movies, Context context) {
        this.movies = Objects.requireNonNullElseGet(movies, ArrayList::new);
        this.context = context;
    }

    /**
     * Setting the movie card listener
     *
     * @param listener movie card listener
     */
    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    /**
     * Inflate the movie card layout
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return viewHolder
     */
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    /**
     * Bind movie data to the ViewHolder
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        holder.bind(movies.get(position), context);
    }

    /**
     * @return total number of items in the dataset
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * Updates the dataset with a new list of movies.
     * Clears the existing list and inserts the new movies efficiently.
     *
     * @param newMovies The new list of movies to display
     */
    public void updateMovies(List<Movie> newMovies) {
        int oldSize = movies.size();

        // Remove old items
        if (!movies.isEmpty()) {
            movies.clear();
            notifyItemRangeRemoved(0, oldSize);
        }

        // Add new items and notify RecyclerView
        if (newMovies != null && !newMovies.isEmpty()) {
            movies.addAll(newMovies);
            notifyItemRangeInserted(0, newMovies.size());
        }
    }
}
