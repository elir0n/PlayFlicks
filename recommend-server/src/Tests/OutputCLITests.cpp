//
// Created by Lavi Shpaisman on 12/1/2024.
//


#include <gtest/gtest.h>
#include <string>
#include "../Model/InputCLI.h"
#include "../Model/OutputCLI.h"

using namespace std;

// Test if input Retrieves the correct line input
TEST(OutPutCLI1, basic) {
    istream &s1 = cin;
    string s = "";
    srand(time(nullptr));

    // generate random string
    for (int i = 0; i <= rand() % 20 + 1; i++) {
        const char x = (rand() % (126 - 33)) + 33;
        if (x == '\n') {
            i--;
            continue;
        }
        s += x;
    }

    // redirect v to string
    std::vector<string> v = {s};

    
    OutputCLI c;
    c.process(v);
}

