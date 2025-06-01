//
// Created by Lavi Shpaisman on 12/12/2024.
//

#include "PATCH.h"

#include "UserDataHandler.h"

// execute the command
string PATCH::execute(User user) {
    if (!UserDataHandler::editUser(user)) {
        this->exitCode = "404 Not Found";
    } else {
        this->exitCode = "204 No Content";
    }
    return "";
}

// get the exit code
string PATCH::getExitCode() {
    return this->exitCode;
}
