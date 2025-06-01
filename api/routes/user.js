const express = require('express');

const userController = require('../controllers/user');
const {authenticateToken} = require("../controllers/token");


const multer = require("multer");



const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, './uploads');
    },
    filename: (req, file, cb) => {
        cb(null, Date.now() + file.originalname);
    }
})

const upload = multer({storage: storage});


const router = express.Router();
// Route to create a new user
router.post('/', upload.single('image'), userController.createUser);

// Route to get user details by ID
router.get('/:id', authenticateToken, userController.getUserById);

// Route to delete a user by ID
router.post('/:id/addMovie', authenticateToken, userController.addMovieWatched);


module.exports = router;