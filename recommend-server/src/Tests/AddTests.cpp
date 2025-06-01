#include "gtest/gtest.h"
#include "../Model/Add.h"
#include "../Model/InputFile.h"
#include <limits>
#include <stdexcept>
#include <vector>
#include <string>

#include "../Model/OutputFile.h"

// test the add function
TEST(AddTest, addNewUser) {
    const string filePath = "../data/user_data.txt";
    // if the file doesn't exist, it dost matter
    remove(filePath.c_str());
    // // Step 1: Load initial data
    vector<string> expectedInitialData = {
        "123, 4566, 7744, 76555565, 6433",
        "145676, 5433, 8951, 532"
    };

    OutputFile file;
    file.process(expectedInitialData);
    // Check initial data matches the expected state
    InputFile inputFile;
    const vector<string> initialData = inputFile.readInput();
    for (size_t i = 0; i < expectedInitialData.size(); i++) {
        EXPECT_EQ(initialData.at(i), expectedInitialData.at(i));
    }

    // Step 2: Perform the add operation
    unsigned long long newUserId = 9999;
    unsigned long long newMovieId = 7777;

    Add a;
    a.addMovies(User(newUserId, {newMovieId}));

    // Step 3: Validate the updated data
    vector<string> updatedData = inputFile.readInput();
    vector<string> expectedUpdatedData = {
        "123, 4566, 7744, 76555565, 6433",
        "145676, 5433, 8951, 532",
        "9999, 7777" // New user added
    };
    for (size_t i = 0; i < expectedUpdatedData.size(); i++) {
        EXPECT_EQ(updatedData.at(i), expectedUpdatedData.at(i)) << "Mismatch at line " << i + 1;
    }
}

// test the added function - edge case
TEST(AddTest, LongLongBoundaryAddMovie) {
    Add a;

    // Invalid boundary: use values exceeding `long long` limits
    unsigned long long tooLarge = std::numeric_limits<unsigned long long>::max();
    unsigned long long tooSmall = std::numeric_limits<unsigned long long>::min();

    // Check that the function throws an exception when the input is out of bounds
    EXPECT_THROW(a.addMovies(User(5, {tooLarge})), std::overflow_error);
    EXPECT_THROW(a.addMovies(User(5, {tooSmall})), std::underflow_error);
}
