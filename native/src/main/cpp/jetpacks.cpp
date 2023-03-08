#include <jni.h>
#include "libnative_api.h"
#include "sched.h"
#include <cerrno>
#include <iostream>
#include "android/log.h"

#define TAG "CppNatives"

#define LOG_E(...) __android_log_print(ANDROID_LOG_ERROR,    TAG, __VA_ARGS__)
#define LOG_W(...) __android_log_print(ANDROID_LOG_WARN,     TAG, __VA_ARGS__)
#define LOG_I(...) __android_log_print(ANDROID_LOG_INFO,     TAG, __VA_ARGS__)
#define LOG_D(...) __android_log_print(ANDROID_LOG_DEBUG,    TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jint JNICALL
Java_com_genlz_jetpacks_libnative_CppNatives_androidJni(JNIEnv *env, jclass clazz) {
    return libnative_symbols()->kotlin.root.functionInNative();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_genlz_jetpacks_libnative_CppNatives_setAffinity(JNIEnv *env, jclass clazz, jint tid,
                                                         jintArray cpus) {
    cpu_set_t mask;
    CPU_ZERO(&mask);
    jsize len = env->GetArrayLength(cpus);
    for (int i = 0; i < len; ++i) {
        jint *e = env->GetIntArrayElements(cpus, nullptr);
        CPU_SET(*e, &mask);
        env->ReleaseIntArrayElements(cpus, e, JNI_COMMIT);
    }
    int r = sched_setaffinity(tid, sizeof(cpu_set_t), &mask);
    if (r != 0) {
        auto cls = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(cls, strerror(errno));
    }
    return 0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_genlz_jetpacks_libnative_CppNatives_whereAmIRunning(JNIEnv *env, jclass clazz) {
    return sched_getcpu();
}