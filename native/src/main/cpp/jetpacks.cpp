#include <jni.h>
#include "libnative_api.h"
#include "sched.h"
#include "sys/prctl.h"
#include "sys/resource.h"
#include "syscall.h"
#include <cerrno>
#include <iostream>
#include "android/log.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/inotify.h>

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
    jint *arr = env->GetIntArrayElements(cpus, nullptr);
    for (int i = 0; i < env->GetArrayLength(cpus); ++i) {
        CPU_SET(arr[i], &mask);
    }
    env->ReleaseIntArrayElements(cpus, arr, JNI_COMMIT);
    int r = sched_setaffinity(tid, sizeof(cpu_set_t), &mask);
    if (r != 0) {
        env->ThrowNew(env->FindClass("java/lang/RuntimeException"), strerror(errno));
        return r;
    }
    return 0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_genlz_jetpacks_libnative_CppNatives_whereAmIRunning(JNIEnv *env, jclass clazz) {
    return sched_getcpu();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_genlz_jetpacks_libnative_INotifyBinding_inotifyInit(JNIEnv *env, jobject thiz) {
    return inotify_init();
}
