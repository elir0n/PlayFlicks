const express = require('express');

const router = express.Router();

const categoryController = require('../controllers/category');

const {authenticateToken} = require("../controllers/token");

// routes for categories
router.route('/')
    .get(authenticateToken, categoryController.getAllCategories)
    .post(authenticateToken, categoryController.createCategory);

router.route('/:id')
    .get(authenticateToken, categoryController.getCategory)
    .patch(authenticateToken, categoryController.updateCategory)
    .delete(authenticateToken, categoryController.deleteCategory);


router.route('/getTitle/:title')
    .get(authenticateToken, categoryController.getCategoryByTitle);

router.route('/:categoryId/:movieId')
    .delete(authenticateToken, categoryController.deleteMovieFromCategory);


module.exports = router;