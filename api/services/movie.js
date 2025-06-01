const Movie = require('../models/movie');
const Category = require('../models/category');
const mongoose = require("mongoose");
const {getCategoryById} = require("./category");
const categoryService = require("./category");
const fs = require("node:fs");
const path = require("node:path");

// generate a new ID for the movie
// the probability of collision is 1/10^10
function generateRandomID() {
    let num = '';
    for (let i = 0; i < 10; i++) {
        const char = Math.round(Math.random() * 9);
        num += char;
    }
    return Number(num);
}


// Get all movies
async function getAllMovies() {
    return Movie.find();
}

// Populate the categories of the movie
async function populateCategories(query) {
    await Movie.populate(query, 'categories');

}

// Get movie by ID
async function getMovieById(id) {
    return await Movie.findById(id).exec();
}

// Check if movie exists by ID
async function movieExistsById(id) {
    return (await getMovieById(id)) != null;
}

// Check if movie exists by title
async function movieExistsByTitle(title) {
    return (await Movie.findOne({title: title})) != null;
}

// Create a new movie in the database
async function createMovie(movieObj) {
    movieObj._id = generateRandomID();
    console.log(movieObj);
    return Movie.create(movieObj);

}

async function deleteMovieCategories(movie) {
    for (const categoryId of movie.categories) {
        await categoryService.deleteMovie(categoryId, movie._id);
    }
}

async function deleteMovieFromUsers(movie) {
    const {getUserByWatchedMovieId, removeWatchedMovie} = require('./user');
    const users = await getUserByWatchedMovieId(movie._id)
    for (const user of users) {
        await removeWatchedMovie(user._id, movie._id);
    }
}

// delete movie by ID
async function deleteMovieById(id) {
    const movie = await getMovieById(id);

    // deleting its categories
    await deleteMovieCategories(movie);

    // delete it from users watched movies
    await deleteMovieFromUsers(movie);

    // delete files(movie, and pic) if exists

    await removeFile(`./public/${movie.video}`);
    await removeFile(`./public/${movie.image}`);
    return Movie.findByIdAndDelete(id);
}

async function removeFile(path) {
    try {
        fs.unlinkSync(path);
    } catch (e) {
        console.log(e);
    }
}

// replace movie id attributes
async function changeMovie(id, attributes) {
    return await Movie.findByIdAndUpdate(id, attributes).exec();
}


// Get movies by category ID
async function getMoviesByCategory(categoryID) {
    return await Movie.find({categories: {$elemMatch: {$eq: categoryID}}}).exec();
}

// Get movie by title
async function getMovieByTitle(title) {
    return await Movie.findOne({title: title}).exec();
}

// Remove category from movie
async function removeCategory(movieId, categoryID) {
    const movie = await getMovieById(movieId);
    await movie.categories.pull(categoryID);
    await movie.save();
}


// Check if category exists in movie by ID
async function categoryExistsById(movieId, categoryId) {
    const movie = await getMovieById(movieId);
    return movie.categories.includes(categoryId);
}

// Add category to movie
async function addCategory(movieId, categoryID) {
    const movie = await getMovieById(movieId);
    console.log(movie);
    movie.categories.push(categoryID);
    console.log(movie);
    console.log(categoryID);

    await movie.save();
}

// Search movies by title, category name, category ID
async function searchMovies(query) {

    // const mongo_id = new mongoose.Types.ObjectId(query);
    const matchingCategories = await Category.find({
        $or: [
            {title: {$regex: query, $options: 'i'}},  // Search in category title
        ],
    }).select('_id');

    return Movie.find({
        $or: [
            {title: {$regex: query, $options: 'i'}}, // Search in movie title
            {categories: {$in: matchingCategories.map((c) => c._id)}}, // Match by category IDs
        ],
    }).populate('categories');
}

async function validateCategories(categories) {
    const categoryExistsResults = await Promise.all(
        categories.map(category => categoryService.categoryExists(category))
    );
    return !categoryExistsResults.includes(false);
}

async function updateMovieCategories(movie, categories) {
    // Remove the movie from old categories
    await Promise.all(movie.categories.map(category => categoryService.deleteMovie(category, movie._id)));

    // Add the movie to new categories
    await Promise.all(categories.map(category => categoryService.addMovie(category, movie._id)));
}

async function moveFile(oldPath, newPath) {
    if (!oldPath) return null;
    try {
        console.log("new: " + newPath + "  old:  " + oldPath);
        await fs.promises.rename(oldPath, newPath);
        return newPath.replace("./public", "");
    } catch (error) {
        throw new Error(`File renaming error: ${error.message}`);
    }
}

async function handleImageAddition(imagePath, movieId) {
    let imageNewPath = "";
    if (imagePath !== null) {
        imageNewPath = await moveFile(imagePath, `./public/moviePosters/${movieId}${path.extname(imagePath)}`);
    }
    return imageNewPath;
}

async function handleVideoAddition(videoPath, movieId) {
    let videoNewPath = "";
    if (videoPath !== null) {
        videoNewPath = await moveFile(videoPath, `./public/movieVideos/${movieId}${path.extname(videoPath)}`);
    }
    return videoNewPath;
}


async function getRandomMovie() {
    const movies = await getAllMovies();

    if (!movies || movies.length === 0) {
        throw new Error("No movies available");
    }

    const randomIndex = Math.floor(Math.random() * movies.length);

    return movies[randomIndex];
}




module.exports = {
    moveFile,
    updateMovieCategories,
    validateCategories,
    getAllMovies,
    searchMovies,
    movieExistsById,
    getMovieById,
    createMovie,
    deleteMovieById,
    movieExistsByTitle,
    handleImageAddition,
    handleVideoAddition,
    updateMovie: changeMovie,
    getMoviesByCategory,
    getMovieByTitle,
    populateCategories,
    removeCategory,
    addCategory,
    removeFile,
    categoryExistsById,
    getRandomMovie
}

