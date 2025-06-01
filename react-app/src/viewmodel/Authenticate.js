const authenticateModel = require("../model/Authenticate");

async function getToken(name, password) {
    const response = await authenticateModel.getToken(name, password);
    if (!response) {
        return null;
    }

    if (!response.ok) {
        return null;
    }

    return await response.json();
}

async function isAuthenticated(token) {
    const response = await authenticateModel.isAuthenticated(token);

    if (!response) {
        return false;
    }

    return !(response.status === 401 || response.status === 403);
}


async function createUser(name, password, image, displayName) {
    const response = await authenticateModel.createUser(name, password, image, displayName);

    if (!response) {
        return {success: false, message: "api server is down"};
    }

    if (response.status === 409) {
        return {success: false, message: "user already exists"};
    }

    return {success: true, message: await response.json()};
}


export {isAuthenticated, getToken, createUser};