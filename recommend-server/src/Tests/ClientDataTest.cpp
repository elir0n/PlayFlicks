//
// Created by Lavi Shpaisman on 12/17/2024.
//

#include "../Controller/ClientData.h"

#include <gtest/gtest.h>
#include "../Model/Help.h"

// Test the getCommand function
TEST(ClientDataTest, getData) {
    string command = "help";

    map<string, ICommandAction *> commands;
    commands["help"] = new Help();
    string response;
    User u(-1, {5});
    // check correct
    bool x = ClientData::getData(command, response, u, commands);
    // check if the command is correct
    EXPECT_EQ("help", response);
    // check if the user is correct
    EXPECT_EQ(x, true);

    // check incorect
    command = "help 5 7 8";

    x = ClientData::getData(command, response, u, commands);
    // check if the command is correct
    EXPECT_EQ(x, false);
}
