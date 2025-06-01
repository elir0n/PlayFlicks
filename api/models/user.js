const mongoose = require('mongoose');

const Schema = mongoose.Schema;

// User schema
const User = new Schema({
    _id: {
        type: Number,
        required: true,
    }, name: {
        type: String,
        required: true,
    }, password: {
        type: String,
        required: true,
    },
    role: {
        type: String,
        default: "user",
    },
    displayName: {
        type: String,
        default: undefined,
    }, emailAddress: {
        type: String,
        default: undefined,
    }, mobilePhone: {
        type: String,
        default: undefined,
    }, image: {
        type: String,
        default: null // putting a random image from our images
    }, watchedMovies: {
        type: [
            {
                type: Number,
                ref: 'Movie'
            },
        ],
        default: []
    }
});

module.exports = mongoose.model('User', User, 'users');