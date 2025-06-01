//
// Created by Lavi Shpaisman on 11/28/2024.
//

#ifndef INPUTFILE_H
#define INPUTFILE_H

#include "ICommandInput.h"

// Class to read the input from a file
class InputFile : public ICommandInput {
    const string filePath = "../data/user_data.txt";

public:

    // A function that gets all the line in a vector from the file.
    vector<string> readInput() override;
};




#endif //INPUTFILE_H
