const express = require('express');
const path = require('path');
const fs = require('fs');
require('dotenv').config({path: path.resolve(__dirname, './config/.env')});
const mongoose = require("mongoose");

const app = express();

const cors = require("cors");
app.use(express.json());

app.use(cors())
app.use('/profileImages', express.static(path.resolve(__dirname, 'public/profileImages')));
app.use('/moviePosters', express.static(path.resolve(__dirname, 'public/moviePosters')));


app.get('/movieVideos/:movie', (req, res) => {
    // Get the video’s actual location and size
    const videoPath = path.join(__dirname, 'public/movieVideos', req.params.movie);
    const fileSize = fs.statSync(videoPath).size;

// Extract the range requested by the browser
    let range = req.headers.range;
    if (!range) range = 'range=0-';
    const parts = range.substring(6).split('-');
    const start = parseInt(parts[0]);
    const chunk_size = 10 ** 7; // 10MB
    const end = Math.min(start + chunk_size, fileSize - 1);
    const file = fs.createReadStream(videoPath, {start, end});
// Stream requested chunk
    const contentLength = end - start + 1;
    const head = {
        'Content-Range': `bytes ${start}-${end}/${fileSize}`,
        'Accept-Ranges': 'bytes',
        'Content-Length': contentLength,
        'Content-Type': 'video/mp4',
    };
    res.writeHead(206, head);
    file.pipe(res);
})

const apiRouter = require('./routes/api');

// gets the mongodb url ad connects to it.

console.log(process.env.MONGO_URL)

mongoose.connect(process.env.MONGO_URL).then(() => {
    console.log("MongoDB Connected");
}).catch(err => {
    console.log(`couldn't connect to MongoDB server err: ${err}`);
});

// go the api route
app.use('/api', apiRouter);

app.listen(3001);