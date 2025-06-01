//
// Created by Lavi Shpaisman on 12/1/2024.
//

#include<vector>
#include<string>
#ifndef ICOMMANDOUTPUT_H
#define ICOMMANDOUTPUT_H

// Interface for the command output
class ICommandOutput {
public:
    virtual ~ICommandOutput() = default;

    // write the given lines to file or database
    virtual void process(std::vector<std::string> lines) = 0;
};


#endif //ICOMMANDOUTPUT_H

