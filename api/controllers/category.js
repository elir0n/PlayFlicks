const categoryService = require('../services/category');
const movieService = require('../services/movie');

// getting all categories
async function getAllCategories(req, res) {
    const categories = await categoryService.getAllCategories();
    await categoryService.populateMovies(categories);

    res.status(200).send(JSON.stringify(
        categories.map(category => ({
            id: category._id,
            title: category.title,
            promoted: category.promoted,
            movies: category.movies ? category.movies.map((movie) => movie.title) : [],
        })),
        null, 4)
    );
}

// create a new category, with title, and promoted val
async function createCategory(req, res) {
    try {
        const categoriesData = req.body;

        if (categoriesData.promoted === undefined || !categoriesData.title) {
            return res.status(400).json("Promoted and title is required");
        }

        if (await categoryService.getCategoryByTitle(categoriesData.title) != null) {
            return res.status(400).json("category already exists");
        }
        // Validate movies array
        if (Array.isArray(categoriesData.movies)) {
            for (const movie of categoriesData.movies) {
                if (!await movieService.movieExistsById(movie)) {
                    return res.status(400).json("some movies doesn't exist");
                }
            }
        }

        const category = await categoryService.createCategory(categoriesData);

        console.log(category);
        if (categoriesData.movies) {
            for (const movie of categoriesData.movies) {
                console.log(movie);
                console.log(category._id);
                await movieService.addCategory(movie, category._id);
            }
        }

        res.status(201).json("category created");
    } catch (error) {
        return res.status(400).json(`error: ${error}`);
    }
}


// get a specific category
async function getCategory(req, res) {
    try {
        const id = req.params.id;

        const category = await categoryService.getCategoryById(id);

        if (!category) {
            return res.status(404).json("error : category not found");
        }
        res.status(200).send(JSON.stringify({
            id: category._id,
            title: category.title,
            promoted: category.promoted,
            movies: category.movies.map((movie) => movie.title),
        }, null, 4));
    } catch (error) {
        return res.status(400).json(`error: ${error}`);
    }
}


// updating categories, fields that are null, or they don't exist will not change
async function updateCategory(req, res) {
    const id = req.params.id;

    const categoryObj = req.body;

    // Checking if there is no value to change
    if (Object.keys(req.body).length === 0) {
        return res.status(400).json("error : no updates requested");
    }
    try {
        // Validate title
        if (await categoryService.categoryExistsByTitle(categoryObj.title) && (await categoryService.getCategoryById(id)).title !== categoryObj.title) {
            return res.status(400).json("category title already exists");
        }

        // Perform the update
        await categoryService.updateCategory(id, categoryObj);
        res.status(204).json("update was successful");
    } catch (error) {
        return res.status(400).json(`error: ${error}`);
    }
}


// deleting a specific category
async function deleteCategory(req, res) {
    try {
        const id = req.params.id;
        if (!await categoryService.getCategoryById(id)) {
            return res.status(404).json("error : category not found");
        }
        const category = (await categoryService.getCategoryById(id));
        for (const movieId of category.movies) {
            await movieService.removeCategory(movieId, id);
            const movie = await movieService.getMovieById(movieId)
            if (movie.categories.length === 0) {
                await movieService.deleteMovieById(movieId);
            }
        }

        await categoryService.deleteCategory(id);
        res.status(204).json("category deleted");
    } catch (error) {
        return res.status(400).json(`error: ${error}`);
    }
}

async function deleteMovieFromCategory(req, res) {
    try {
        const movieId = req.params.movieId;
        const categoryId = req.params.categoryId;

        if (!await movieService.movieExistsById(movieId)) {
            return res.status(404).json("error : movie not found");
        }
        if (!await categoryService.categoryExists(categoryId)) {
            return res.status(404).json("error :category not found");
        }

        await categoryService.deleteMovie(categoryId, movieId);
        await movieService.removeCategory(movieId, categoryId);
        const movie = await movieService.getMovieById(movieId);

        if (movie.categories.length === 0) {
            await movieService.deleteMovieById(movieId);
        }
        res.status(204).json("Movie removed successfully");
    } catch (e) {
        console.error(e);
        res.status(400).json(`error: ${e}`);
    }

}


async function getCategoryByTitle(req, res) {
    try {
        console.log('Fetching category with title:', req.params.title);
        const title = req.params.title;
        const category = await categoryService.getCategoryByTitle(title);

        if (!category) {
            return res.status(404).json("error : category not found");
        }

        res.status(200).send(JSON.stringify({
            _id: category._id,
            title: category.title,
            promoted: category.promoted,
            movies: category.movies.map((movie) => movie.title),
        }, null, 4));
    } catch (error) {
        return res.status(400).json(`error: ${error}`);
    }
}

module.exports = {
    getAllCategories,
    createCategory,
    getCategory,
    updateCategory,
    deleteMovieFromCategory,
    deleteCategory,
    getCategoryByTitle,
}
