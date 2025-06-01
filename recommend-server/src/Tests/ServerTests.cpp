#include "gtest/gtest.h"

TEST(ServerTest, all) {
    // don't do EXCEPT or ASSERT because we don't know what will be in the docker user_data.txt
    const std::string command =
            "python3 /app/src/Tests/ServerTestFiles/client1.py server 8096 < /app/src/Tests/ServerTestFiles/input1.txt &"
            "python3 /app/src/Tests/ServerTestFiles/client2.py server 8096 <  /app/src/Tests/ServerTestFiles/input2.txt & "
            "python3 /app/src/Tests/ServerTestFiles/client3.py server 8096 < /app/src/Tests/ServerTestFiles/input3.txt &"
            "python3 /app/src/Tests/ServerTestFiles/client4.py server 8096 < /app/src/Tests/ServerTestFiles/input4.txt";
    system(command.c_str());
}
