//
// Created by Lavi Shpaisman on 12/1/2024.
//

#include "OutputCLI.h"

#include <iostream>

#include "InputFile.h"


// printing the given lines to the CLI
void OutputCLI::process(std::vector<std::string> lines) {
    for (std::string &line: lines) {
        InputFile inputFile;
        std::cout << line << endl;
    }
}
