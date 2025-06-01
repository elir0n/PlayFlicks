package com.example.myapplication.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Movie entity for Room database and JSON serialization.
 */
@Entity(tableName = "movies")
public class Movie {

    // Primary key for the movie entity
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "movieId")
    private long movieId;

    // Movie title
    @SerializedName("title")
    @ColumnInfo(name = "title")
    private String title;

    // URL or path to the movie's image
    @SerializedName("image")
    @ColumnInfo(name = "image")
    private String image;

    // URL or path to the movie's video
    @SerializedName("video")
    @ColumnInfo(name = "video")
    private String video;

    // Brief description of the movie
    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description;

    /**
     * Constructor to initialize a Movie object.
     *
     * @param movieId     Unique identifier for the movie
     * @param title       Title of the movie
     * @param image       URL/path to the movie poster image
     * @param video       URL/path to the movie video
     * @param description Short description of the movie
     */
    public Movie(long movieId, String title, String image, String video, String description) {
        this.movieId = movieId;
        this.title = title;
        this.image = image;
        this.video = video;
        this.description = description;
    }

    /**
     * Gets the movie ID.
     *
     * @return the movie ID
     */
    public long getMovieId() {
        return movieId;
    }

    /**
     * Sets the movie ID.
     *
     * @param movieId the movie ID to set
     */
    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    /**
     * Gets the title of the movie.
     *
     * @return the title of the movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the movie.
     *
     * @param title the title of the movie to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the image URL for the movie.
     *
     * @return the image URL of the movie
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image URL for the movie.
     *
     * @param image the image URL to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets the video URL for the movie.
     *
     * @return the video URL of the movie
     */
    public String getVideo() {
        return video;
    }

    /**
     * Sets the video URL for the movie.
     *
     * @param video the video URL to set
     */
    public void setVideo(String video) {
        this.video = video;
    }

    /**
     * Gets the description of the movie.
     *
     * @return the description of the movie
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the movie.
     *
     * @param description the description of the movie to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
