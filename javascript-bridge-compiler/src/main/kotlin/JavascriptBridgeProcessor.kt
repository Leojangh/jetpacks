import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStream

/**
 * KSP
 */
class JavascriptBridgeProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation("com.genlz.share.widget.web.bridge.Javascript")
        println(symbols)
        val ret = symbols.filter { !it.validate() }.toList()
        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(Visitor(), Unit) }
        return ret
    }

    inner class Visitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            println(classDeclaration)
            logger.logging(classDeclaration.toString())
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            println(function)
            logger.logging(function.toString())
        }
    }

    class Provider : SymbolProcessorProvider {
        override fun create(environment: SymbolProcessorEnvironment) =
            JavascriptBridgeProcessor(environment.codeGenerator, environment.logger)
    }

    companion object {
        fun OutputStream.appendText(str: String) {
            this.write(str.toByteArray())
        }
    }
}

