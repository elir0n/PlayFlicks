const movieService = require('../services/movie');
const recommendService = require('../services/recommend');
const categoryService = require('../services/category');
const userService = require('../services/user');
const {removeFile} = require("../services/movie");

// Handle getting all movies
async function getAllMovies(req, res) {
    const movies = await movieService.getAllMovies();
    await movieService.populateCategories(movies);

    const res1 = (JSON.stringify(movies.map((movie) => ({
        id: movie._id,
        title: movie.title,
        image: movie.image,
        description: movie.description,
        categories: movie.categories.map(category => category._id),
        video: movie.video,
    })), null, 4));
    return res.status(200).send(res1);
}

// Handle getting a movie by ID
async function getMovieById(req, res) {
    try {
        const {id} = req.params; // Extract movie ID from URL

        const movie = await movieService.getMovieById(id);
        await movieService.populateCategories(movie);
        if (movie) {
            res.status(200).send(JSON.stringify({
                id: movie._id,
                title: movie.title,
                categories: movie.categories.map(category => category.title),
                image: movie.image,
                video: movie.video,
                description: movie.description
            }, null, 4));
        } else {
            res.status(404).json('Movie not found');
        }
    } catch (error) {
        return res.status(400).json(`error: ${error}`);
    }
}


// Handle creating a new movie
async function createMovie(req, res) {
    let {title, description} = req.body;
    let categories;
    try {
        categories = JSON.parse(req.body.categories);
    } catch {
        return res.status(400).json("Invalid categories format");

    }
    const {imageFile, videoFile} = {
        imageFile: req.files?.at(0) || null,
        videoFile: req.files?.at(1) || null
    };

    const imagePath = imageFile?.path || null;
    const videoPath = videoFile?.path || null;

    const cleanUp = () => {
        movieService.removeFile(imagePath);
        movieService.removeFile(videoPath);
    };

    if (!title) {
        cleanUp();
        return res.status(400).json('Title is required');
    }
    if (description === null) {
        description = "";
    }

    try {
        if (await movieService.movieExistsByTitle(title)) {
            cleanUp();
            return res.status(400).json('Movie already exists');
        }

        if (!(await categoryService.areCategoriesValid(categories))) {
            cleanUp();
            return res.status(400).json("Some categories don't exist");
        }

        const movie = await movieService.createMovie({title, description, categories});

        await categoryService.addMovieToCategories(categories, movie._id);

        const imageNewPath = await movieService.handleImageAddition(imagePath, movie._id);
        const videoNewPath = await movieService.handleVideoAddition(videoPath, movie._id);

        await movieService.updateMovie(movie._id, {image: imageNewPath, video: videoNewPath});

        res.status(201).json('Movie created successfully');
    } catch (error) {
        cleanUp();
        res.status(400).json(`Error: ${error.message}`);
    }
}


// Handle deleting a movie by ID
async function deleteMovieById(req, res) {
    const {id} = req.params; // Extract movie ID from URL
    try {
        if (!await movieService.movieExistsById(id)) {
            return res.status(404).json('Movie not found');
        }

        await movieService.deleteMovieById(id);


        res.status(200).json('Movie deleted successfully');


    } catch (error) {
        return res.status(400).json(`error: ${error}`);
    }
}

// Handle updating a movie by ID
async function updateMovieById(req, res) {
    const {id} = req.params;
    let {title, description} = req.body;
    const imageFile = req.files?.at(0);
    const videoFile = req.files?.at(1);
    const imagePath = imageFile?.path || null;
    const videoPath = videoFile?.path || null;

    const cleanUp = () => {
        removeFile(imagePath);
        removeFile(videoPath);
    };
    try {
        let categories;
        try {
            categories = JSON.parse(req.body.categories);
        } catch {
            return res.status(400).json("Invalid categories format");
        }

        if (await movieService.movieExistsByTitle(title) && (await movieService.getMovieById(id)).title !== title) {
            cleanUp();
            return res.status(400).json("Movie title already exists");
        }

        if (!await movieService.movieExistsById(id)) {
            cleanUp();
            return res.status(400).json("Movie not found");
        }

        if (!await movieService.validateCategories(categories)) {
            cleanUp();
            return res.status(400).json("Some categories don't exist");
        }
        if (description === null) {
            description = "";

        }

        let movie = await movieService.getMovieById(id);
        await movieService.updateMovieCategories(movie, categories);

        let imageNewPath = movie.image;
        if (imagePath != null) {
            imageNewPath = await movieService.handleImageAddition(imagePath, movie._id);
        }
        let videoNewPath = movie.video;
        if (videoPath != null) {
            videoNewPath = await movieService.handleVideoAddition(videoPath, movie._id);
        }


        await movieService.updateMovie(movie._id, {
            title,
            description,
            categories,
            image: imageNewPath,
            video: videoNewPath
        });

        res.status(200).json("Movie replaced successfully");
    } catch (error) {
        console.log(error)
        cleanUp()
        res.status(500).json(`Error: ${error.message}`);
    }
}


//get all movies from a specific category
async function getMoviesByCategory(req, res) {
    try {
        const {id} = req.params; // Extract category ID from the request parameters
        const movies = (await movieService.getMoviesByCategory(id));
        res.status(200).json(JSON.stringify(movies.map(movie => ({
            id: movie._id, title: movie.title, movies: movie.categories.map((category) => category.title),
        })), null, 4));
    } catch (error) {
        res.status(500).json(`error: ${error}`);
    }
}

// return 20 random movies from promoted categories and 20 last watched movies
async function getMoviesByCategories(req, res) {
    try {
        const userID = req.user.id;
        const user = await userService.getUserById(userID);
        if (!user) {
            return res.status(404).json('User not found');
        }

        // Fetch promoted categories
        const promotedCategories = await categoryService.getAllPromotedCategories();
        await categoryService.populateMovies(promotedCategories);

        const result = {};

        // Fetch movies for each promoted category
        for (const category of promotedCategories) {
            const allMoviesInCategory = await movieService.getMoviesByCategory(category._id);

            // Get IDs of watched movies for the user
            const watchedMovieIds = new Set((await userService.getWatchedMoviesIds(userID)));

            // Filter out movies that the user has already watched
            const unseenMovies = allMoviesInCategory.filter(movie => !watchedMovieIds.has(movie._id));
            // Select up to 20 random unseen movies
            const movies = unseenMovies.sort(() => Math.random() - 0.5).slice(0, 20);
            result[category.title] = movies.map(movie => ({
                id: movie._id,
                video: movie.video,
                description: movie.description,
                title: movie.title,
                image: movie.image,
            }));
        }

        // Fetch the last 20 movies the user has watched
        // Shuffle the order
        // Add a special category for watched movies
        const array = await userService.getWatchedMovies(userID);

        if (array && array.size !== 0) {
            const randomWatched = array
                .slice(-20)
                .sort(() => Math.random() - 0.5);

            result['Watched Movies'] = randomWatched.map(movie => ({
                id: movie._id,
                video: movie.video,
                title: movie.title,
                description: movie.description,
                image: movie.image,
            }));
        }

        if (result.length === 0) {
            return res.status(200).json();
        }

        // Return the response
        res.status(200).send(JSON.stringify(result, null, 4));
    } catch (error) {
        res.status(500).json(`error: ${error}`);
    }
}


// Add a category to a movie
async function addCategory(req, res) {
    try {
        const {title} = req.body;
        const id = req.params.id;

        if (!title) {
            return res.status(400).json('invalid title or id provided');
        }

        const category = await categoryService.getCategoryByTitle(title);
        if (!category) {
            return res.status(404).json("category doesn't exists");
        }
        if (!await movieService.movieExistsById(id)) {
            return res.status(404).json("movie doesn't exists");
        }
        if (await movieService.categoryExistsById(id, category._id)) {
            return res.status(400).json('category already exists');
        }

        // Add the category to the movie
        await movieService.addCategory(id, category._id);
        await categoryService.addMovie(category._id, id);

        res.status(200).json('category added successfully');
    } catch (error) {
        res.status(500).json(`error: ${error}`);
    }
}

// Add a movie to a category
async function addMovieToCategory(req, res) {
    try {
        const movieId = req.body.id;

        const categoryId = req.params.id;

        if (!await movieService.movieExistsById(movieId) || !await categoryService.categoryExists(categoryId)) {
            return res.status(400).json("category or movies doesn't exist");
        }

        // Add the movie to the category
        await categoryService.addMovie(movieId, categoryId);
        await movieService.addCategory(categoryId, movieId);
    } catch (error) {
        res.status(500).json(`
        error: $
        {
            error
        }
        `);
    }
}

// Handle searching movies by query
async function searchMovies(req, res) {
    try {
        const {query} = req.params; // Extract the query from the URL
        if (!query) {
            return res.status(400).json('Query parameter is required');
        }

        // Perform a case-insensitive search across multiple fields
        const movies = await movieService.searchMovies(query);
        if (movies.length === 0) {
            return res.status(404).json('No movies found');
        }
        res.status(200).send(JSON.stringify(movies.map(movie => ({
            id: movie._id,
            title: movie.title,
            categories: movie.categories.map((category) => category.title),
        })), null, 4));
    } catch (error) {
        res.status(500).json(`
        error: ${error}`);
    }
}

async function getRandomMovie(req, res) {
    try {
      const randomMovie = await movieService.getRandomMovie();
      
      if (!randomMovie) {
        return res.status(404).json({ message: 'No movies found in the database' });
      }
      
      res.status(200).json(randomMovie);
    } catch (error) {
      console.error('Error fetching random movie:', error);
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

async function getMovieByTitle(req, res) {
    try {
        const title = req.params.title;
        const movie = await movieService.getMovieByTitle(title);

        if (!movie) {
            return res.status(404).json("Movie not found");
        }

        res.status(200).json({
            id: movie._id,
            title: movie.title,
            categories: movie.categories.map(category => category.title),
            image: movie.image,
            video: movie.video,
            description: movie.description,
        });
    } catch (error) {
        res.status(500).json(`error: ${error}`);

    }
}


module.exports = {
    addCategory,
    getMovieById,
    createMovie,
    deleteMovieById,
    changeMovieById: updateMovieById,
    getMoviesByCategories,
    searchMovies,
    getMoviesByCategory,
    getAllMovies,
    addMovieToCategory,
    getRandomMovie,
    getMovieByTitle
};