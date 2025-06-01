async function addMovieToCategories(categories, id) {
    await Promise.all(categories.map(category => addMovie(category, id)));
}

async function areCategoriesValid(categories) {
    const categoryCheckResults = await Promise.all(
        categories.map(category => categoryExists(category))
    );
    return !categoryCheckResults.includes(false);
}

const Category = require("../models/category");

// getting all categories
async function getAllCategories() {
    return await Category.find().exec();
}


// creating a new category with title and promotion
async function createCategory(categoryObj) {
    return Category.create(categoryObj);
}


// get the category by id
async function getCategoryById(id) {
    return Category.findById(id).exec();
}

// see if category exists
async function categoryExists(id) {
    return (await getCategoryById(id)) != null;
}

// updating the category
async function updateCategory(id, updateOptions) {
    await Category.findByIdAndUpdate(id, updateOptions);
}


// add a movie to the movies
async function addMovie(categoryID, movieID) {
    const category = await getCategoryById(categoryID);
    category.movies.push(movieID);
    await category.save();
}


// delete the specific category id
async function deleteCategory(id) {
    await Category.deleteOne({_id: id}).exec();
}


// delete the movie from movies
async function deleteMovie(categoryID, movieID) {
    const category = await getCategoryById(categoryID);
    await category.movies.pull(movieID);
    await category.save();
}

async function movieExistsById(categoryId, movieId) {
    const category = await getCategoryById(categoryId);
    return category.movies.includes(movieId);
}


// get the category by its title
async function getCategoryByTitle(title) {
    return await Category.findOne({title: title}).exec();
}

async function categoryExistsByTitle(title) {
    const category = await Category.findOne({title: title});
    return category != null;

}

// add movies to the category
async function addMovies(id, movies) {
    const category = await getCategoryById(id);
    for (const movie of movies) {
        category.movies.push(movie);
    }
    await category.save();
}

// get all categories that are promoted
async function getAllPromotedCategories() {
    return await Category.find({promoted: true}).exec();
}

// populate the movies in the category
async function populateMovies(query) {
    await Category.populate(query, {path: 'movies'});
}

module.exports = {
    createCategory,
    getAllCategories,
    deleteCategory,
    getCategoryById,
    addMovie,
    deleteMovie,
    movieExistsById,
    populateMovies,
    categoryExists,
    categoryExistsByTitle,
    addMovies,
    updateCategory,
    getCategoryByTitle,
    getAllPromotedCategories,
    addMovieToCategories,
    areCategoriesValid,
};

