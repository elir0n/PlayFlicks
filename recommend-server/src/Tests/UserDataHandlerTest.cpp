//
// Created by Lavi Shpaisman on 12/11/2024.
//

#include "gtest/gtest.h"
# include "../Model/OutputFile.h"
# include "../Model/UserDataHandler.h"
# include "../Model/User.h"

using namespace std;

// Test the FileController - get users
TEST(FileController, getUsers) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<string> usersText = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};
    const User u1(3, {56, 67, 78});
    const User u2(4, {56, 78});
    const User u3(15, {67, 49, 105});

    OutputFile output_file;
    output_file.process(usersText);

    const vector<User> users = UserDataHandler::getUsers();

    EXPECT_TRUE(users.at(0) == u1);
    EXPECT_TRUE(users.at(1) == u2);
    EXPECT_TRUE(users.at(2) == u3);
}

// Test the FileController - add user
TEST(FileController, formatForAdding) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<string> usersText = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};
    const User u1(3, {56, 67, 78});
    const User u2(4, {56, 78});
    const User u3(15, {67, 49, 105});

    OutputFile output_file;
    output_file.process(usersText);

    EXPECT_TRUE(UserDataHandler::formatForAdding(u1) == usersText.at(0));
    EXPECT_TRUE(UserDataHandler::formatForAdding(u2) == usersText.at(1));
    EXPECT_TRUE(UserDataHandler::formatForAdding(u3) == usersText.at(2));
}

// Test the FileController - add user
TEST(FileController, getUserFromLine) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    vector<string> usersText = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};

    OutputFile output_file;
    output_file.process(usersText);
    const User u1(3, {56, 67, 78});
    const User u2(4, {56, 78});
    const User u3(15, {67, 49, 105});
    EXPECT_TRUE(UserDataHandler::getUserFromLine(usersText.at(0)) == u1);
    EXPECT_TRUE(UserDataHandler::getUserFromLine(usersText.at(1)) == u2);
    EXPECT_TRUE(UserDataHandler::getUserFromLine(usersText.at(2)) == u3);
}

// Test the FileController - remove user
TEST(FileController, removeUser) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<string> usersText = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};

    const User u(3, {56, 67, 78});
    OutputFile output_file;
    output_file.process(usersText);

    UserDataHandler::removeUser(u.getId());
    const User u2(4, {56, 78});
    const User u3(15, {67, 49, 105});

    const vector<User> users = UserDataHandler::getUsers();

    EXPECT_TRUE(users.at(0) == u2);
    EXPECT_TRUE(users.at(1) == u3);
}

// Test the FileController - edit user
TEST(FileController, editUser) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<string> usersText = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};

    User u(3, {56, 67, 78});
    OutputFile output_file;
    output_file.process(usersText);

    UserDataHandler::editUser(u);


    const vector<User> users = UserDataHandler::getUsers();

    EXPECT_TRUE(users.at(2) == u);
}
