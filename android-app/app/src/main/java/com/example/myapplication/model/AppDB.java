package com.example.myapplication.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Room database class for the application.
 * Defines the database and provides DAOs for data access.
 */
@Database(entities = {Category.class, Movie.class, MovieCategoryCrossRef.class}, version = 25)
public abstract class AppDB extends RoomDatabase {

    // Singleton instance of the database
    private static volatile AppDB INSTANCE;

    /**
     * Abstract methods to get DAOs for database operations.
     */
    public abstract CategoryDao categoryDao();

    public abstract MovieDao movieDao();

    /**
     * Returns the singleton instance of the database.
     * Ensures only one instance of the database is created.
     *
     * @param context Application context
     * @return Instance of AppDB
     */
    public static AppDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDB.class, "categories_db")
                            .fallbackToDestructiveMigration() // Handles version updates by recreating the database
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
