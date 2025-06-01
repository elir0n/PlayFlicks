#include "DELETE.h"
#include "DELETE.h"
// execute the command
string DELETE::execute(User user) {
    if (!UserDataHandler::RemoveUserMovies(user)) {
        this->exitCode = "404 Not Found";
    } else {
        this->exitCode = "204 No Content";
    }
    return "";
}

std::string DELETE::getExitCode() {
    return this->exitCode;
}
