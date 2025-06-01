//
// Created by Lavi Shpaisman on 12/18/2024.
//

#include "gtest/gtest.h"
#include "../Model/Help.h"

TEST(HelpTest, BasicTest) {
    // checking if output correctly
    const std::string expected =
            "DELETE, arguments: [userid] [movieid1] [movieid2] ...\n"
            "GET, arguments: [userid] [movieid]\n"
            "help\n"
            "PATCH, arguments: [userid] [movieid1] [movieid2] ...\n"
            "POST, arguments: [userid] [movieid1] [movieid2] ...";

    auto help = Help();
    // dummy User
    const User u(1, {2, 3});
    const std::string output = help.execute(u);

    EXPECT_EQ(output, expected);
}
