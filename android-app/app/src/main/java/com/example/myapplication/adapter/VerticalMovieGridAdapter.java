package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Movie;

import java.util.List;

/**
 * Adapter for displaying movies in a vertical list, with two movies per row.
 */
public class VerticalMovieGridAdapter extends RecyclerView.Adapter<VerticalMovieGridAdapter.MovieViewHolder> {

    private final List<Movie> movies;
    private final Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    /**
     * Interface for handling item click events.
     */
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    /**
     * Interface for handling item long click events.
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(long movieId);
    }

    /**
     * Constructor for initializing adapter with movie data.
     *
     * @param movies  List of Movie objects.
     * @param context Application context.
     */
    public VerticalMovieGridAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    /**
     * Updates the movie list and refreshes the RecyclerView.
     *
     * @param newMovies New list of movies.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateMovies(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item_details, parent, false);
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
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    /**
     * @return total number of items in the dataset
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * Sets the click listener for movie items.
     *
     * @param listener Click event listener.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * Sets the long click listener for movie items.
     *
     * @param listener Long click event listener.
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    /**
     * ViewHolder class for movie items.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView movieImage;
        private final TextView movieTitle;
        private final CardView movieCard;

        /**
         * initializes class
         *
         * @param itemView the viewHolder item
         */
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImage = itemView.findViewById(R.id.movieImage);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            movieCard = itemView.findViewById(R.id.movieCard);
        }

        /**
         * Binds movie data to the ViewHolder.
         *
         * @param movie Movie object.
         */
        public void bind(Movie movie) {
            Glide.with(context)
                    .load(context.getString(R.string.BASE_URL) + movie.getImage())
                    .into(movieImage)
                    .onLoadFailed(AppCompatResources.getDrawable(context, R.drawable.no_image_found));

            movieTitle.setText(movie.getTitle());

            // Handle click event
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(movie);
                }
            });

            // Handle long click event
            movieCard.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(movie.getMovieId());
                    return true;
                }
                return false;
            });
        }
    }
}