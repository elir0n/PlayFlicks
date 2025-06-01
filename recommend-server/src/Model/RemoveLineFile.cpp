//
// Created by Lavi Shpaisman on 12/3/2024.
//

#include "RemoveLineFile.h"

#include <iostream>
#include <sstream>
#include <cstdio>
#include <cstring>
#include <vector>

#include "InputFile.h"
#include "OutputFile.h"


// Remove line if the id matches
string RemoveLineFile::execute(unsigned long long id) {
    InputFile file;
    vector<string> lines = file.readInput();
    vector<string> newLines;

    // loop through lines and add the lines that dost have id
    for (const auto &line: lines) {
        if (!idMatch(id, line)) {
            newLines.push_back(line);
        }
    }
    if (!removeOldFile()) {
        return "400 Bad Request\n";
    }
    OutputFile output;
    output.process(newLines);
    return "204 No Content\n";
}

// check if id is a match
bool RemoveLineFile::idMatch(const unsigned long long id, const string &s) {
    const long long delimPos = s.find(", ");
    const string idStr = s.substr(0, delimPos);
    if (id == atoi(idStr.c_str())) {
        return true;
    }
    return false;
}

// remove the old file
bool RemoveLineFile::removeOldFile() const {
    if (remove(filePath.c_str()) != 0) {
        throw runtime_error("File could not be removed");
        return false;
    }
    return true;
}
