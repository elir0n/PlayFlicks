const userService = require("../services/user");

const jwt = require("jsonwebtoken");

// getting the user id by name and password
async function getUserId(req, res) {

    const {name, password} = req.body;
    if (!name) {
        return res.status(400).json("name is required");
    }

    if (!password) {
        return res.status(400).json("password  is required");
    }

    if (Object.keys(req.body).length > 2) {
        return res.status(400).json("password and name are the only accepted field's");

    }

    const user = await userService.getUserByNameAndPassword(name, password);

    // if the ID = null, then the user doesn't exist.
    if (user == null) {
        res.status(404).json("user name or password are incorrect");
    } else {
        res.status(200).json(user._id);
    }
}


async function getToken(req, res) {
    const {name, password} = req.body;
    const user = await userService.getUserByNameAndPassword(name, password);
    if (user === null) return res.status(404).json("user name or password are incorrect");
    // return res.status(200).json(jwt.sign(user.toObject(), process.env.SECRET_KEY, {expiresIn: "1h"}));
    return res.status(201).json(jwt.sign({
        displayName: user.displayName,
        id: user._id,
        image: user.image,
        role: user.role
    }, process.env.SECRET_KEY,));
}

function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    console.log(token);
    if (token == null)
        return res.sendStatus(401);
    jwt.verify(token, process.env.SECRET_KEY, (err, decoded) => {
        if (err) return res.sendStatus(403);
        req.user = decoded;
        next();
    })
}

module.exports = {authenticateToken, getToken, getUserId};