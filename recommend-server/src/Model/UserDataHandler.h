//
// Created by Lavi Shpaisman on 12/11/2024.
//

#ifndef CONTROLLERFROMFILE_H
#define CONTROLLERFROMFILE_H
#include "User.h"

using namespace std;

class UserDataHandler {
public:
    // get all users from file
    static vector<User> getUsers();

    // get a specific user
    static User getUser(unsigned long long id);

    // Checks if user exists
    static bool userExists(unsigned long long userId);

    // function where its parameter is line where it is formatted: id, movieId1, movieId2, etc.
    static User getUserFromLine(std::string &line);

    // returns the user's data in a formatted string: "userId, movieId1, movieId2, etc.."
    static string formatForAdding(const User &user);

    // add a specific user to file, true if user not found, otherwise false
    static bool addUser(const User &user);

    // delete the specific file described
    static void deleteFile(const string &file);

    // remove the user from the file, false if user not found, otherwise true
    static bool removeUser(unsigned long long id);

    // edit the user movies, false if user exists, otherwise false
    static bool editUser(User &user);

    // returns true if movie exists, otherwise false
    static bool movieExists(const User &user, unsigned long long movieId);

    // edit the user movies for delete, false userID or movieID wasn't found, else true.
    static bool RemoveUserMovies(User &user);
};


#endif //CONTROLLERFROMFILE_H
