#include <gtest/gtest.h>

#include "../Model/User.h"

std::vector<unsigned long long> x = {123, 456, 678};

User u(123, x);

// test if user id is correct
TEST(UserTest, getID) {
    EXPECT_EQ(123, u.getId());
}

// test it if adding movie correctly to user class
std::vector<unsigned long long> x1 = {123};

// test if the movie is added correctly
TEST(UserTest, addMovie) {
    u.addMovies(x1);
    x = {567, 654, 345};
}

// test if the num of common movie match
TEST(UserTest, communMovies) {
    const User u1(23345, {456, 6789, 123434});
    EXPECT_EQ(1, u.numOfCommonMovies(u1));
}
