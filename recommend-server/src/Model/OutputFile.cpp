//
//
// Created by Lavi Shpaisman on 12/2/2024.
//

#include "OutputFile.h"
#include <string>
#include <fstream>
#include <vector>

#include "InputFile.h"
using namespace std;

// add the given lines at the end of the file
void OutputFile::process(vector<string> lines) {
    InputFile i;
    vector<string> current_input = i.readInput();
    ofstream outputFile(filePath);


    // check if a file opens else throw error
    if (!outputFile.is_open()) {
        throw runtime_error("Error opening output file\n");
    }

    // write the lines to the file
    for (auto &line: current_input) {
        outputFile << line << endl;
    }

    for (string &line: lines) {
        if (line == lines.back()) {
            outputFile << line;
            continue;
        }
        outputFile << line << endl;
    }
    outputFile.close();
}
