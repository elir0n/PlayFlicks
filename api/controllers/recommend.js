const recommendService = require('../services/recommend');
const userService = require('../services/user');
const movieService = require("../services/movie");

// return the movie to recommend for the user based on the movieID
async function getMoviesRecommendation(req, res) {
    try {
        const id = req.user.id;

        if (isNaN(id)) {
            return res.status(400).json("id isn't a number");
        }

        if (!await movieService.getMovieById(req.params.id)) {
            res.json("movie doesn't exist").status(404);

        }

        // there is only one func
        await recommendService.getRecommendation(id, req.params.id).then(output => {
            res.json(recommendService.getGetRecommendation(output)).status(recommendService.getResponseCode(output));
        });
    } catch (error) {
        res.status(404).json(`error: ${error}`);
    }
}


// adding the movie to the userid, and creating if he doesn't exist
async function addMoviesToRecommendationServer(req, res) {
    try {
        const id = req.user.id;
        if (isNaN(id)) {
            return res.status(400).json("id isn't a number");
        }

        if (!await userService.getUserById(id)) {
            return res.status(404).json("user does not exist");
        }
        // needs to wait to check if the status code =404, the user already exists

        await recommendService.addUser(id, req.params.id).then(output => {
            res.status(recommendService.getResponseCode(output)).end();
        });

        console.error("status" + res.statusCode);

        if (res.statusCode !== 404) {
            res.end();
            return;
        }
        // user already exists trying PATCH
        await recommendService.updateUser(id, req.params.id).then(output => {
            res.status(recommendService.getResponseCode(output)).end();
        });

    } catch (error) {
        res.status(404).json('server connection error');
    }
}

module.exports = {addMoviesToRecommendationServer, getMoviesRecommendation};