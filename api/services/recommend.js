const net = require('net');
require('dotenv').config({path: './config/.env'});

// function to send data to the recommended server
async function sendToServer(command) {
    return new Promise((resolve, reject) => {
        // create the socket and send data to the recommend server
        const socket = net.createConnection({
            port: Number(process.env.RECOMMEND_SERVER_PORT),
            host: process.env.RECOMMEND_SERVER_IP
        }, () => {
            console.log('server listening on port 8096');
        }).on('error', () => {
            reject('err server listening on port 8096');
        }).on('connect', () => {
            socket.write(command);
        }).on('data', (data) => {
            console.log(command);
            console.log('Received data', data.toString());
            resolve(data.toString());
            socket.destroy();
        });
    });
}

// the code returned by the server


function getResponseCode(response) {
    console.log(response);
    const lines = response.split('\n');
    const outputs = lines[0].split(' ');
    console.log(outputs[0]);
    return Number(outputs[0])
}


// handle the data sending, resolve=true, if wasn't a problem connecting to server.
function getGetRecommendation(response) {
    console.log(response);
    // send the string to the server via TCP socket
    const lines = response.split('\n');

    if (lines.length === 3) {
        return lines[2]; //the output is the line, for GET
    }
    return null;
}

// function to add a user to the recommended server
async function addUser(userId, movieId) {
    return getResponseCode(await sendToServer(`POST ${userId} ${movieId}`));
}

// function to update a user in the recommended server
async function updateUser(userId, movieId) {
    return getResponseCode(await sendToServer(`PATCH ${userId} ${movieId}`));
}

// function to delete a movie from the recommended server
async function deleteMovie(userId, movieId) {
    return getResponseCode(await sendToServer(`DELETE ${userId} ${movieId}`));
}

// function to get a recommendation from the recommended server
async function getRecommendation(userId, movieId) {
    return await sendToServer(`GET ${userId} ${movieId}`);

}

module.exports = {
    getGetRecommendation,
    getResponseCode,
    addUser,
    updateUser,
    deleteMovie,
    getRecommendation
}