#include <gtest/gtest.h>

#include "../Model/OutputFile.h"
#include "../Model/Recommend.h"

using namespace std;

// Test the recommendation function
TEST(PrintRecommendationTest, SanityTest) {
    string filePath = "../data/user_data.txt";

    // Remove the file
    remove(filePath.c_str());


    vector<string> lines = {
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

    OutputFile outputFile;

    outputFile.process(lines);

    Recommend recommend;

    // Read string from cout and compare it to expected output
    stringstream buffer;
    streambuf *sbuf = std::cout.rdbuf();
    std::cout.rdbuf(buffer.rdbuf());
    recommend.execute(User(1, {104}));
    string output = buffer.str();
    std::cout.rdbuf(sbuf);
    string expected = "105 106 111 110 112 113 107 108 109 114\n";

    // Check if the output is correct
    for (int i = 0; i < output.size(); i++) {
        EXPECT_EQ(output.at(i), expected.at(i));
    }
}
