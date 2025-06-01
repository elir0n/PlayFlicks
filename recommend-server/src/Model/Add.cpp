#include "Add.h"
#include <fstream>
#include <limits>
#include <stdexcept>
#include "RemoveLineFile.h"
#include "OutputFile.h"
#include "User.h"
#include <vector>
#include <string>

#include "UserDataHandler.h"

// adds movies to a user, if the user doesn't exist, it will create a new user
void Add::addMovies(User user) {
    vector<unsigned long long> movieId = user.getMovies();
    unsigned long long userId = user.getId();

    // Check for overflow and underflow
    for (auto &singleMovieId: movieId) {
        if (singleMovieId == std::numeric_limits<unsigned long long>::max() || userId == std::numeric_limits<unsigned
                long long>::max()) {
            throw std::overflow_error("overflow error.");
        }
        if (singleMovieId == std::numeric_limits<unsigned long long>::min() || userId == std::numeric_limits<unsigned
                long long>::min()) {
            throw std::underflow_error("underflow error.");
        }
    }

    RemoveLineFile removeLineFile;
    OutputFile outputFile;
    this->users = UserDataHandler::getUsers();

    // Check if the user already exists
    for (auto &user: this->users) {
        if (user.getId() == userId) {
            removeLineFile.execute(userId);

            // Add only the movies that are not already in the user's movie list
            for (auto &singleMovieId: movieId) {
                if (!user.hasMovie(singleMovieId)) {
                    user.addMovies({singleMovieId});
                }
            }
            outputFile.process({UserDataHandler::formatForAdding(user)});
            return;
        }
    }

    // User doesn't exist, create a new user and add the movies
    this->users.push_back(User(userId, {}));

    this->users.back().addMovies(movieId);
    outputFile.process({
        UserDataHandler::formatForAdding(this->users.back())
    });
}

std::string Add::getExitCode() {
    return "";
}


// Execute the Add command
string Add::execute(User user) {
    // Add the movie to the user
    this->addMovies(user);
    return "200 OK\n";

    // Clear the user's vector
    this->users.clear();
    this->users.shrink_to_fit();
}
