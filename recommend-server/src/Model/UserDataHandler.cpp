//
// Created by Lavi Shpaisman on 12/11/2024.
//

#include "UserDataHandler.h"
#include <vector>
#include <algorithm>
#include <mutex>
#include <sstream>

#include "OutputFile.h"
#include "InputFile.h"

// get all users from file
vector<User> UserDataHandler::getUsers() {
    vector<User> users;
    InputFile input;
    vector<string> userAsText = input.readInput();

    // convert the text to a user objects.
    users.reserve(userAsText.size());
    for (string userText: userAsText) {
        users.emplace_back(getUserFromLine(userText));
    }
    return users;
}

// get a specific user
User UserDataHandler::getUser(const unsigned long long id) {
    vector<User> users = getUsers();
    for (User user: users) {
        if (user.getId() == id) {
            return user;
        }
    }
    throw runtime_error("user not found");
}

// Checks if user exists
bool UserDataHandler::userExists(const unsigned long long userId) {
    bool flag = false;
    const vector<User> users = getUsers();
    for (auto &user: users) {
        if (user.getId() == userId) {
            flag = true;
            break;
        }
    }
    return flag;
}

// function where its parameter is line where it is formatted: id, movieId1, movieId2, etc.
User UserDataHandler::getUserFromLine(std::string &line) {
    // getId
    unsigned long long posSpace = line.find(' ');
    unsigned long long startPos = 0;
    const unsigned long long id = stoll(line.substr(startPos, posSpace));
    startPos = posSpace + 1;

    if ((posSpace = line.find(',') == std::string::npos)) {
        return User{id, {}};
    }

    // get movies
    vector<unsigned long long> movies;
    while ((posSpace = line.find(", ", posSpace + 1)) != string::npos) {
        movies.push_back(stoll(line.substr(startPos, posSpace + 1)));
        startPos = posSpace + 1;
    }
    movies.push_back(stoll(line.substr(startPos, line.length())));
    return User{id, movies};
}

// returns the user's data in a formatted string: "userId, movieId1, movieId2, etc.."
string UserDataHandler::formatForAdding(const User &user) {
    stringstream ss;
    ss << user.getId();

    for (const auto &movie: user.getMovies()) {
        // Add movie IDs
        ss << ", " << movie;
    }
    // Return the formatted string
    return ss.str();
}

// add a specific user to file, true if user not found, otherwise false
bool UserDataHandler::addUser(const User &user) {
    if (userExists(user.getId())) {
        return false;
    }
    OutputFile output;
    string userText = formatForAdding(user);
    output.process({userText});
    return true;
}

// delete the specific file described
void UserDataHandler::deleteFile(const string &file) {
    remove(file.c_str());
}

// remove the user from the file, false if user not found, otherwise true
bool UserDataHandler::removeUser(const unsigned long long id) {
    if (!userExists(id)) {
        return false;
    }

    User user = getUser(id);

    vector<User> users = getUsers();
    // add all movie except userToDel

    users.erase(std::remove(users.begin(), users.end(), user), users.end());

    // deleting the file for writing again.
    string filePath = "../data/user_data.txt";

    deleteFile(filePath);

    for (User &user1: users) {
        addUser(user1);
    }
    return true;
}

// edit the user movies, false if user exists, otherwise false
bool UserDataHandler::editUser(User &user) {
    if (!userExists(user.getId())) {
        return false;
    }
    for (long long int id: user.getMovies()) {
      if(movieExists(getUser(user.getId()),id)) {
          return false;
      }

    }

    vector<User> users = getUsers();

    User oldUser = getUser(user.getId());


    // edit the user selected and remove its id in file
    for (User &user1: users) {
        if (user1.getId() == user.getId()) {
            user.addMovies(user1.getMovies());
            (void) removeUser(user1.getId());
        }
    }
    // add the updated user.
    addUser(user);

    return true;
}

// returns true if movie exists, otherwise false
bool UserDataHandler::movieExists(const User &user, const unsigned long long movieId) {
    bool flag = false;
    for (const auto movie: user.getMovies()) {
        if (movie == movieId) {
            flag = true;
            break;
        }
    }
    return flag;
}

// edit the user movies for delete, false userID or movieID wasn't found, else true.
bool UserDataHandler::RemoveUserMovies(User &user) {
    if (!userExists(user.getId())) {
        return false;
    }

    vector<User> users = getUsers();


    User user1 = getUser(user.getId());

    // Remove the specified movies from the user's movie list.
    for (const auto &movie: user.getMovies()) {
        if (!movieExists(user1, movie)) {
            return false;
        }
        user1.removeMovie(movie);
    }
    // Remove the old user from storage.
    (void) removeUser(user1.getId());


    // Add the updated user back to storage.
    addUser(user1);

    return true;
}
