#include "ThreadPool.h"
#include <iostream> // Optional: For debugging purposes

ThreadPool::ThreadPool(const int threadsNumber) : stop(false) {
    for (int i = 0; i < threadsNumber; i++) {
        threads.emplace_back(&ThreadPool::Threads, this);
    }
}

void ThreadPool::Threads() {
    while (true) {
        std::function<void(int)> task;
        int arg; {
            std::unique_lock<std::mutex> lock(mutex);
            // Wait until there are tasks in the queue or the pool is stopping
            condition.wait(lock, [this] {
                return stop || !queue.empty();
            });

            // Exit if stopping, and no tasks are left
            if (stop && queue.empty()) {
                return;
            }

            // Fetch the next task
            task = std::move(queue.front().first);
            arg = queue.front().second;
            queue.pop();
        }

        task(arg);
    }
}

// remove threads
ThreadPool::~ThreadPool() { {
        std::unique_lock<std::mutex> lock(mutex);
        stop = true;
    }
    condition.notify_all();

    for (std::thread &t: threads) {
        if (t.joinable()) {
            t.join();
        }
    }
}

// add the task (func)
void ThreadPool::enqueue(std::function<void(int)> func, int arg) { {
        std::unique_lock<std::mutex> lock(mutex);
        // add the pair of the func and attr
        queue.emplace(std::move(func), arg);
    }
    // notify one thread
    condition.notify_one();
}
