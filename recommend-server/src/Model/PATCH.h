//
// Created by Lavi Shpaisman on 12/12/2024.
//

#ifndef PATCH_H
#define PATCH_H
#include "ICommandAction.h"

// Class to add movies to an already existing user
class PATCH : public ICommandAction {
    string exitCode;

public:
    // execute the command
    string execute(User user) override;

    // get the exit code
    string getExitCode() override;
};


#endif //PATCH_H
