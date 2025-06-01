#ifndef COMMANDRECOMMEND_H
#define COMMANDRECOMMEND_H
#include "ICommandAction.h"
#include <string>
#include <vector>
#include <map>
#include "User.h"

using namespace std;

// Class to recommend movies to a user
class Recommend : public ICommandAction {
    // Holds users' number of common movies with user that asked for recommendation
    map<User, unsigned long long> usersCommonMovies;

    // Holds all users
    vector<User> users;

    // Holds all movies and their relevance in the recommendation
    map<unsigned long long, unsigned long long> moviesRelevance;

public:
    // get recommend exit code
    std::string getExitCode() override;

    // Gets all users and the number of movies they share with the user who asked for recommendation
    void getUsersCommonMovies(const User &currentUser);

    // Prints the movies, sorted by their relevance in the recommendation
    string printRecommendation();

    // Executes the recommendation command
    string execute(User user) override;

    // Gets all movies and their relevance in the recommendation
    void getMoviesRelevance(const User &currentUser, unsigned long long movieId);
};
#endif
