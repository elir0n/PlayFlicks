//
// Created by Lavi Shpaisman on 11/27/2024.
//

#ifndef USER_H
#define USER_H
#include <string>
#include <vector>

using namespace std;

// Class to represent a user
class User {
    unsigned long long id;
    vector<unsigned long long> movies;

public:
    // user constructor get is id and vector of movies.
    User(unsigned long long id, const vector<unsigned long long> &movies);

    // function that it's parameter is line where it is formatted: id, movieId1, movieId2, etc.
    explicit User(std::string &line);

    // get user id
    unsigned long long getId() const;

    // add the movies to the user
    void addMovies(const vector<unsigned long long> &movies);

    // check if the user has a specific movie
    bool hasMovie(unsigned long long movie) const;

    // get all user movies
    vector<unsigned long long> getMovies() const;

    // return the number of common movies with another user
    unsigned long long numOfCommonMovies(const User &user) const;

    // define equal between two User objects
    bool operator==(const User &user) const;

    // define not equal between two User objects
    bool operator!=(const User &user) const;

    bool operator<(const User &user) const;

    // remove a specific movie from the user's movie list
    void removeMovie(unsigned long long movieId);
};


#endif //USER_H
