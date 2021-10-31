#ifndef RENDERSCRIPT_MIGRATION_SAMPLE_COMPUTE_PIPELINE_H
#define RENDERSCRIPT_MIGRATION_SAMPLE_COMPUTE_PIPELINE_H

#include <android/asset_manager_jni.h>
#include <vulkan/vulkan_core.h>

#include <chrono>
#include <memory>

#include "VulkanContext.h"
#include "VulkanResources.h"

namespace sample {

// ComputePipeline manages the Vulkan objects for a single compute task with a compute shader.
// In this sample app, the compute shaders always take 2D images as the input and output, with
// runtime parameters passed by an uniform buffer. The image and buffer resources are managed
// outside of this class.
    class ComputePipeline {
    public:
        // Create a compute pipeline with the input shader.
        // Return the created ComputePipeline on success, or nullptr if failed.
        static std::unique_ptr<ComputePipeline>
        create(const VulkanContext *context, const char *shader,
               AAssetManager *assetManager,
               uint32_t pushConstantSize,
               bool useUniformBuffer);

        // Prefer ComputePipeline::create
        ComputePipeline(const VulkanContext *context, uint32_t pushConstantSize)
                : mContext(context),
                  mDescriptorSetLayout(context->device()),
                  mPipelineLayout(context->device()),
                  mPipeline(context->device()),
                  mPushConstantSize(pushConstantSize) {}

        // Record the compute pipeline to the command buffer with the given uniform buffer and
        // input/output image.
        void recordComputeCommands(VkCommandBuffer cmd, const void *pushConstantData,
                                   const Image &inputImage, const Image &outputImage,
                                   const Buffer *uniformBuffer = nullptr);

    protected:
        // Initialization
        bool createDescriptorSet(bool useUniformBuffer);

        bool createComputePipeline(const char *shader, AAssetManager *assetManager);

        // Update descriptor sets with the given input and output image.
        bool updateDescriptorSets(const Image &inputImage, const Image &outputImage,
                                  const Buffer *uniformBuffer);

        // Context
        const VulkanContext *mContext;

        // Compute pipeline
        VulkanDescriptorSetLayout mDescriptorSetLayout;
        VulkanPipelineLayout mPipelineLayout;
        VkDescriptorSet mDescriptorSet = VK_NULL_HANDLE;
        VulkanPipeline mPipeline;
        uint32_t mPushConstantSize;
    };

}  // namespace sample

#endif  // RENDERSCRIPT_MIGRATION_SAMPLE_COMPUTE_PIPELINE_H
