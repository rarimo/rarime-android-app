cmake_minimum_required(VERSION 3.22.1)

project("rarime")

include_directories(include)

link_directories(lib)

add_library(rapidsnark SHARED IMPORTED)
set_target_properties(rapidsnark PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/librapidsnark.so)


add_library(register_identity_universal SHARED IMPORTED)
set_target_properties(register_identity_universal PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentityUniversal.so)


add_library(query_identity SHARED IMPORTED)
set_target_properties(query_identity PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_queryIdentity.so)

add_library(auth SHARED IMPORTED)
set_target_properties(auth PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_auth.so)


add_library(${CMAKE_PROJECT_NAME} SHARED
        rarime.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log  rapidsnark register_identity_universal query_identity auth)