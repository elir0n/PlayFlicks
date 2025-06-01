package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Category entity used for Room database and Gson serialization.
 */
@Entity(tableName = "categories")
public class Category {

    /**
     * Unique identifier for the category.
     */
    @PrimaryKey
    @SerializedName("id")
    @NonNull
    @ColumnInfo(name = "categoryId")
    private String categoryId;

    /**
     * Title of the category.
     */
    @SerializedName("title")
    @ColumnInfo(name = "title")
    private String title;

    /**
     * Indicates whether the category is promoted.
     */
    @SerializedName("promoted")
    @ColumnInfo(name = "promoted")
    private boolean promoted;

    /**
     * Constructor for Category.
     *
     * @param categoryId Unique category identifier.
     * @param title      Name of the category.
     * @param promoted   Promotion status of the category.
     */
    public Category(@NonNull String categoryId, String title, boolean promoted) {
        this.categoryId = categoryId;
        this.title = title;
        this.promoted = promoted;
    }

    /**
     * Gets the category ID
     *
     * @return category id
     */
    @NonNull
    public String getCategoryId() {
        return categoryId;
    }


    /**
     * Sets the category ID
     *
     * @param categoryId category id
     */
    public void setCategoryId(@NonNull String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the title
     *
     * @return category title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title
     *
     * @param title category title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Checks if the category is promoted
     *
     * @return is category promoted
     */
    public boolean isPromoted() {
        return promoted;
    }
}
