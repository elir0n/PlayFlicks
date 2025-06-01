//
// Created by Lavi Shpaisman on 12/2/2024.
//

#include <fstream>

#include "gtest/gtest.h"
#include <vector>
#include <string>
#include "../Model/InputFile.h"
#include "../Model/OutputFile.h"

using namespace std;

// check if the writing is executed
TEST(OutputFile, writingIsGood) {
    InputFile input;
    vector<string> inputLines = input.readInput();
    vector<string> lines = {"145334, 1", "1764, 76, 87"};

    // add lines to evaluate
    for (string ln: lines) {
        inputLines.push_back(ln);
    }
    OutputFile output;
    output.process(lines);
    vector<string> x = input.readInput();

    // see equality
    EXPECT_TRUE(equal(x.begin(),x.end(),inputLines.begin(),inputLines.end()));

    ofstream outputFile("../data/user_data.txt");
    if (!outputFile.is_open()) {
    }

    // write the lines to the file
    for (auto &input_line: inputLines) {
        outputFile << input_line << endl;
    }
    outputFile.close();
}

