package com.genlz.jetpacks.threadaffinity

/**
 * 用于指示现在大小核架构的CPU的架构模式，例如SM8475的1+3+4的组合模式，SM8550的1+2+2+3
 */
interface CpuLayout {

    /**
     * 返回"大核"的CPU ids，在具有超大核的语义环境下，它可能表示中核。也就是"1+3+4"中的"3"。
     * 若核心频率一致，它也表示所有核心。
     */
    fun bigCores(): IntArray

    /**
     * 返回小核的CPU ids
     */
    fun littleCores(): IntArray

    /**
     * 返回超大核的cpu ids
     */
    fun primeCores(): IntArray

    fun cores(): List<Core>

    companion object Impl : CpuLayout by CpuLayoutImpl()
}

