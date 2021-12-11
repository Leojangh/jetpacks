package com.genlz.javascript.processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class Provider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        JavascriptBridgeProcessor(environment.codeGenerator, environment.logger)
}