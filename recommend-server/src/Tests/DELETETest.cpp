#include <gtest/gtest.h>
#include <string>
#include "../Model/DELETE.h"
#include "../Model/OutputFile.h"
#include "../Model/InputFile.h"
using namespace std;

// regular test
TEST(DELETE, DeleteCorrect) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it doesn't matter
    remove(filePath.c_str());

    const vector<string> users = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};
    OutputFile output_file;
    output_file.process(users);

    string userToDelete = "4, 56";
    const User u(userToDelete);

    DELETE del;
    string output = del.execute(u);

    vector<string> expectedValues = {"3, 56, 67, 78", "15, 67, 49, 105", "4, 78"};

    InputFile input_file;
    vector<string> currentValues = input_file.readInput();

    // check if the values are the same
    EXPECT_TRUE(equal(expectedValues.begin(),expectedValues.end(),currentValues.begin()));

    string exitCode = "204 No Content";

    // check if the exit code is the same
    EXPECT_EQ(exitCode, del.getExitCode());
}

// Test if the user is not found
TEST(DELETE, userNotFound) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it doesn't matter
    remove(filePath.c_str());

    const vector<string> users = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};
    OutputFile output_file;
    output_file.process(users);

    string userToDelete = "5";
    const User u(userToDelete);

    DELETE del;
    del.execute(u);

    vector<string> expectedValues = users;

    InputFile input_file;
    vector<string> currentValues = input_file.readInput();

    // check if the values are the same
    EXPECT_TRUE(equal(expectedValues.begin(),expectedValues.end(),currentValues.begin()));

    string exitCode = "404 Not Found";

    // check if the exit code is the same
    EXPECT_EQ(exitCode, del.getExitCode());
}

// Test if the movie is not found
TEST(DELETE, movieNotFound) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());

    // putting values for test purposes
    const vector<string> users = {"3, 56, 67, 78", "4, 56, 78", "15, 67, 49, 105"};
    OutputFile output_file;
    output_file.process(users);

    string userToDelete = "4, 67";
    const User u(userToDelete);

    DELETE del;
    del.execute(u);

    vector<string> expectedValues = users;

    InputFile input_file;
    vector<string> currentValues = input_file.readInput();

    // check if the values are the same
    EXPECT_TRUE(equal(expectedValues.begin(),expectedValues.end(),currentValues.begin()));

    string exitCode = "404 Not Found";

    // check if the exit code is the same
    EXPECT_EQ(exitCode, del.getExitCode());
}
