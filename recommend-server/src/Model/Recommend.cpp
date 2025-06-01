#include "Recommend.h"
#include <vector>
#include <map>
#include <iostream>
#include <sstream>
#include <string>
#include <algorithm>

#include "User.h"
#include "UserDataHandler.h"

using namespace std;

// Executes the recommendation command the user his user id and movie to recommend based on
string Recommend::execute(const User user) {
    this->usersCommonMovies = {};
    this->moviesRelevance = {};
    this->users = UserDataHandler::getUsers();
    const User currentUser = UserDataHandler::getUser(user.getId());
    getUsersCommonMovies(currentUser);
    getMoviesRelevance(currentUser, user.getMovies().back());

    return (printRecommendation());
}

// Gets all movies and their relevance in the recommendation
void Recommend::getMoviesRelevance(
    const User &currentUser, const unsigned long long movieId) {
    // For each pair of user and number of common movies
    for (const auto &mapPair: this->usersCommonMovies) {
        // If the user does not have the movie, that the user who asked for recommendation has
        if (!mapPair.first.hasMovie(movieId)) {
            // Skip to the next user
            continue;
        }

        User user = mapPair.first;
        // If the user who asked for recommendation has the movie or the movie is the same movie that the user
        // who asked for recommendation.
        for (unsigned long long movie: user.getMovies()) {
            if (currentUser.hasMovie(movie) || movie == movieId) {
                // Skip to the next movie
                continue;
            }

            // Add the number of common movies to the relevance of the movie, to increase movie's weight in relevance
            this->moviesRelevance[movie] += mapPair.second;
        }
    }
}

std::string Recommend::getExitCode() {
    return "";
}

// Gets all users and the number of movies they share with the user who asked for a recommendation
void Recommend::getUsersCommonMovies(const User &currentUser) {
    // Get all users' number of common movies with the user who asked for a recommendation
    for (auto &user: users) {
        if (user == currentUser) {
            continue;
        }
        // Set user's weight as his number of common movies with the user who asked for a recommendation
        this->usersCommonMovies[user] = currentUser.numOfCommonMovies(user);
    }
}

// Prints the movies, sorted by their relevance in the recommendation
string Recommend::printRecommendation() {
    vector<pair<unsigned long long, unsigned long long> > vecToSort(this->moviesRelevance.begin(),
                                                                    this->moviesRelevance.end());
    sort(vecToSort.begin(), vecToSort.end(), // Sort the movies by their relevance
         [](const pair<unsigned long long, unsigned long long> &movie1,
            const pair<unsigned long long, unsigned long long> &movie2) {
             return movie1.second > movie2.second;
         });

    // print the most relevant movies
    string s;
    for (int i = 0; i < 10 && i < vecToSort.size(); i++) {
        s += std::to_string((vecToSort.at(i).first));
        if (i != 9) {
            s += " ";
        }
    }
    return s;
}
