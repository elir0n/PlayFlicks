cmake_minimum_required(VERSION 3.10)
project(my_project)

set(CMAKE_CXX_STANDARD 11)

include(FetchContent)
FetchContent_Declare(
        googletest
        URL
        https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
        DOWNLOAD_EXTRACT_TIMESTAMP TRUE
)


set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

set(MODEL_FILES
        src/Model/Add.cpp
        src/Model/Add.h
        #

        src/Model/Help.cpp
        src/Model/Help.h

        src/Model/ICommandAction.h

        src/Model/ICommandInput.h

        src/Model/ICommandOutput.h

        src/Model/ICommandRemove.h

        src/Model/InputCLI.cpp
        src/Model/InputCLI.h

        src/Model/InputFile.cpp
        src/Model/InputFile.h

        src/Model/OutputCLI.cpp
        src/Model/OutputCLI.h

        src/Model/OutputFile.cpp
        src/Model/OutputFile.h

        src/Model/PATCH.cpp
        src/Model/PATCH.h

        src/Model/POST.cpp
        src/Model/POST.h

        src/Model/Recommend.cpp
        src/Model/Recommend.h


        src/Model/RemoveLineFile.cpp
        src/Model/RemoveLineFile.h
        #

        #
        src/Model/User.cpp
        src/Model/User.h
        #
        src/Model/UserDataHandler.cpp
        src/Model/UserDataHandler.h
        #
        src/Model/DELETE.cpp
        src/Model/DELETE.h

        src/Model/GET.cpp
        src/Model/GET.h
)

set(TEST_FILES
        src/Tests/AddTests.cpp

        src/Tests/UserDataHandlerTest.cpp

        src/Tests/InputCLITests.cpp

        src/Tests/InputFileTest.cpp

        src/Tests/OutputCLITests.cpp

        src/Tests/OutputFileTest.cpp

        src/Tests/PATCHTest.cpp

        src/Tests/POSTTest.cpp

        src/Tests/RecommendTest.cpp


        src/Tests/ServerTests.cpp
        src/Tests/UserTest.cpp
        src/Tests/ClientDataTest.cpp
        src/Tests/GETTest.cpp
        src/Tests/HelpTest.cpp
        src/Tests/DELETETest.cpp


)

set(VIEW_FILES
        src/View/client.py
)
set(CONTROLLER_FILES
        src/Controller/ClientData.cpp
        src/Controller/ClientData.h
        src/Controller/ThreadPool.cpp
        src/Controller/ThreadPool.h
)
add_executable(runTests ${TEST_FILES} ${CONTROLLER_FILES} ${MODEL_FILES})
add_executable(runServer ${MODEL_FILES} ${CONTROLLER_FILES} src/Model/Server.cpp
        src/Model/Server.h)


target_link_libraries(runTests gtest gtest_main)
