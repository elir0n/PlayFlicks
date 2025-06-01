package com.example.myapplication.api;

import android.content.Context;
import android.net.TrafficStats;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.model.Movie;
import com.example.myapplication.utils.TokenManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * ApiService interface defines the REST API endpoints for interacting with the backend.
 */
public interface ApiService {


    /**
     * Registering user
     *
     * @param image       user image
     * @param name        username
     * @param password    user password
     * @param displayName user display name
     * @return Response of register user
     */
    @Multipart
    @POST("api/users")
    Call<ResponseBody> registerUser(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("password") RequestBody password,
            @Part("displayName") RequestBody displayName
    );

    /**
     * Getting the user token
     *
     * @param loginBody the login body data with username and password
     * @return Response body with token
     */
    @POST("api/tokens/getToken")
    Call<ResponseBody> getToken(@Body RequestBody loginBody);

    /**
     * Get movies by categories
     *
     * @return movies by categories
     */
    @GET("api/movies")
    Call<Map<String, List<Movie>>> getCategoriesAndMovies();

    /**
     * Get All Categories
     *
     * @return all categories
     */
    @GET("api/categories")
    Call<ResponseBody> getAllCategories();

    /**
     * Get all Movies
     *
     * @return All Movies
     */
    @GET("api/movies/get/allMovies")
    Call<ResponseBody> getAllMovies();

    /**
     * Get all recommended movies by movie id
     *
     * @param movieId movie id
     * @return all of the recommended movies
     */
    @GET("api/movies/{id}/recommend")
    Call<ResponseBody> getRecommendedMovies(@Path("id") long movieId);

    /**
     * Creating a new category
     *
     * @param body a body wit the category details
     * @return the api response
     */
    @POST("api/categories")
    Call<ResponseBody> createNewCategory(@Body RequestBody body);

    /**
     * Edit the category data
     *
     * @param id   the category id
     * @param body the category data  to change
     * @return the api response
     */
    @PATCH("api/categories/{id}")
    Call<ResponseBody> editCategory(@Path("id") String id, @Body RequestBody body);

    /**
     * Delete the category
     *
     * @param categoryId the category id
     * @return the api response
     */
    @DELETE("api/categories/{id}")
    Call<ResponseBody> deleteCategory(@Path("id") String categoryId);

    /**
     * Deleting movie from on of its categories
     *
     * @param movieId    movie id
     * @param categoryId category id
     * @return api response
     */
    @DELETE("api/categories/{categoryId}/{movieId}")
    Call<ResponseBody> deleteMovieFromCategory(@Path("movieId") long movieId, @Path("categoryId") String categoryId);


    /**
     * Creating a new movie
     *
     * @param image       movie poster image
     * @param video       movie video
     * @param title       movie title
     * @param description movie description
     * @param categories  movie categories
     * @return api server response
     */
    @Multipart
    @POST("api/movies")
    Call<ResponseBody> createNewMovie(
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("categories") RequestBody categories
    );

    /**
     * Editing movie
     *
     * @param movieId     movie id
     * @param image       movie poster image
     * @param video       movie video
     * @param title       movie title
     * @param description movie description
     * @param categories  movie categories
     * @return api server response
     */
    @Multipart
    @PUT("api/movies/{id}")
    Call<ResponseBody> editMovie(
            @Path("id") long movieId,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("categories") RequestBody categories
    );

    /**
     * Adding movie as whatched for current user
     *
     * @param movieId watched movie  id
     * @return api response
     */
    @POST("api/users/{id}/addMovie")
    Call<ResponseBody> addMovieToUserWatchedList(@Path("id") long movieId);

    /**
     * gets all the movies from the query
     *
     * @param query the query filter
     * @return all of the filtered movies, if success, else api response code
     */
    @GET("api/movies/search/{query}")
    Call<ResponseBody> getQueriedMovie(@Path("query") String query);


    /**
     * Creates a singleton instance of the ApiService with Retrofit and OkHttpClient.
     *
     * @param context Application context
     * @return Instance of ApiService
     */
    static ApiService getInstance(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new TrafficStatsInterceptor())
                .addInterceptor(chain -> {
                    String token = TokenManager.getToken(context);
                    if (token != null) {
                        token = token.replace("\"", ""); // Remove extra quotes
                    }
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", token != null ? "Bearer " + token : "")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getApplicationContext().getString(R.string.BASE_URL))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(ApiService.class);
    }


    /**
     * TrafficStatsInterceptor ensures network traffic monitoring using Android TrafficStats.
     */
    class TrafficStatsInterceptor implements Interceptor {
        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            TrafficStats.setThreadStatsTag(12345); // Set unique identifier for tracking
            return chain.proceed(chain.request());
        }
    }
}
