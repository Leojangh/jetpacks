#include <jni.h>
#include "libnative_api.h"

extern "C"
JNIEXPORT jint JNICALL
Java_com_genlz_jetpacks_App_00024Companion_androidJni(JNIEnv *env, jobject thiz) {
    return libnative_symbols()->kotlin.root.functionInNative();
}