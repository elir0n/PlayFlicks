const userService = require('../services/user');
const movieService = require('../services/movie');
const fs = require('fs');
const {getUserByNameAndPassword} = require("../services/user");
const path = require("node:path");
const recommendService = require("../services/recommend");
const {removeFile} = require("../services/movie");

// Handle creating a new user
async function createUser(req, res) {
    const imagePath = req.file ? req.file.path : "";
    try {
        const {name, password} = req.body; // Expecting JSON data in the request body
        if (!name || !password) {
            await removeFile(imagePath);
            return res.status(400).json('Name and password are required');
        }

        // if a user is null, he or she already exists
        if (await userService.userExistsByName(name)) {
            await removeFile(imagePath);
            return res.status(409).json('User already exists');
        }


        await userService.createUser({...req.body, image: imagePath});

        const userId = (await getUserByNameAndPassword(name, password))._id;
        const fileExt = path.extname(imagePath);
        const newPath = `./public/profileImages/${userId}${fileExt}`;

        if (!imagePath) {
            return res.status(201).json('User created successfully');

        }
        fs.rename(imagePath, newPath, (err) => {
            if (err)
                console.log(err);
        });

        const webPath = `/profileImages/${userId}${fileExt}`

        await userService.updateUser(userId, {image: webPath});

        res.status(201).json('User created successfully');
    } catch
        (err) {
        await removeFile(imagePath);
        res.status(500).json(`User created failed : ${err}`);
    }
}

async function saveUserImage(req, res, next) {

}


// Handle getting user details by ID
async function getUserById(req, res) {
    try {
        const {id} = req.params; // Extract user ID from URL
        const user = await userService.getUserById(id);

        if (!user) {
            return res.status(404).json('User not found');
        }

        await userService.populateWatchedMovies(user);

        console.log(user);

        res.status(200).send(JSON.stringify(
            {
                id: user._id,
                name: user.name,
                password: user.password,
                emailAddress: user.emailAddress,
                mobilePhone: user.mobilePhone,
                image: user.image,
                watchedMovies: user.watchedMovies.map(movie => movie.title)
            }, null, 4));

    } catch (err) {
        res.status(500).json(`Getting user failed : ${err}`);
    }
}


// add watched movie to user
async function addMovieWatched(req, res) {
    try {
        const id = req.user.id;
        const user = await userService.getUserById(id);

        const movieId = Number(req.params.id);
        if (!user) {
            return res.status(404).json('User not found');
        }

        if (!await movieService.movieExistsById(movieId)) {
            return res.status(404).json('Movie not found');

        }

        if (await userService.didUserWatchMovie(id, movieId)) {
            return res.status(404).json('user already watched this movie');
        }

        let output = await recommendService.addUser(id, movieId);

        console.log(output);

        if (output === 404) {
            output = await recommendService.updateUser(id, movieId);
            console.log(output);
            if (output === 404) {
                return res.status(404).json("error adding to recommend server");
            }
        }

        // add the movie to the user
        await userService.addMovieWatched(id, movieId);

        res.status(200).json("watched movie added successfully");
    } catch (error) {
        res.status(500).json(`error: ${error}`);
    }
}

module.exports = {createUser, addMovieWatched, getUserById};