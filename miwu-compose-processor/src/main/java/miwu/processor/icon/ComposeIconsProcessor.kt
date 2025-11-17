package miwu.processor.icon

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo

class ComposeIconsProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private var isProcessingOver = false
    private val drawableList = mutableListOf<String>()
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isProcessingOver) return emptyList()
        try {
            val iconProperties = extractIconProperties(resolver) ?: return emptyList()
            generateComposeIconsImplementation(iconProperties)
        } catch (e: Exception) {
            logger.error("Failed to process Compose icons")
            e.printStackTrace()
        }
        isProcessingOver = true
        return emptyList()
    }

    private fun extractIconProperties(resolver: Resolver): List<IconProperty>? {
        val iconsInterface = resolver.getClassDeclarationByName(ICONS_INTERFACE_NAME)
        if (iconsInterface == null) {
            logger.error("Icons interface not found: $ICONS_INTERFACE_NAME")
            return null
        }
        val properties = iconsInterface.getAllProperties().toList()
        if (properties.isEmpty()) {
            logger.warn("No properties found in Icons interface")
            return emptyList()
        }
        return properties.mapNotNull { property ->
            val propertyName = property.simpleName.asString()
            if (propertyName.isValidIconName()) {
                IconProperty(propertyName)
            } else {
                logger.warn("Skipping invalid icon property name: $propertyName")
                null
            }
        }
    }

    private fun generateComposeIconsImplementation(iconProperties: List<IconProperty>) {
        val composeIconsObject = createComposeIconsObject(iconProperties)
        val fileSpec = createFileSpec(composeIconsObject)
        try {
            fileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
            logger.info("Generated ComposeIcons with ${iconProperties.size} properties")
        } catch (e: Exception) {
            logger.error("Failed to write ComposeIcons file")
        }
    }

    private fun createComposeIconsObject(iconProperties: List<IconProperty>): TypeSpec {
        return TypeSpec.objectBuilder(OBJECT_NAME)
            .addSuperinterface(SUPER_INTERFACE)
            .apply {
                iconProperties.forEach { iconProperty ->
                    addProperty(createIconProperty(iconProperty))
                }
            }
            .build()
    }

    private fun createIconProperty(iconProperty: IconProperty): PropertySpec {
        val drawableResourceName = iconProperty.name.camelToSnakeCase()
        drawableList.add("ic_$drawableResourceName")
        return PropertySpec.builder(iconProperty.name, COMPOSE_ICON_CLASS)
            .addModifiers(KModifier.OVERRIDE)
            .initializer(
                "%T(Res.drawable.ic_%L)",
                COMPOSE_ICON_CLASS,
                drawableResourceName
            )
            .build()
    }

    private fun createFileSpec(composeIconsObject: TypeSpec): FileSpec {
        return FileSpec.builder(PACKAGE_NAME, OBJECT_NAME)
            .addType(composeIconsObject)
            .addImport(COMPOSE_ICON_PACKAGE, COMPOSE_ICON_SIMPLE_NAME)
            .addImport(MIWU_COMMON_RESOURCES, "Res")
            .addImport(MIWU_COMMON_RESOURCES, drawableList)
            .build()
    }

    private fun String.camelToSnakeCase(): String {
        return this
            .replace(CAMEL_TO_SNAKE_REGEX_1, "$1_$2")
            .replace(CAMEL_TO_SNAKE_REGEX_2, "$1_$2")
            .lowercase()
    }

    private fun String.isValidIconName(): Boolean {
        return isNotEmpty() &&
                first().isJavaIdentifierStart() &&
                all { it.isJavaIdentifierPart() }
    }

    private data class IconProperty(val name: String)

    companion object {
        private const val PACKAGE_NAME = "miwu.compose.icon.generated.icon"
        private const val OBJECT_NAME = "ComposeIcons"
        private const val ICONS_INTERFACE_NAME = "miwu.icon.Icons"
        private const val MIWU_COMMON_RESOURCES = "miwu.common.resources"

        private val SUPER_INTERFACE = ClassName("miwu.icon", "Icons")
        private val COMPOSE_ICON_CLASS = ClassName("miwu.compose.icon", "ComposeIcon")

        private const val COMPOSE_ICON_PACKAGE = "miwu.compose.icon"
        private const val COMPOSE_ICON_SIMPLE_NAME = "ComposeIcon"

        private val CAMEL_TO_SNAKE_REGEX_1 = Regex("([a-z])([A-Z])")
        private val CAMEL_TO_SNAKE_REGEX_2 = Regex("([A-Z])([A-Z][a-z])")
    }
}
