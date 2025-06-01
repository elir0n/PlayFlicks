//
// Created by Lavi Shpaisman on 12/3/2024.
//

#ifndef DELETELINEFILE_H
#define DELETELINEFILE_H
#include <string>

#include "ICommandRemove.h"


using namespace std;

// Class to remove a line from a file
class RemoveLineFile : public ICommandRemove {
    const string filePath = "../data/user_data.txt";

public:

    // check if the current line id matches the id given
    static bool idMatch(unsigned long long id, const string &s);


    // remove the old file
    bool removeOldFile() const;

    // the executing to operate all the code together
    string execute(unsigned long long id) override;

};


#endif //DELETELINEFILE_H
