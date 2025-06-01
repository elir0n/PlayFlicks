//
// Created by Lavi Shpaisman on 12/15/2024.
//

#include <string>

#include "User.h"
#include <iostream>
#include <sys/socket.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <csignal>
#include <string.h>
#include <climits> // For LLONG_MAX
#include <map>
#include <thread>
#include <mutex>
#include "Server.h"
#include <bits/stl_algo.h>

#include "DELETE.h"
#include "Help.h"
#include "PATCH.h"
#include "POST.h"
#include "GET.h"
#include "../Controller/ClientData.h"
#include "../Controller/ThreadPool.h"

struct sockaddr;
class ICommandAction;
using namespace std;

constexpr int port = 8096;

// mutex for lock
mutex m;

// all the commands
map<string, ICommandAction *> commands;

// all the threads
vector<thread> threads;

int serverSock = -1;
vector<int> clientsSocks;
ThreadPool pool(100);

int main()
{
    std::signal(SIGINT, signalHandler);
    commands["POST"] = new POST();
    commands["PATCH"] = new PATCH();
    commands["help"] = new Help();
    commands["DELETE"] = new DELETE();
    commands["GET"] = new GET();
    serverSocketInitialize();
    listenToUser();
}

// // handle the ctr+c
void signalHandler(const int sig)
{
    if (SIGINT != sig)
    {
        return;
    }
    // locking so threads will not try to over delete
    m.lock();
    for (const int client : clientsSocks)
    {
        close(client);
    }
    close(serverSock);
    deleteMap();

    exit(0);
}

// initialize the server socket
void serverSocketInitialize()
{
    serverSock = socket(AF_INET, SOCK_STREAM, 0);

    if (serverSock < 0)
    {
        perror("error creating socket");
    }
    // get server attributes
    sockaddr_in serverSockAddr = {};
    serverSockAddr.sin_family = AF_INET;
    serverSockAddr.sin_port = htons(port);
    serverSockAddr.sin_addr.s_addr = INADDR_ANY;

    // see if the socket can open
    if (bind(serverSock, reinterpret_cast<sockaddr *>(&serverSockAddr), sizeof(serverSockAddr)) < 0)
    {
    }
}

// listen to user
void listenToUser()
{
    // listen to user infinite
    while (true)
    {
        if (listen(serverSock, INT_MAX) < 0)
        {
            continue;
        }

        // get a clientSock
        sockaddr_in clientSockAddr = {};

        unsigned int addrLen = sizeof(clientSockAddr);
        int clientSock = accept(serverSock, reinterpret_cast<struct sockaddr *>(&clientSockAddr), &addrLen);
        if (clientSock < 0)
        {
            continue;
        }
        // execute its logic in a different thread
        clientsSocks.push_back(clientSock);

        pool.enqueue(handleUser, clientSock);
    }
}

// handle the user
void handleUser(int clientSock)
{

    // listen for the client sock until
    while (true)
    {
        char buffer[4096] = {};

        constexpr int expected_data_len = sizeof(buffer);
        const int read_bytes = recv(clientSock, buffer, expected_data_len, 0);

        // the client sock was closed
        if (read_bytes == 0)
        {
            break;
        }

        // another error tries again
        if (read_bytes < 0)
        {
            continue;
        }

        string line(buffer);

        string txt = "400 Bad Request";

        string desiredOutput;

        cout << line << endl;
        string command;
        User user(-1, {});
        const bool good = ClientData::getData(line, command, user, commands);
        cout << good << endl;

        if (!good)
        {
            sendText(txt, clientSock);
            continue;
        }

        string exitCode;
        string output;
        if (command != "help")
        {
            m.lock();
            output = commands[command]->execute(user);
            exitCode = commands[command]->getExitCode();
            m.unlock();
        }
        else
        {
            output = commands[command]->execute(user);
            exitCode = commands[command]->getExitCode();
        }

        // to have two \n
        if (command == "GET")
        {
            desiredOutput = exitCode;
            desiredOutput += "\n\n";
            desiredOutput += output;
        }
        else
        {
            desiredOutput = exitCode + output;
        }
        sendText(desiredOutput, clientSock);
    }
    // remove the clientSock
    clientsSocks.erase(std::remove(clientsSocks.begin(), clientsSocks.end(), clientSock), clientsSocks.end());
}

// send the text to the user
void sendText(const string &text, const int clientSock)
{
    const int sent_bytes = send(clientSock, text.c_str(), text.size(), 0);

    // error sending string
    if (sent_bytes < 0)
    {
    }
}

// delete all the map values
void deleteMap()
{
    // loop threw all map values and deleted them
    for (const auto &pair : commands)
    {
        delete pair.second;
        commands.erase(pair.first);
    }
}
