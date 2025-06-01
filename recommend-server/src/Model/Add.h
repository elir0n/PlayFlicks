#ifndef ADD_H
#define ADD_H

#include <string>
#include <vector>
#include "User.h"
#include "ICommandAction.h"
using namespace std;

// Class to add a movie to a user. If the user doesn't exist, create a new one.
class Add : public ICommandAction {
    vector<User> users;

public:
    // Method to add a movie to a user. If the user doesn't exist, create a new one.
    void addMovies(User user);

    std::string getExitCode() override;

    // Method to execute the action of the command 
    string execute(User user) override;
};

#endif
