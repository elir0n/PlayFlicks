#include <gtest/gtest.h>
#include "../Model/User.h"
#include "../Model/GET.h"
#include "../Model/InputFile.h"
#include "../Model/OutputFile.h"
#include <string>

#include "../Model/UserDataHandler.h"
using namespace std;

// Test the GET command - sanity test
TEST(GETTest, SanityTest) {
    const string filePath = "../data/user_data.txt";

    // Remove the file
    remove(filePath.c_str());

    const vector<User> users = {
        User(1, {100, 101, 102, 103}),
        User(2, {101, 102, 104, 105, 106}),
        User(3, {100, 104, 105, 107, 108}),
        User(4, {101, 105, 106, 107, 109, 110}),
        User(5, {100, 102, 103, 105, 108, 111}),
        User(6, {100, 103, 104, 110, 111, 112, 113}),
        User(7, {102, 105, 106, 107, 108, 109, 110}),
        User(8, {101, 104, 105, 106, 109, 111, 114}),
        User(9, {100, 103, 105, 107, 112, 113, 115}),
        User(10, {100, 102, 105, 106, 107, 109, 110, 116})
    };

    // Add the users to the file
    for (const User &user: users) {
        UserDataHandler::addUser(user);
    }

    GET get;

    // Execute the GET command
    const string output = get.execute(User(1, {104}));
    const vector<string> expectedValues = {"105 106 111 110 112 113 107 108 109 114"};

    // Check the output
    EXPECT_EQ(output, expectedValues[0]);

    // Check the exit code
    string exitCode = get.getExitCode();
    string expected = "200 OK";
    EXPECT_EQ(exitCode, expected);
}

// Test the GET command - user not found
TEST(GETTest, UserNotFound) {
    string filePath = "../data/user_data.txt";

    vector<string> users = {
        "1, 100, 101, 102, 103",
        "2, 101, 102, 104, 105, 106",
        "3, 100, 104, 105, 107, 108",
        "4, 101, 105, 106, 107, 109, 110",
        "5, 100, 102, 103, 105, 108, 111",
        "6, 100, 103, 104, 110, 111, 112, 113",
        "7, 102, 105, 106, 107, 108, 109, 110",
        "8, 101, 104, 105, 106, 109, 111, 114",
        "9, 100, 103, 105, 107, 112, 113, 115",
        "10, 100, 102, 105, 106, 107, 109, 110, 116"
    };

    // Remove the file
    remove(filePath.c_str());

    // Add the users to the file
    OutputFile outputFile;
    outputFile.process(users);

    GET get;

    // Execute the GET command
    const string output = get.execute(User(11, {104}));
    const string expectedValues;

    // Check the output
    EXPECT_EQ(output, expectedValues);

    // Check the exit code
    string exitCode = get.getExitCode();
    string expected = "404 Not Found";
    EXPECT_EQ(exitCode, expected);
}
