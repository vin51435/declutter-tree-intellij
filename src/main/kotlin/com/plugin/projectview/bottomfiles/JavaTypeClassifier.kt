package com.plugin.projectview.bottomfiles

import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiModifier

/**
 * Fast, stub-backed classifier that categorizes Java [PsiClass] elements
 * according to their structural kind and modifiers.
 */
object JavaTypeClassifier {

    @JvmStatic
    fun classify(psiClass: PsiClass?): JavaTypeCategory {
        if (psiClass == null || !psiClass.isValid) {
            return JavaTypeCategory.CLASS
        }

        return try {
            classifyInternal(psiClass)
        } catch (_: IndexNotReadyException) {
            // During indexing / dumb mode, fall back safely to standard class
            JavaTypeCategory.CLASS
        } catch (_: Throwable) {
            JavaTypeCategory.CLASS
        }
    }

    private fun classifyInternal(psiClass: PsiClass): JavaTypeCategory {
        // 1. Annotation (@interface)
        // Must be checked before isInterface because annotations are interfaces in the JVM
        if (psiClass.isAnnotationType) {
            return JavaTypeCategory.ANNOTATION
        }

        // 2. Enum
        // Must be checked before final/abstract modifier checks because enums are implicitly final/abstract
        if (psiClass.isEnum) {
            return JavaTypeCategory.ENUM
        }

        // 3. Record
        // Must be checked before final modifier checks because records are implicitly final
        if (psiClass.isRecord) {
            return JavaTypeCategory.RECORD
        }

        // 4. Interface
        if (psiClass.isInterface) {
            return JavaTypeCategory.INTERFACE
        }

        // 5. Abstract Class
        if (psiClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
            return JavaTypeCategory.ABSTRACT_CLASS
        }

        // 6. Final Class
        if (psiClass.hasModifierProperty(PsiModifier.FINAL)) {
            return JavaTypeCategory.FINAL_CLASS
        }

        // 7. Regular concrete Class
        return JavaTypeCategory.CLASS
    }
}
