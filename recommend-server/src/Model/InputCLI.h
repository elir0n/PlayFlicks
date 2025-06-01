//
// Created by Lavi Shpaisman on 11/26/2024.
//

#ifndef INPUTCLI_H
#define INPUTCLI_H
#include <string>

#include "ICommandInput.h"

#include  <string>
#include  <vector>


using namespace std;

// class that will get all the different inputs lines.
class InputCLI : public ICommandInput {
public:

    // a method that the user will get the line from the cli
    vector<string> readInput() override;
};


#endif //INPUTCLI_H