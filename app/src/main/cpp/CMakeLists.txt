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

add_library(registerIdentity_1_256_3_6_576_264_1_2448_3_256 SHARED IMPORTED)
set_target_properties(registerIdentity_1_256_3_6_576_264_1_2448_3_256 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_1_256_3_6_576_264_1_2448_3_256.so)

add_library(registerIdentity_2_256_3_6_336_248_1_2432_3_256 SHARED IMPORTED)
set_target_properties(registerIdentity_2_256_3_6_336_248_1_2432_3_256 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_2_256_3_6_336_248_1_2432_3_256.so)

add_library(registerIdentity_2_256_3_6_576_248_1_2432_3_256 SHARED IMPORTED)
set_target_properties(registerIdentity_2_256_3_6_576_248_1_2432_3_256 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_2_256_3_6_576_248_1_2432_3_256.so)


add_library(registerIdentity_11_256_3_3_576_248_1_1184_5_264 SHARED IMPORTED)
set_target_properties(registerIdentity_11_256_3_3_576_248_1_1184_5_264 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_11_256_3_3_576_248_1_1184_5_264.so)

add_library(registerIdentity_12_256_3_3_336_232_NA SHARED IMPORTED)
set_target_properties(registerIdentity_12_256_3_3_336_232_NA PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_12_256_3_3_336_232_NA.so)

add_library(registerIdentity_1_256_3_4_336_232_1_1480_5_296 SHARED IMPORTED)
set_target_properties(registerIdentity_1_256_3_4_336_232_1_1480_5_296 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_1_256_3_4_336_232_1_1480_5_296.so)

add_library(registerIdentity_1_256_3_4_600_248_1_1496_3_256 SHARED IMPORTED)
set_target_properties(registerIdentity_1_256_3_4_600_248_1_1496_3_256 PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_1_256_3_4_600_248_1_1496_3_256.so)



add_library(registerIdentity_1_160_3_4_576_200_NA SHARED IMPORTED)
set_target_properties(registerIdentity_1_160_3_4_576_200_NA PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_1_160_3_4_576_200_NA.so)

add_library(registerIdentity_21_256_3_3_336_232_NA SHARED IMPORTED)
set_target_properties(registerIdentity_21_256_3_3_336_232_NA PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_21_256_3_3_336_232_NA.so)

add_library(registerIdentity_24_256_3_4_336_232_NA SHARED IMPORTED)
set_target_properties(registerIdentity_24_256_3_4_336_232_NA PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/libwitnesscalc_registerIdentity_24_256_3_4_336_232_NA.so)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -D__STDC_FORMAT_MACROS -D__STDC_LIMIT_MACROS -O2 -fPIC -fstack-protector-strong -fsanitize-address-use-after-scope -Wl")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -D__STDC_FORMAT_MACROS -D__STDC_LIMIT_MACROS -O2 -fPIC -fstack-protector-strong -fsanitize-address-use-after-scope -Wl")

add_library(${CMAKE_PROJECT_NAME} SHARED
        rarime.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log rapidsnark query_identity auth
        registerIdentity_1_256_3_5_576_248_NA
        registerIdentity_1_256_3_6_576_248_1_2432_5_296
        registerIdentity_2_256_3_6_336_264_21_2448_6_2008
        registerIdentity_21_256_3_7_336_264_21_3072_6_2008
        registerIdentity_1_256_3_6_576_264_1_2448_3_256
        registerIdentity_2_256_3_6_336_248_1_2432_3_256
        registerIdentity_2_256_3_6_576_248_1_2432_3_256

        registerIdentity_11_256_3_3_576_248_1_1184_5_264
        registerIdentity_12_256_3_3_336_232_NA
        registerIdentity_1_256_3_4_336_232_1_1480_5_296
        registerIdentity_1_256_3_4_600_248_1_1496_3_256

        registerIdentity_1_160_3_4_576_200_NA
        registerIdentity_21_256_3_3_336_232_NA
        registerIdentity_24_256_3_4_336_232_NA
)