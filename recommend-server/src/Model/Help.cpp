//
// Created by Lavi Shpaisman on 12/3/2024.
//

#include "Help.h"

#include <algorithm>

#include "OutputCLI.h"

using namespace std;

// print the desired lines
string Help::execute(User user) {
    // sort the list alphabetically.
    std::sort(this->lines.begin(), this->lines.end(), [](const string &a, const string &b) {
        string aLow = a;
        string bLow = b;

        std::transform(aLow.begin(), aLow.end(), aLow.begin(), ::tolower);
        std::transform(bLow.begin(), bLow.end(), bLow.begin(), ::tolower);

        return aLow < bLow;
    });


    string result;

    // concat string
    for (int i = 0; i < this->lines.size() - 1; i++) {
        result += this->lines[i] + "\n";
    }
    result += this->lines[this->lines.size() - 1];

    return result;
}

std::string Help::getExitCode() {
    return exitCode;
}
