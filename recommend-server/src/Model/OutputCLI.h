//
// Created by Lavi Shpaisman on 12/1/2024.
//

#ifndef OUTPUTCLI_H
#define OUTPUTCLI_H

#include "ICommandOutput.h"

// Class to print the output to the CLI
class OutputCLI: public ICommandOutput {
public:

    // printing the given lines to the CLI
    void process(std::vector<std::string> lines) override;
};



#endif //OUTPUTCLI_H

