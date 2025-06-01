//
// Created by Lavi Shpaisman on 12/11/2024.
//

#include "POST.h"

#include <stdexcept>

#include "UserDataHandler.h"


// execute the command
string POST::execute(const User user) {
    if (UserDataHandler::addUser(user)) {
        this->exitCode = "201 Created";
    } else {
        this->exitCode = "404 Not Found";
    }

    return "";
}

// get the Post exit code
string POST::getExitCode() {
    return this->exitCode;
}
