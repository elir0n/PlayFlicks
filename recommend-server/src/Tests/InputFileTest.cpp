//
// Created by Lavi Shpaisman on 11/28/2024.
//


#include <gtest/gtest.h>

#include "../Model/InputFile.h"
#include "../Model/OutputFile.h"


using namespace std;

// check if the files read are working correctly
TEST(InputFileTest, checkLineEqulity) {
    string filePath = "../data/user_data.txt";
    InputFile inputFile;
    OutputFile outputFile;

    if (remove(filePath.c_str()) != 0) {
    }

    // Create the parameters that will be tested
    OutputFile o;
    vector<string> v = {"123, 4566, 7744, 76555565, 6433", "145676, 5433, 8951, 532"};
    o.process(v);
    vector<string> e = inputFile.readInput();

    // Check if the files are equal
    for (int i = 0; i < e.size() && i < v.size(); i++) {
        EXPECT_EQ(e.at(i), v.at(i));
    }
}
