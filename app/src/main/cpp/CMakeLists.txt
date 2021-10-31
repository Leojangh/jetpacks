cmake_minimum_required(VERSION 3.10.2)
project(rs_migration_jni)

add_library(rs_migration_jni
        SHARED
        RsMigration_jni.cpp
        ComputePipeline.cpp
        ImageProcessor.cpp
        VulkanContext.cpp
        VulkanResources.cpp)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++1z")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Weverything -Werror")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-c++98-compat-pedantic")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-c99-extensions")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-padded")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-switch-enum")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-missing-prototypes")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-sign-conversion")

find_library(libandroid android)
find_library(liblog log)
find_library(libjnigraphics jnigraphics)
find_library(libvulkan vulkan)
target_link_libraries(rs_migration_jni
        ${libandroid}
        ${liblog}
        ${libjnigraphics}
        ${libvulkan})
