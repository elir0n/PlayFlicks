const express = require('express');

const router = express.Router();

const tokenController = require('../controllers/token');

// without any params goes to getUserID

router.route('/').post(tokenController.getUserId);
router.route('/getToken').post(tokenController.getToken);
router.route('/auth').get(tokenController.authenticateToken);

module.exports = router;