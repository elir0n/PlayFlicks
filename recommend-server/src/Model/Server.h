//
// Created by Lavi Shpaisman on 12/17/2024.
//

#ifndef SERVER_H
#define SERVER_H

// delete all the map values
void deleteMap();

// handles what happens when a user exits with ctr+c
void signalHandler(int sig);

// initializes the socket to listen to clients
void serverSocketInitialize();

// listen for user connections
void listenToUser();

// handles the server-user interactions
void handleUser(int clientSock);

// sends the text to the user
void sendText(const string &text, const int clientSock);


#endif //SERVER_H
