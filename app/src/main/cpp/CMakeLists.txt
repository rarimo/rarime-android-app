cmake_minimum_required(VERSION 3.22.1)
project("rarime")

include_directories(include)
include_directories(${CMAKE_SOURCE_DIR}/Include/light)

link_directories(lib)
link_directories(${CMAKE_SOURCE_DIR}/lib/include)

set(NORMAL_LIB_FILES
        librapidsnark.so
        libwitnesscalc_queryIdentity.so
        libwitnesscalc_auth.so
        libwitnesscalc_faceRegistryNoInclusion.so

        libwitnesscalc_registerIdentity_1_256_3_5_576_248_NA.so
        libwitnesscalc_registerIdentity_1_256_3_6_576_248_1_2432_5_296.so
        libwitnesscalc_registerIdentity_2_256_3_6_336_264_21_2448_6_2008.so
        libwitnesscalc_registerIdentity_21_256_3_7_336_264_21_3072_6_2008.so
        libwitnesscalc_registerIdentity_1_256_3_6_576_264_1_2448_3_256.so
        libwitnesscalc_registerIdentity_2_256_3_6_336_248_1_2432_3_256.so
        libwitnesscalc_registerIdentity_2_256_3_6_576_248_1_2432_3_256.so
        libwitnesscalc_registerIdentity_11_256_3_3_576_248_1_1184_5_264.so
        libwitnesscalc_registerIdentity_12_256_3_3_336_232_NA.so
        libwitnesscalc_registerIdentity_1_256_3_4_336_232_1_1480_5_296.so
        libwitnesscalc_registerIdentity_1_256_3_4_600_248_1_1496_3_256.so
        libwitnesscalc_registerIdentity_1_160_3_4_576_200_NA.so
        libwitnesscalc_registerIdentity_21_256_3_3_336_232_NA.so
        libwitnesscalc_registerIdentity_24_256_3_4_336_232_NA.so
        libwitnesscalc_registerIdentity_1_160_3_3_576_200_NA.so
        libwitnesscalc_registerIdentity_20_256_3_3_336_224_NA.so
        libwitnesscalc_registerIdentity_1_256_3_3_576_248_NA.so
        libwitnesscalc_registerIdentity_10_256_3_3_576_248_1_1184_5_264.so
        libwitnesscalc_registerIdentity_11_256_3_5_576_248_1_1808_4_256.so
        libwitnesscalc_registerIdentity_21_256_3_3_576_232_NA.so

        libwitnesscalc_registerIdentity_2_256_3_6_336_264_1_2448_3_256.so
        libwitnesscalc_registerIdentity_3_160_3_3_336_200_NA.so
        libwitnesscalc_registerIdentity_3_160_3_4_576_216_1_1512_3_256.so

        libwitnesscalc_registerIdentity_11_256_3_3_576_240_1_864_5_264.so
        libwitnesscalc_registerIdentity_11_256_3_5_576_248_1_1808_5_296.so
        libwitnesscalc_registerIdentity_21_256_3_4_576_232_NA.so


        libwitnesscalc_registerIdentity_1_256_3_6_336_248_1_2744_4_256.so
        libwitnesscalc_registerIdentity_1_256_3_6_336_560_1_2744_4_256.so
        libwitnesscalc_registerIdentity_4_160_3_3_336_216_1_1296_3_256.so
        libwitnesscalc_registerIdentity_11_256_3_3_336_248_NA.so
        libwitnesscalc_registerIdentity_14_256_3_4_336_64_1_1480_5_296.so
        libwitnesscalc_registerIdentity_20_160_3_3_736_200_NA.so
        libwitnesscalc_registerIdentity_15_512_3_3_336_248_NA.so
        libwitnesscalc_registerIdentity_20_256_3_5_336_72_NA.so
        libwitnesscalc_registerIdentity_21_256_3_5_576_232_NA.so
)

set(LIGHT_LIB_FILES
        libwitnesscalc_registerIdentityLight160.so
        libwitnesscalc_registerIdentityLight224.so
        libwitnesscalc_registerIdentityLight256.so
        libwitnesscalc_registerIdentityLight384.so
        libwitnesscalc_registerIdentityLight512.so
)

function(import_lib_normal filename)
    get_filename_component(name ${filename} NAME_WE)
    add_library(${name} SHARED IMPORTED)
    set_target_properties(${name} PROPERTIES
            IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/lib/${filename}"
            IMPORTED_SONAME "${filename}"
    )
    list(APPEND LIB_TARGETS ${name})

    set(LIB_TARGETS ${LIB_TARGETS} PARENT_SCOPE)
endfunction()

function(import_lib_light filename)
    get_filename_component(name ${filename} NAME_WE)
    add_library(${name} SHARED IMPORTED)
    set_target_properties(${name} PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/light/${filename})
    list(APPEND LIB_TARGETS ${name})
    set(LIB_TARGETS ${LIB_TARGETS} PARENT_SCOPE)
endfunction()

foreach (lib ${NORMAL_LIB_FILES})
    import_lib_normal(${lib})
endforeach ()

foreach (lib ${LIGHT_LIB_FILES})
    import_lib_light(${lib})
endforeach ()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O2 -fPIC -fstack-protector-strong -fsanitize-address-use-after-scope")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS}   -O2 -fPIC -fstack-protector-strong -fsanitize-address-use-after-scope")

add_library(${PROJECT_NAME} SHARED rarime.cpp)
target_link_libraries(${PROJECT_NAME} android log ${LIB_TARGETS})