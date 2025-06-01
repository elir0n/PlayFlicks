//
// Created by Lavi Shpaisman on 12/17/2024.
//

#include "ClientData.h"

#include <iostream>
#include <map>
#include <ostream>

#include "../Model/ICommandAction.h"

std::string ClientData::getCommand(std::string &line)
{
    unsigned long long posSpace = line.find(' ');

    // checking if there isn't any space
    if (posSpace == string::npos)
    {
        string command = line.substr(0, line.length());
        line = "";
        return command;
    }

    string command = line.substr(0, posSpace);
    line = line.substr(posSpace + 1, line.length());
    return command;
}

User ClientData::getUser(std::string &line, bool &notNumber, bool &noUser)
{
    notNumber = false;
    unsigned long long count = 0;

    unsigned long long userId = -1;
    vector<unsigned long long> movieId = {};

    while (true)
    {
        // getting space position
        const unsigned long long posSpace = line.find(' ');

        //
        if (line.empty())
        {
            break;
        }

        string subLine;
        // checking if space was not found(end of string)
        if (posSpace == string::npos)
        {
            subLine = line.substr(0, line.length() + 1);
            line = "";
        }
        else
        {
            subLine = line.substr(0, posSpace);
            line = line.substr(posSpace + 1, line.length());
        }

        if (subLine.empty())
        {
            continue;
        }

        unsigned long long id;

        // checking if the subLine is a number

        try
        {
            id = stoll(subLine);
            // -1 to get read of the end
        }
        catch (...)
        {
            notNumber = true;
            // junk val

            return User(56, {67});
        }

        // if it doesn't have userId

        if (count == 0)
        {
            userId = id;
            count++;
            continue;
        }

        // else add to movies
        movieId.emplace_back(id);

        // incrementing count
        count++;

        // checking if space was not found(end of string)
        if (posSpace == string::npos)
        {
            break;
        }
    }
    // if there is no user
    if (userId == -1)
    {
        noUser = true;
        return User(56, {67});
    }

    noUser = false;
    return User(userId, movieId);
}

// returns if was successful
bool ClientData::validateCommand(const string &command, map<std::string, ICommandAction *> commands)
{
    // checking if the command is empty
    if (command.empty())
    {
        return false;
    }

    // checking if the command is not in the command map

    if (commands.find(command) == commands.end())
    {
        return false;
    }
    return true;
}

// returns if was successful
bool ClientData::validateUser(const string &command, const User &user, const bool notNumber, const bool &noUser,
                              const map<string, ICommandAction *> &commands)
{
    // checking if the user is not a number
    if (command == "help" && noUser)
    {
        return true;
    }
    if (notNumber)
    {
        return false;
    }
    if (noUser)
    {
        return false;
    }
    if (command == "GET" && user.getMovies().size() > 1)
    {
        return false;
    }
    if (command == "help")
    {
        return false;
    }
    if (command != "help" && user.getMovies().empty())
    {
        return false;
    }

    return true;
}

// get command and user
bool ClientData::getData(string &line, string &command, User &user, const map<string, ICommandAction *> &commands)
{
    command = getCommand(line);
    cout << command << endl;

    bool notNumber;
    bool noUser;

    user = getUser(line, notNumber, noUser);
    cout << user.getId() << endl;

    return validateCommand(command, commands) && validateUser(command, user, notNumber, noUser, commands);
}
