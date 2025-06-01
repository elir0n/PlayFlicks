//
// Created by Lavi Shpaisman on 12/11/2024.
//


#include <gtest/gtest.h>
# include <string>
#include "../Model/User.h"

#include "../Model/InputFile.h"
#include "../Model/OutputFile.h"
#include "../Model/POST.h"
using namespace std;

// Test the FileController - add user
TEST(POST, AddCorrect) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<string> users = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};
    OutputFile output_file;
    output_file.process(users);


    // putting new user
    string newUser = "16, 7, 8, 9, 10, 11, 12, 13, 14, 15";
    const User u(newUser);

    POST post;
    // executing the post
    string output = post.execute(u);

    const string expected = "201 Created";

    // checking if executed correctly
    vector<string> expectedValues = users;
    expectedValues.push_back(newUser);

    InputFile input_file;
    vector<string> currentValues = input_file.readInput();

    EXPECT_TRUE(equal(expectedValues.begin(),expectedValues.end(),currentValues.begin()));

    EXPECT_EQ(post.getExitCode(), expected);
}

// Test the FileController - user already exists
TEST(POST, DontAdd) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<string> users = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};
    OutputFile output_file;
    output_file.process(users);


    // trying to add user
    string newUser = "3, 7, 8, 9, 10, 11, 12, 13, 14, 15";
    const User u(newUser);

    POST post;
    // executing the post
    string output = post.execute(u);

    const string expected = "404 Not Found";

    EXPECT_EQ(post.getExitCode(), expected);
}
