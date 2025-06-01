const express = require('express');
const multer = require('multer');
const router = express.Router();

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, './uploads');
    },
    filename: (req, file, cb) => {
        cb(null, Date.now() + file.originalname);
    }
})


const upload = multer({storage: storage});


const recommendController = require('../controllers/recommend');
const movieController = require('../controllers/movies');
const {authenticateToken} = require("../controllers/token");


// First, the specific routes with fixed paths
router.route('/get/allMovies').get(authenticateToken, movieController.getAllMovies);
router.route('/random-movie').get(authenticateToken, movieController.getRandomMovie);
router.route('/search/:query').get(authenticateToken, movieController.searchMovies);
router.route('/getTitle/:title').get(authenticateToken, movieController.getMovieByTitle);

// Then, the routes with :id but with additional path segments
router.route('/:id/recommend')
    .get(authenticateToken, recommendController.getMoviesRecommendation)
    .post(authenticateToken, recommendController.addMoviesToRecommendationServer);
router.route('/:id/category/add').post(authenticateToken, movieController.addCategory);

// Finally, the most general :id routes
router.route('/:id')
    .get(authenticateToken, movieController.getMovieById)
    .delete(authenticateToken, movieController.deleteMovieById)
    .put(upload.array("image-video", 2), movieController.changeMovieById);

// The root route
router.route('/')
    .post(authenticateToken, upload.array("image-video", 2), movieController.createMovie)
    .get(authenticateToken, movieController.getMoviesByCategories);


module.exports = router;