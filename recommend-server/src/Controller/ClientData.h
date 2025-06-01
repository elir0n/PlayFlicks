//
// Created by Lavi Shpaisman on 12/17/2024.
//

#ifndef CLIENTDATA_H
#define CLIENTDATA_H

#include <map>
#include <string>
#include "../Model/User.h"
#include "../Model/ICommandAction.h"


// this class supposes to transfer the client data because we yet integrated in one app, It's not well MCV
class ClientData {
public:
    // for a string that input in the format "Command [userId] [movieId] etc." get command
    static std::string getCommand(std::string &line);

    // for a string that input in the format "Command [userId] [movieId] etc." get the User format.
    // moreThanOneArgs: the user needs to have at least his id and one movie.
    // notNumber: if line has a var that isn't a number.
    static User getUser(std::string &line, bool &notNumber, bool &noUser);

    // get command and user
    static bool getData(string &line, string &command, User &user, const map<string, ICommandAction *> &commands);

    // returns if was successful
    static bool validateUser(const string &command, const User &user, bool notNumber, const bool &noUser,
                             const map<string, ICommandAction *> &commands);

    // returns if was successful
    static bool validateCommand(const string &command, map<std::string, ICommandAction *> commands);
};


#endif //CLIENTDATA_H
