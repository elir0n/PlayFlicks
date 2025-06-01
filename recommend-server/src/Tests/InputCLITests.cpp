#include <gtest/gtest.h>
#include <string>
#include "../Model/InputCLI.h"
#include <filesystem>
#include <fstream>


using namespace std;

// Test if input Retrieves the correct line input
TEST(InputCLI1, basic) {
    string s = "";
    srand(time(nullptr));

    // generate random string
    for (int i = 0; i <= rand() % 20 + 1; i++) {
        const char x = (rand() % (126 - 33)) + 33;
        if (x == '\\') {
            i--;
            continue;
        }
        s += x;
    }

    // redirect cin to string
    std::streambuf *orig = std::cin.rdbuf();
    std::istringstream input(s + "\n");
    std::cin.rdbuf(input.rdbuf());

    string line;

    // read input
    InputCLI x;
    const vector<string> tmp = x.readInput();
    cout << "--------------------------------------" << endl << endl;
    cout << "original: " << s << endl;
    cout << "read: " << tmp.back() << endl << endl;
    cout << "--------------------------------------" << endl << endl;
    // check if the input is correct
    EXPECT_EQ(s, tmp.back());

    // retrieve original cin
    std::cin.rdbuf(orig);
}


int main() {
    testing::InitGoogleTest();
    RUN_ALL_TESTS();
    const string filePath = "../data/user_data.txt";
    remove(filePath.c_str());
}
