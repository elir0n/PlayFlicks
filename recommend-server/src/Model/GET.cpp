#include "GET.h"
#include "UserDataHandler.h"
#include "UserDataHandler.h"

// execute the command
string GET::execute(const User user) {
    this->users = UserDataHandler::getUsers();
    return getRecommendation(user);
}

// get the exit code
string GET::getExitCode() {
    return this->exitCode;
}

// get the recommendation
string GET::getRecommendation(const User &user) {
    Recommend recommend;
    // check if the user exists
    if (!UserDataHandler::userExists(user.getId())) {
        this->exitCode = "404 Not Found";
        return "";
    }
    string reco = recommend.execute(user);
    this->exitCode = "200 OK";
    return reco;
}
