package com.plugin.projectview.bottomfiles

import com.intellij.openapi.project.IndexNotReadyException
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * Fast, stub-backed classifier that categorizes Kotlin PSI elements
 * ([KtClassOrObject], [KtFile]) according to their structural kind and modifiers.
 */
object KotlinTypeClassifier {

    @JvmStatic
    fun classify(element: Any?): JavaTypeCategory? {
        if (element == null) return null

        return try {
            when (element) {
                is KtClassOrObject -> classifyClassOrObject(element)
                is KtFile -> classifyFile(element)
                else -> null
            }
        } catch (_: IndexNotReadyException) {
            JavaTypeCategory.CLASS
        } catch (_: Throwable) {
            null
        }
    }

    @JvmStatic
    fun classifyClassOrObject(ktClassOrObject: KtClassOrObject): JavaTypeCategory {
        if (!ktClassOrObject.isValid) {
            return JavaTypeCategory.CLASS
        }

        // 1. Singleton Object (object MyObject)
        if (ktClassOrObject is KtObjectDeclaration) {
            return JavaTypeCategory.OBJECT
        }

        if (ktClassOrObject is KtClass) {
            // 2. Annotation class (@annotation class MyAnnotation)
            if (ktClassOrObject.isAnnotation()) {
                return JavaTypeCategory.ANNOTATION
            }

            // 3. Enum class (enum class MyEnum)
            if (ktClassOrObject.isEnum()) {
                return JavaTypeCategory.ENUM
            }

            // 4. Interface (interface MyInterface)
            if (ktClassOrObject.isInterface()) {
                return JavaTypeCategory.INTERFACE
            }

            // 5. Abstract / Sealed class (abstract class MyAbstract / sealed class MySealed)
            if (ktClassOrObject.hasModifier(KtTokens.ABSTRACT_KEYWORD) || ktClassOrObject.isSealed()) {
                return JavaTypeCategory.ABSTRACT_CLASS
            }

            // 6. Regular class (class MyClass / data class MyData / open class MyOpen)
            return JavaTypeCategory.CLASS
        }

        return JavaTypeCategory.CLASS
    }

    @JvmStatic
    fun classifyFile(ktFile: KtFile): JavaTypeCategory {
        if (!ktFile.isValid) {
            return JavaTypeCategory.KOTLIN_FILE
        }

        // Fast zero-allocation scan for top-level classes/objects
        var singleClassOrObject: KtClassOrObject? = null
        var classCount = 0

        for (decl in ktFile.declarations) {
            if (decl is KtClassOrObject) {
                classCount++
                if (classCount == 1) {
                    singleClassOrObject = decl
                } else {
                    // More than one class -> early exit
                    break
                }
            }
        }

        if (classCount == 1 && singleClassOrObject != null) {
            return classifyClassOrObject(singleClassOrObject)
        }

        return JavaTypeCategory.KOTLIN_FILE
    }
}
