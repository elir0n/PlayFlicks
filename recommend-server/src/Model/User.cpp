//
// Created by Lavi Shpaisman on 11/27/2024.
//

#include "User.h"

#include <algorithm>
#include <sstream>

#include <bits/streambuf_iterator.h>

using namespace std;

// user constructor get id and vector of movies.
User::User(const unsigned long long id, const vector<unsigned long long> &movies) {
    this->id = id;
    this->addMovies(movies);
}

// function where its parameter is line where it is formatted: id, movieId1, movieId2, etc.
User::User(std::string &line) {
    // getId
    unsigned long long posSpace = line.find(", ");
    unsigned long long startPos = 0;
    this->id = stoll(line.substr(startPos, posSpace));
    startPos = posSpace + 1;

    // get movies
    vector<unsigned long long> movies;
    while ((posSpace = line.find(", ", posSpace + 1)) != string::npos) {
        movies.push_back(stoll(line.substr(startPos, posSpace + 1)));
        startPos = posSpace + 1;
    }
    movies.push_back(stoll(line.substr(startPos, line.length())));
    this->addMovies(movies);
}

// get user id
unsigned long long User::getId() const {
    return this->id;
}

// add movies to user
void User::addMovies(const vector<unsigned long long> &movies) {
    for (const auto &movie: movies) {
        if (!this->hasMovie(movie)) {
            this->movies.push_back(movie);
        }
    }
}

// get all user's movies
vector<unsigned long long> User::getMovies() const {
    return this->movies;
}

// check if user has a specific movie
bool User::hasMovie(unsigned long long movie) const {
    if (this->movies.empty()) {
        return false;
    }
    return std::any_of(this->movies.begin(), this->movies.end(), [movie](const unsigned long long &movie1) {
        return movie1 == movie;
    });
}

// returns the number of common movies with another user
unsigned long long User::numOfCommonMovies(const User &user) const {
    unsigned long long numOfCommonMovies = 0;
    for (const auto &currentUserMovie: this->movies) {
        for (const auto &otherUserMovie: user.movies) {
            if (currentUserMovie == otherUserMovie) {
                numOfCommonMovies++;
            }
        }
    }
    return numOfCommonMovies;
}

// define equal between two User objects
bool User::operator==(const User &user) const {
    if (this->id != user.id) {
        return false;
    }
    return true;
}

// define not equal between two User objects
bool User::operator!=(const User &user) const {
    if (this->id == user.id) {
        return false;
    }
    if (this->movies.size() != user.movies.size()) {
        return true;
    }

    return !std::all_of(user.movies.begin(), user.movies.end(), [this](const long long int movie) {
        if (hasMovie(movie)) {
            return true;
        }
        return false;
    });
}

bool User::operator<(const User &user) const {
    if (this->id < user.id) {
        return true;
    }
    return false;
}

// Remove a specific movie from the user's movie list
void User::removeMovie(unsigned long long movieId) {
    // Erase the movie with the specified ID from the movie vector
    this->movies.erase(
        std::remove(this->movies.begin(), this->movies.end(), movieId),
        this->movies.end()
    );
}

