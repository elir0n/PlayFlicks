const mongoose = require('mongoose');

const Schema = mongoose.Schema;

// Movie schema
const Movie = new Schema({
        _id: {
            type: Number,
            required: true
        }, title: {
            type: String,
            required: true,
        }, description: {
            type: String,
            default: '',
        }, categories: {
            type: [
                {
                    type: Schema.Types.ObjectId,
                    ref: 'Category',
                }
            ],
            default: [],
        },
        image:
            {
                type: String,
                default:
                    ""
            }
        ,
        video:
            {
                type: String,
                default:
                    ""
            }
    })
;

module.exports = mongoose.model('Movie', Movie, 'movies');