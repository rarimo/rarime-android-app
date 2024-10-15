cmake_minimum_required(VERSION 3.22.1)

project("rarime")

include_directories(include)

link_directories(lib)

add_library(rapidsnark SHARED IMPORTED)
set_target_properties(rapidsnark PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/librapidsnark.so)


add_library(query_identity SHARED IMPORTED)
set_target_properties(query_identity PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_queryIdentity.so)

add_library(auth SHARED IMPORTED)
set_target_properties(auth PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_auth.so)

#registration libs
add_library(registerIdentity_1_256_3_5_576_248_NA SHARED IMPORTED)
set_target_properties(registerIdentity_1_256_3_5_576_248_NA PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_1_256_3_5_576_248_NA.so)


add_library(registerIdentity_1_256_3_6_576_248_1_2432_5_296 SHARED IMPORTED)
set_target_properties(registerIdentity_1_256_3_6_576_248_1_2432_5_296 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_1_256_3_6_576_248_1_2432_5_296.so)


add_library(registerIdentity_2_256_3_6_336_264_21_2448_6_2008 SHARED IMPORTED)
set_target_properties(registerIdentity_2_256_3_6_336_264_21_2448_6_2008 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_2_256_3_6_336_264_21_2448_6_2008.so)


add_library(registerIdentity_21_256_3_7_336_264_21_3072_6_2008 SHARED IMPORTED)
set_target_properties(registerIdentity_21_256_3_7_336_264_21_3072_6_2008 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_21_256_3_7_336_264_21_3072_6_2008.so)


add_library(${CMAKE_PROJECT_NAME} SHARED
        rarime.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log rapidsnark query_identity auth
        registerIdentity_1_256_3_5_576_248_NA
        registerIdentity_1_256_3_6_576_248_1_2432_5_296
        registerIdentity_2_256_3_6_336_264_21_2448_6_2008
        registerIdentity_21_256_3_7_336_264_21_3072_6_2008
)