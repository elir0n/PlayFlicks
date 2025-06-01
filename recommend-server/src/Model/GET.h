#ifndef GET_H
#define GET_H

#include "ICommandAction.h"
#include "Recommend.h"
using namespace std;

// Class to get the recommendation for a user
class GET : public ICommandAction {
    vector<User> users;
    string exitCode;
    
    // get the recommendation
    string getRecommendation(const User &user);

public:
     // the command operations to execute
    string execute(User user) override;

    // get the Post exit code
    string getExitCode() override;
};

#endif //GET_H
    
