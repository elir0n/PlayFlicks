//
// Created by Lavi Shpaisman on 12/30/2024.
//

#ifndef THREADPOOL_H
#define THREADPOOL_H
#include <atomic>
#include <condition_variable>
#include <functional>
#include <mutex>
#include <queue>
#include <thread>
#include <vector>


class ThreadPool {
    std::vector<std::thread> threads;
    std::queue<std::pair<std::function<void(int)>, int> > queue;
    std::mutex mutex;
    std::condition_variable condition;
    std::atomic<bool> stop{false};

public:
    explicit ThreadPool(int threadsNumber);

    void Threads();

    ~ThreadPool();

    void enqueue(std::function<void(int)> func, int arg);
};


#endif //THREADPOOL_H
