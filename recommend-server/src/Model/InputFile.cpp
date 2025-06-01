//
// Created by Lavi Shpaisman on 11/28/2024.
//

#include "InputFile.h"
#include <fstream>
#include <iostream>

#include "User.h"

// read all the file contents and return it as vector
vector<string> InputFile::readInput() {
    vector<string> lines;
    ifstream file(this->filePath);
    if (file.is_open()) {
        string currentLine;
        while (getline(file, currentLine)) {
            lines.push_back(currentLine);
        }
    }
    file.close();
    return lines;
}
