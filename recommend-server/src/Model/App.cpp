//
// Created by Lavi Shpaisman on 12/5/2024.
//

#include <map>
#include <string>
#include <vector>

#include "Add.h"
#include "App.h"
#include "Help.h"
#include "ICommandAction.h"
#include "InputCLI.h"
#include "Recommend.h"

using namespace std;

std::vector<unsigned long long> movies;
unsigned long long userId = 0;
map<string, ICommandAction *> commands;

// convert the input to the desired format
bool convertInput(string line) {
    int count = 0;
    int posSpace = line.find(' ');
    int startPos = 0;

    // check if the line is empty
    if (posSpace == -1) {
        return true;
    }

    line = line.substr(posSpace, line.length());
    string subLine;
    while ((posSpace = line.find(' ')) != string::npos) {
        subLine = line.substr(0, posSpace);


        // line = line.substr(0, line.length());
        if (subLine.length() == 0) {
            line = line.substr(startPos + 1, line.length());
            continue;
        }

        unsigned long long id;

        // check if the id is valid
        try {
            id = stoll(subLine);
            line = line.substr(posSpace, line.length());
        } catch (...) {
            return false;
        }
        // check if the id is the first id
        if (count == 0) {
            userId = id;
            count++;
            continue;
        }

        // add the movie to the list
        addMovie(id);
        count++;
    }

    // check if the line is empty
    subLine = line.substr(0, line.length());
    if (subLine.length() != 0) {
        try {
            unsigned long long number = stoll(subLine);
            movies.push_back(number);
            count++;
        } catch (...) {
            return false;
        }
    }

    // check if the line is valid
    return count > 1;
}

// add the movie to the list
void addMovie(const unsigned long long movieId) {
    for (const auto movie: movies) {
        if (movie == movieId) {
            return;
        }
    }
    movies.push_back(movieId);
}

// run the program
void run() {
    InputCLI input;
    while (true) {
        movies.clear();
        movies.shrink_to_fit();
        userId = -1;
        vector<string> line = input.readInput();

        // check if the line is empty
        if (!convertInput(line.back())) {
            continue;
        }
        string command = line.back().substr(0, line.back().find(' '));
        if (commands.find(command) == commands.end()) {
            continue;
        }
        commands[command]->execute(User(userId, movies));
    }
}

// main function
int main() {
    commands["help"] = new Help();
    commands["add"] = new Add();
    commands["recommend"] = new Recommend();
    run();

    delete commands["help"];
    delete commands["add"];
    delete commands["recommend"];
    return 0;
}
