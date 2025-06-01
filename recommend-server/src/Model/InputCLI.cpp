//
// Created by Lavi Shpaisman on 11/26/2024.
//

#include "InputCLI.h"

#include <iostream>

// reading the user input until \n
vector<string> InputCLI::readInput() {
    vector<string> lines;
    string input;
    getline(cin, input);
    lines.push_back(input);
    return lines;
}