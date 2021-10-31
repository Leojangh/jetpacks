#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <android/hardware_buffer_jni.h>
#include <android/log.h>
#include <jni.h>

#include "ImageProcessor.h"

namespace {

    using sample::ImageProcessor;

    ImageProcessor *castToImageProcessor(jlong handle) {
        return reinterpret_cast<ImageProcessor *>(static_cast<uintptr_t>(handle));
    }

}  // namespace

extern "C" JNIEXPORT jlong JNICALL
Java_com_genlz_jetpacks_utility_imageprocessor_VulkanImageProcessor_initVulkanProcessor(
        JNIEnv *env, jobject /* this */, jobject _assetManager) {
    auto *assetManager = AAssetManager_fromJava(env, _assetManager);
    RET_CHECK(assetManager != nullptr);
    auto processor = ImageProcessor::create(/*enableDebug=*/true, assetManager);
    return static_cast<jlong>(reinterpret_cast<uintptr_t>(processor.release()));
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_genlz_jetpacks_utility_imageprocessor_VulkanImageProcessor_configureInputAndOutput(
        JNIEnv *env, jobject /* this */, jlong _processor, jobject _inputBitmap,
        jint _numberOfOutputImages) {
    if (_processor == 0L) return false;
    return castToImageProcessor(_processor)
            ->configureInputAndOutput(env, _inputBitmap, _numberOfOutputImages);
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_genlz_jetpacks_utility_imageprocessor_VulkanImageProcessor_getOutputHardwareBuffer(
        JNIEnv *env, jobject /* this */, jlong _processor, jint _index) {
    if (_processor == 0L) return nullptr;
    auto *ahwb = castToImageProcessor(_processor)->getOutputAHardwareBuffer(_index);
    return AHardwareBuffer_toHardwareBuffer(env, ahwb);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_genlz_jetpacks_utility_imageprocessor_VulkanImageProcessor_rotateHue(JNIEnv * /* env */,
                                                                              jobject /* this */,
                                                                              jlong _processor,
                                                                              jfloat _radian,
                                                                              jint _outputIndex) {
    if (_processor == 0L) return false;
    return castToImageProcessor(_processor)->rotateHue(_radian, _outputIndex);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_genlz_jetpacks_utility_imageprocessor_VulkanImageProcessor_blur(JNIEnv * /* env */,
                                                                         jobject /* this */,
                                                                         jlong _processor,
                                                                         jfloat _radius,
                                                                         jint _outputIndex) {
    if (_processor == 0L) return false;
    return castToImageProcessor(_processor)->blur(_radius, _outputIndex);
}

extern "C" JNIEXPORT void JNICALL
Java_com_genlz_jetpacks_utility_imageprocessor_VulkanImageProcessor_destroyVulkanProcessor(
        JNIEnv * /* env */,
        jobject /* this */,
        jlong _processor) {
    if (_processor == 0L) return;
    delete castToImageProcessor(_processor);
}
