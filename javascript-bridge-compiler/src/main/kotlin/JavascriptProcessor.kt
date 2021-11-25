import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/**
 * APT
 */
class JavascriptProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Javascript::class.java.canonicalName)
    }

    override fun process(p0: MutableSet<out TypeElement>?, environment: RoundEnvironment): Boolean {
        environment.getElementsAnnotatedWith(Javascript::class.java).filterIsInstance<TypeElement>()
            .map(::process0)
        return true
    }

    private fun process0(typeElement: TypeElement) {
        val classBuilder = TypeSpec.classBuilder(typeElement.simpleName.toString())
        val constructor = typeElement.enclosedElements.filterIsInstance<ExecutableElement>()
            .first { it.simpleName.toString() == "<init>" }

    }
}