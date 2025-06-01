package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Entity representing a many-to-many relationship between movies and categories.
 * Each entry links a movie to a category, enforcing referential integrity via foreign keys.
 */
@Entity(
        tableName = "ref", // Table name for the cross-reference
        primaryKeys = {"movieId", "categoryId"}, // Composite primary key
        indices = {
                @Index(value = {"movieId"}), // Index on movieId for faster lookups
                @Index(value = {"categoryId"}) // Index on categoryId for faster lookups
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Movie.class,
                        parentColumns = "movieId",
                        childColumns = "movieId",
                        onDelete = ForeignKey.CASCADE // Cascade delete when a movie is deleted
                ),
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "categoryId",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.CASCADE // Cascade delete when a category is deleted
                )
        }
)
public class MovieCategoryCrossRef {
    // Movie ID, part of the composite primary key
    public long movieId;


    // Category ID, part of the composite primary key
    @NonNull
    public String categoryId;

    /**
     * Constructor for creating a new MovieCategoryCrossRef instance.
     *
     * @param movieId    ID of the movie
     * @param categoryId ID of the category
     */
    public MovieCategoryCrossRef(long movieId, @NonNull String categoryId) {
        this.movieId = movieId;
        this.categoryId = categoryId;
    }
}
