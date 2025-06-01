//
// Created by Lavi Shpaisman on 12/5/2024.
//

#ifndef APP_H
#define APP_H
#include <string>

// add the movie to the list
void addMovie(unsigned long long movieId);

// convert the input to the desired format
bool convertInput(std::string line);

// run the program
void run();

// main function
int main();
#endif //APP_H
