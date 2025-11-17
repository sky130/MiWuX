package miwu.processor.icon


import com.google.devtools.ksp.processing.SymbolProcessorEnvironment as Environment
import com.google.devtools.ksp.processing.SymbolProcessorProvider as Provider


internal class ComposeIconsProcessorProvider : Provider {
    override fun create(environment: Environment) = ComposeIconsProcessor(
        options = environment.options,
        codeGenerator = environment.codeGenerator,
        logger = environment.logger
    )
}



