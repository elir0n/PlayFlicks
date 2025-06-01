//
// Created by Lavi Shpaisman on 12/3/2024.
//

#ifndef ICOMMANDACTION_H
#define ICOMMANDACTION_H

#include <string>
#include <vector>

#include "User.h"

// Interface for the command action
class ICommandAction {
public:
    virtual ~ICommandAction() = default;

    // command to execute each action we want when we get the user entered string
    virtual std::string execute(User user) = 0;

    virtual std::string getExitCode() = 0;
};
#endif //ICOMMANDACTION_H
