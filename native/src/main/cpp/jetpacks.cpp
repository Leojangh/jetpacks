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
#include <vector>
#include <string>
#include <regex>

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


class BytePattern {
public:
    virtual bool matches(std::vector<uint8_t> &target) = 0;
};

class KmpImpl : public BytePattern {
private:
    std::vector<uint16_t> pattern;

private:
    static int kmp(const std::vector<uint8_t> &target,
                   const uint32_t from,
                   const uint32_t to,
                   const std::vector<uint16_t> &pattern) {

        uint32_t m = pattern.size();
        std::vector<int> lps = computeLPS(pattern);
        uint32_t i = from;
        uint32_t j = 0;
        while (i < (to - from)) {
            if (pattern[j] == 0xffff || target[i] == pattern[j]) {
                i++;
                j++;
            }
            if (j == m) {
                return i - j;
            } else if (i < (to - from) && (pattern[j] != 0xffff && target[i] != pattern[j])) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return -1;
    }

private:
    static std::vector<int> computeLPS(const std::vector<uint16_t> &pattern) {
        int n = (int) pattern.size();
        std::vector<int> lps(n, 0);
        int len = 0;
        int i = 1;
        while (i < n) {
            if (pattern[i] == pattern[len]) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

public:
    explicit KmpImpl(std::string pattern) {
        std::regex ws_re("\\s+"); // whitespace
        std::vector<std::string> v(
                std::sregex_token_iterator(pattern.begin(), pattern.end(), ws_re, -1),
                std::sregex_token_iterator());
        std::vector<uint16_t> patternArray;
        for (auto &&s: v) {
            int byte = std::stoi(s, nullptr, 16);
            patternArray.push_back(byte);
        }
        this->pattern = patternArray;
    }

    bool matches(std::vector<uint8_t> &target) override {
        return kmp(target, 0, target.size(), pattern) != -1;
    }
};

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_genlz_jetpacks_libnative_CppNatives_search(JNIEnv *env, jclass clazz, jstring target,
                                                    jstring pattern) {
    KmpImpl kmp("ca fe ba be");
    std::vector<uint8_t> a = std::vector<uint8_t>();
    a.push_back(0xff);
    a.push_back(0xca);
    a.push_back(0xfe);
    a.push_back(0xba);
    a.push_back(0xbe);
    return kmp.matches(a);
}