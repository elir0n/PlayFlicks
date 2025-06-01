const mongoose = require('mongoose');

const Schema = mongoose.Schema;

// Category schema
const category = new Schema({
    title: {
        type: String,
        required: true,
    }, promoted: {
        type: Boolean,
        required: true,
    }, movies: {
        type: [
            {type: Number, ref: 'Movie'},
        ]
    }
});


module.exports = mongoose.model('Category', category, 'categories');