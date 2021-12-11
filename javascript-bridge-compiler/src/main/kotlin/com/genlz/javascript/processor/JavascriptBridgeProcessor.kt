package com.genlz.javascript.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * KSP
 */
class JavascriptBridgeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation("com.genlz.javascript.Javascript")
        val ret = symbols.filter { !it.validate() }.toList()
        symbols.filter { it is KSClassDeclaration && it.validate() }.forEach { ks ->
            ks.accept(Visitor(), Unit)
        }
        return ret
    }

    inner class Visitor : KSVisitorVoid() {

        private val ln = System.lineSeparator()

        private lateinit var writer: BufferedWriter

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val className = classDeclaration.simpleName.asString()
            writer = codeGenerator.createNewFile(
                Dependencies(true, classDeclaration.containingFile!!),
                "",
                className
            ).wrap()
            writer.use { file ->
                file.newLine()
                file.append("external object $className {$ln$ln")
                classDeclaration.getAllFunctions().forEach { it.accept(this, data) }
                file.newLine()
                file.append("}$ln")
            }
        }

        /**
         *
         */
        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            if (function.simpleName.asString() in setOf("equals", "hashCode", "toString")) return
            writer.append("fun ${function.simpleName.asString()}(")
            function.parameters.forEach {
                val name = it.name!!.asString()
                val typeName = it.type.resolve().declaration.qualifiedName?.asString() ?: "<ERROR>"
                writer.append("$name:$typeName,")
            }
            writer.append(")$ln$ln")
        }
    }

    companion object {
        fun OutputStream.wrap(): BufferedWriter = BufferedWriter(OutputStreamWriter(this))
    }
}

