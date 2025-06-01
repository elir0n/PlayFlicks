#ifndef DELETE_H
#define DELETE_H
#include "RemoveLineFile.h"
#include "ICommandAction.h"
#include "User.h"
#include "UserDataHandler.h"

// Class to delete a movie (can be more than 1) from a user
class DELETE : public ICommandAction {
    string exitCode;

public:
    // execute the command
    string execute(User user) override;

    // returns the exit code
    std::string getExitCode() override;
};

#endif //DELETE_H
