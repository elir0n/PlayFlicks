package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Movie;
import com.example.myapplication.viewmodel.MoviesViewModel;
import com.example.myapplication.model.Category;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * RecyclerView Adapter for displaying a list of categories and an optional video player.
 */
public class ListItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_VIDEO = 0; // View type for video player
    private static final int VIEW_TYPE_MOVIES = 1; // View type for movie categories

    private final Context context;
    private final List<Category> categories; // list of all categories
    private Map<String, List<Movie>> categoriesMap; // all categories names with their movies
    private boolean isUsingMap = false; // if i am using the map
    private List<String> mapKeys; // categories keys

    private ExoPlayer player = null; // the exoPlayer
    private MoviesAdapter.OnItemClickListener movieCardListener;

    /**
     * Setting the movie card listener
     *
     * @param listener movie card listener
     */
    public void setOnClickCardListener(MoviesAdapter.OnItemClickListener listener) {
        this.movieCardListener = listener;
    }


    /**
     * Constructor for list-based categories.
     */
    public ListItemsAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = new LinkedList<>(categories);
    }

    /**
     * Constructor for map-based categories.
     */
    public ListItemsAdapter(Context context, @NonNull Map<String, List<Movie>> categoriesMap) {
        this.context = context;
        this.categoriesMap = categoriesMap;
        this.categories = new LinkedList<>();
        this.categories.add(0, null); // Placeholder for video player
        this.mapKeys = new LinkedList<>(categoriesMap.keySet());
        this.isUsingMap = true;
    }

    /**
     * @param position Position to query
     * @return Item type
     */
    @Override
    public int getItemViewType(int position) {
        return isUsingMap && position == 0 ? VIEW_TYPE_VIDEO : VIEW_TYPE_MOVIES;
    }

    /**
     * @return Number of items
     */
    @Override
    public int getItemCount() {
        return isUsingMap ? mapKeys.size() + 1 : categories.size();
    }

    /**
     * Updates data for list-based categories.
     */
    public void updateData(List<Category> newCategories) {
        if (newCategories == null) {
            int oldSize = categories.size();
            categories.clear();
            notifyItemRangeRemoved(0, oldSize);
            return;
        }
        categories.clear();
        categories.addAll(newCategories);
        notifyItemRangeChanged(0, categories.size());
    }

    /**
     * Updates data for map-based categories.
     */
    public void updateData(Map<String, List<Movie>> newCategoriesMovies) {
        if (newCategoriesMovies == null) {
            int oldSize = categoriesMap.size();
            categoriesMap.clear();
            mapKeys.clear();
            notifyItemRangeRemoved(1, oldSize + 1); // from 1 because its without video
            return;
        }
        categoriesMap.clear();
        categoriesMap = newCategoriesMovies;
        mapKeys.clear();
        mapKeys.addAll(categoriesMap.keySet());
        notifyItemRangeChanged(0, categories.size());
    }

    /**
     * Handle view holder creation
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return corresponding viewHolder
     */

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_VIDEO) {
            return new VideoViewHolder(inflater.inflate(R.layout.video_in_row_item, parent, false));
        } else {
            return new RecyleViewHolder(inflater.inflate(R.layout.recycle_in_row_item, parent, false));
        }
    }

    /**
     * Binding the viewHolder
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyleViewHolder) {
            String title = isUsingMap ? mapKeys.get(position - 1) : categories.get(position).getTitle();
            String categoryId = isUsingMap ? "" : categories.get(position).getCategoryId();
            ((RecyleViewHolder) holder).bind(categoryId, title);
        }
    }

    /**
     * Plays video only on view
     *
     * @param holder Holder of the view being attached
     */
    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof VideoViewHolder && holder.itemView.isShown()) {
            ((VideoViewHolder) holder).playVideo();
        }
    }

    /**
     * Stop video when isn't visible
     *
     * @param holder Holder of the view being detached
     */
    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof VideoViewHolder && !holder.itemView.isShown()) {
            releasePlayer();
        }
    }


    /**
     * Detach player on adapter detachment
     *
     * @param recyclerView The RecyclerView instance which stopped observing this adapter.
     */
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        releasePlayer();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * Releasing the exoPlayer
     */
    void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    /**
     * ViewHolder for handling video playback.
     */
    class VideoViewHolder extends RecyclerView.ViewHolder {
        private final PlayerView playerView;
        private final CardView videoCard;

        VideoViewHolder(View view) {
            super(view);
            playerView = view.findViewById(R.id.videoView);
            videoCard = view.findViewById(R.id.videoCard);
        }

        /**
         * Play the random movie
         */
        void playVideo() {
            new Thread(() -> {
                List<Movie> movies = new ArrayList<>();
                for (List<Movie> movieList : categoriesMap.values()) {
                    movies.addAll(movieList);
                }
                if (movies.isEmpty()) return;
                Movie randomMovie = movies.get(new Random().nextInt(movies.size()));
                ((Activity) context).runOnUiThread(() -> startVideo(randomMovie.getVideo()));
            }).start();
        }


        /**
         * starting movie video
         *
         * @param videoUrl videoUrl to play
         */
        private void startVideo(String videoUrl) {
            releasePlayer();
            playerView.setVisibility(View.VISIBLE);
            videoCard.setVisibility(View.VISIBLE);
            player = new ExoPlayer.Builder(context).build();
            playerView.setPlayer(player);
            player.setMediaItem(MediaItem.fromUri(Uri.parse(context.getString(R.string.BASE_URL) + videoUrl)));
            player.setVolume(0);
            player.prepare();
            player.play();

            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_ENDED) playVideo();
                }
            });
        }
    }

    /**
     * ViewHolder for handling movie categories.
     */
    class RecyleViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerView;
        private final TextView title;

        private final MoviesAdapter adapter;

        /**
         * Initialize the viewHolder
         *
         * @param view current view
         */
        RecyleViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.rowRecycleView);
            title = view.findViewById(R.id.etCategoryTitle);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            this.title.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);

            adapter = new MoviesAdapter(new LinkedList<>(), context);
            adapter.setOnClickListener(movieCardListener);
        }

        /**
         * binding the viewHolder data
         *
         * @param categoryId the category id if no using map
         * @param title      the category title
         */
        void bind(String categoryId, String title) {
            this.title.setText(title);

            // if using map
            if (isUsingMap) {
                if (!Objects.requireNonNull(categoriesMap.get(title)).isEmpty()) {
                    this.title.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.updateMovies(categoriesMap.get(title));
                recyclerView.setAdapter(adapter);

                return;
            }

            MoviesViewModel moviesViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(MoviesViewModel.class);
            moviesViewModel.getMoviesByCategoryId(categoryId).observe((LifecycleOwner) context, movies -> {
                recyclerView.setAdapter(adapter);
                if (!movies.isEmpty()) {
                    this.title.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter.updateMovies(movies);
            });
        }
    }
}
