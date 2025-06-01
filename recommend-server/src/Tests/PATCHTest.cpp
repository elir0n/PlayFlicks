//
// Created by Lavi Shpaisman on 12/12/2024.
//

#include "gtest/gtest.h"
#include "../Model/OutputFile.h"
#include "../Model/User.h"
#include "../Model/PATCH.h"
#include "../Model/UserDataHandler.h"

using namespace std;

TEST(PATCH, AddCoorect) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<User> users = {
        User(3, {56, 67, 78}), User(4, {56, 78}), User(15, {67, 49, 105})
    };
    for (const User &user: users) {
        UserDataHandler::addUser(user);
    }


    const User newUser(3, {45, 67, 78});

    PATCH patch;
    patch.execute(newUser);

    const string exitCode = patch.getExitCode();

    const vector<User> expectedValues = {
        User(4, {56, 78}), User(15, {67, 49, 105}), User(3, {56, 67, 78, 45})
    };;

    const vector<User> currentUsers = UserDataHandler::getUsers();

    for (int i = 0; i < expectedValues.size(); i++) {
        for (const User &user: currentUsers) {
        }
        EXPECT_TRUE(expectedValues[i] == currentUsers[i]);
    }

    const string expected = "204 No Content";

    EXPECT_EQ(exitCode, expected);
}

TEST(PATCH, notCoorect) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<User> users = {
        User(3, {56, 67, 78}), User(4, {56, 78}), User(15, {67, 49, 105})
    };

    for (const User &user: users) {
        UserDataHandler::addUser(user);
    }

    // trying to add user
    string newUser = "18, 7, 8, 9, 10, 11, 12, 13, 14, 15";
    const User u(newUser);

    PATCH patch;
    // executing the post
    patch.execute(u);

    string exitCode = patch.getExitCode();
    // checking if getting bad request.
    const string output = "404 Not Found";

    // checking if getting not found.
    EXPECT_EQ(exitCode, output);
}
