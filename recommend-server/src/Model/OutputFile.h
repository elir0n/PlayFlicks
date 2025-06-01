//
// Created by Lavi Shpaisman on 12/2/2024.
//

#ifndef OUTPUTFILE_H
#define OUTPUTFILE_H
#include "ICommandOutput.h"

// Class to write the output to a file
class OutputFile : public ICommandOutput {
    const std::string filePath = "../data/user_data.txt";

public:
    // write the lines to the end of the file
    void process(std::vector<std::string> lines) override;
};


#endif //OUTPUTFILE_H
