//
// Created by Lavi Shpaisman on 11/26/2024.
//

#ifndef ICOMMANDINPUT_H
#define ICOMMANDINPUT_H
#include <string>
#include <vector>

using namespace std;

// Interface for the command input
class ICommandInput {
public:
    virtual ~ICommandInput() = default;

    // read the lines from the file/cli ad etc.
    virtual vector<string> readInput() = 0;
};


#endif //ICOMMANDINPUT_H
