FROM node

RUN apt-get update && apt-get install -y cmake


WORKDIR /app
COPY /routes/ /app/routes/
COPY /config/docker.env /app/config/.env
COPY /controllers/ /app/controllers/
COPY /services/ /app/services/
COPY /models/ /app/models/

RUN mkdir -p /app/public/moviePosters
RUN mkdir -p /app/public/movieVideos
RUN mkdir -p /app/public/profileImages
RUN mkdir -p  /app/uploads

COPY /app.js /app/app.js

COPY /package.json /app/package.json
COPY /package-lock.json /app/package-lock.json

RUN npm install

CMD ["node", "app.js"]
