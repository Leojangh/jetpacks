#include <jni.h>
#include "libnative_api.h"

extern "C"
JNIEXPORT jint JNICALL
Java_com_genlz_jetpacks_libnative_CppNatives_androidJni(JNIEnv *env, jclass clazz) {
    return libnative_symbols()->kotlin.root.functionInNative();
}