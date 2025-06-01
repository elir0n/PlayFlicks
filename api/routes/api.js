const express = require('express');

const router = express.Router();

// importing the wanted routers
const tokensRouter = require('./tokens');
const moviesRouter = require('./movies');
const userRouter = require('./user');
const categoryRouter = require('./categories');


// based on /api goes to the wanted loc.
router.use('/movies', moviesRouter);
router.use('/tokens', tokensRouter);
router.use('/users', userRouter);
router.use('/categories', categoryRouter);


module.exports = router;