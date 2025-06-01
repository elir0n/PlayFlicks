//
// Created by Lavi Shpaisman on 12/3/2024.
//

#ifndef ICOMMANDREMOVE_H
#define ICOMMANDREMOVE_H

#include <string>
using namespace std;
// Interface for the command remove
class ICommandRemove {
public:
    virtual ~ICommandRemove() = default;

    //  ICommand method to delete line, depends on where it stores(by id)
    virtual string execute(unsigned long long id) = 0;

};
#endif //ICOMMANDREMOVE_H
