//
// Created by Lavi Shpaisman on 12/3/2024.
//

#ifndef HELP_H
#define HELP_H
#include "ICommandAction.h"
#include <string>

using namespace std;

// Class to print the help command
class Help : public ICommandAction {
    vector<string> lines = {
        "help",
        "GET, arguments: [userid] [movieid]",
        "POST, arguments: [userid] [movieid1] [movieid2] ...",
        "PATCH, arguments: [userid] [movieid1] [movieid2] ...",
        "DELETE, arguments: [userid] [movieid1] [movieid2] ..."
    };
    string exitCode;

public:
    // print the desired lines
    string execute(User user) override;

    std::string getExitCode() override;
};


#endif //HELP_H
