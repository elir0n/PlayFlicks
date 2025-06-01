const User = require('../models/user');
const {populateCategories} = require("./movie");
const multer = require("multer");
const fs = require("fs");
const res = require("express/lib/response");

// Get the user ID by name and password, return null if no user is found
async function getUserByNameAndPassword(name, password) {
    return await User.findOne({name: name, password: password}).exec();
}

// generate a new ID for the user
// the probability of collision is 1/10^10
function generateRandomID() {
    let num = '';
    for (let i = 0; i < 10; i++) {
        const char = Math.round(Math.random() * 9);
        num += char;
    }

    return Number(num);
}

function removeFile(path) {
    try {
        fs.unlinkSync(path);
    } catch (error) {
        console.log(error);
    }
}

async function getUsersByWatchedMovieId(movieId) {
    return User.find({watchedMovies: movieId});
}

async function createUser(user) {
    // Ensure `userId` is always a valid random 10-digit number
    const userId = generateRandomID();
    return await User.create({...user, _id: userId});
}


// Get user by ID
async function getUserById(id) {
    return await User.findById(id).exec();
}

// Check if user exists
async function userExistsByName(name) {
    return (await User.findOne({name: name})) != null;
}

// Check if user exists
async function userExistsById(id) {
    return (await User.findOne({_id: id})) != null;
}

// Check if user watched a movie
async function didUserWatchMovie(userId, movieId) {
    const user = await User.findOne({_id: userId});

    if (!user.watchedMovies) {
        return false;
    }
    return user.watchedMovies.some(watchedMovie => watchedMovie === movieId);
}


// adding watched movie to user
async function addMovieWatched(userId, movieId) {
    const user = await User.findOne({_id: userId}).exec();
    await user.watchedMovies.push(movieId);
    await user.save();
}



// removing watched movie to user
async function removeWatchedMovie(userId, movieId) {
    const {deleteMovie} = require("./recommend");
    const user = await User.findOne({_id: userId}).exec();
    await user.watchedMovies.pull(movieId);
    await user.save();
    await deleteMovie(userId, movieId);
}

// get all watched movies IDs for a user
async function getWatchedMoviesIds(userId) {
    const user = await getUserById(userId);
    return user.watchedMovies;
}

// get all watched movies for a user
async function getWatchedMovies(userId) {
    const user = await getUserById(userId);
    await populateWatchedMovies(user);
    return user.watchedMovies;
}

// updating the user
async function updateUser(userId, user) {
    await User.findOneAndUpdate({_id: userId}, user, {
        new: true
    });
}

// populate the watched movies for a user
async function populateWatchedMovies(query) {
    await User.populate(query, {path: 'watchedMovies'});
}

module.exports = {
    updateUser,
    addMovieWatched,
    getUserByNameAndPassword,
    createUser,
    getUserById,
    userExistsByName,
    getUserByWatchedMovieId: getUsersByWatchedMovieId,
    populateWatchedMovies,
    userExistsById,
    removeWatchedMovie,
    removeFile,
    didUserWatchMovie,
    getWatchedMoviesIds,
    getWatchedMovies,
};