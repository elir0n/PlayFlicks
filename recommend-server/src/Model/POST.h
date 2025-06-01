//
// Created by Lavi Shpaisman on 12/11/2024.
//

#ifndef POST_H
#define POST_H
#include "Add.h"
#include "ICommandAction.h"

// Class to add a user
class POST : public ICommandAction {
    vector<User> users;
    string exitCode;

public:
    // execute the command
    string execute(User user) override;

    // get the Post exit code
    string getExitCode() override;
};


#endif //POST_H
