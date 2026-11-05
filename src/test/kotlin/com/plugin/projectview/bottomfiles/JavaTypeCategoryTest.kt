package com.plugin.projectview.bottomfiles

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JavaTypeCategoryTest {

    @Test
    fun `test weight ordering strictly follows desired category hierarchy`() {
        val categories = listOf(
            JavaTypeCategory.INTERFACE,
            JavaTypeCategory.ABSTRACT_CLASS,
            JavaTypeCategory.CLASS,
            JavaTypeCategory.FINAL_CLASS,
            JavaTypeCategory.RECORD,
            JavaTypeCategory.ENUM,
            JavaTypeCategory.ANNOTATION,
            JavaTypeCategory.OTHER_FILE,
            JavaTypeCategory.BOTTOM_FILE
        )

        for (i in 0 until categories.size - 1) {
            val current = categories[i]
            val next = categories[i + 1]
            assertTrue(
                current.weight < next.weight,
                "Expected ${current.name} (weight ${current.weight}) to be < ${next.name} (weight ${next.weight})"
            )
        }
    }

    @Test
    fun `test sort prefix ordering strictly follows desired category hierarchy`() {
        val categories = listOf(
            JavaTypeCategory.INTERFACE,
            JavaTypeCategory.ABSTRACT_CLASS,
            JavaTypeCategory.CLASS,
            JavaTypeCategory.FINAL_CLASS,
            JavaTypeCategory.RECORD,
            JavaTypeCategory.ENUM,
            JavaTypeCategory.ANNOTATION,
            JavaTypeCategory.OTHER_FILE,
            JavaTypeCategory.BOTTOM_FILE
        )

        for (i in 0 until categories.size - 1) {
            val current = categories[i]
            val next = categories[i + 1]
            assertTrue(
                current.sortPrefix < next.sortPrefix,
                "Expected prefix '${current.sortPrefix}' to sort before '${next.sortPrefix}'"
            )
        }
    }

    @Test
    fun `test composite sort key groups types together and preserves alphabetical ordering within groups`() {
        val items = listOf(
            "${JavaTypeCategory.INTERFACE.sortPrefix}UserRepository",
            "${JavaTypeCategory.INTERFACE.sortPrefix}AuthService",
            "${JavaTypeCategory.ABSTRACT_CLASS.sortPrefix}BaseEntity",
            "${JavaTypeCategory.ABSTRACT_CLASS.sortPrefix}AbstractHandler",
            "${JavaTypeCategory.CLASS.sortPrefix}UserServiceImpl",
            "${JavaTypeCategory.CLASS.sortPrefix}ApplicationStartupListener",
            "${JavaTypeCategory.FINAL_CLASS.sortPrefix}SystemConstants",
            "${JavaTypeCategory.FINAL_CLASS.sortPrefix}AppConfig",
            "${JavaTypeCategory.RECORD.sortPrefix}UserResponse",
            "${JavaTypeCategory.RECORD.sortPrefix}CreateUserRequest",
            "${JavaTypeCategory.ENUM.sortPrefix}UserRole",
            "${JavaTypeCategory.ENUM.sortPrefix}AccountStatus",
            "${JavaTypeCategory.ANNOTATION.sortPrefix}Transactional",
            "${JavaTypeCategory.ANNOTATION.sortPrefix}Audited",
            "${JavaTypeCategory.OTHER_FILE.sortPrefix}schema.sql",
            "${JavaTypeCategory.OTHER_FILE.sortPrefix}application.yml",
            "${JavaTypeCategory.BOTTOM_FILE.sortPrefix}package-info.java"
        )

        val sorted = items.sorted()

        val expectedOrder = listOf(
            // 1. Interfaces (sorted A-Z)
            "${JavaTypeCategory.INTERFACE.sortPrefix}AuthService",
            "${JavaTypeCategory.INTERFACE.sortPrefix}UserRepository",

            // 2. Abstract Classes (sorted A-Z)
            "${JavaTypeCategory.ABSTRACT_CLASS.sortPrefix}AbstractHandler",
            "${JavaTypeCategory.ABSTRACT_CLASS.sortPrefix}BaseEntity",

            // 3. Classes (sorted A-Z)
            "${JavaTypeCategory.CLASS.sortPrefix}ApplicationStartupListener",
            "${JavaTypeCategory.CLASS.sortPrefix}UserServiceImpl",

            // 4. Final Classes (sorted A-Z)
            "${JavaTypeCategory.FINAL_CLASS.sortPrefix}AppConfig",
            "${JavaTypeCategory.FINAL_CLASS.sortPrefix}SystemConstants",

            // 5. Records (sorted A-Z)
            "${JavaTypeCategory.RECORD.sortPrefix}CreateUserRequest",
            "${JavaTypeCategory.RECORD.sortPrefix}UserResponse",

            // 6. Enums (sorted A-Z)
            "${JavaTypeCategory.ENUM.sortPrefix}AccountStatus",
            "${JavaTypeCategory.ENUM.sortPrefix}UserRole",

            // 7. Annotations (sorted A-Z)
            "${JavaTypeCategory.ANNOTATION.sortPrefix}Audited",
            "${JavaTypeCategory.ANNOTATION.sortPrefix}Transactional",

            // 8. Other Files (sorted A-Z)
            "${JavaTypeCategory.OTHER_FILE.sortPrefix}application.yml",
            "${JavaTypeCategory.OTHER_FILE.sortPrefix}schema.sql",

            // 9. Bottom Files (strictly at the bottom)
            "${JavaTypeCategory.BOTTOM_FILE.sortPrefix}package-info.java"
        )

        assertEquals(expectedOrder, sorted)
    }

    @Test
    fun `test package-info is strictly sorted at the very bottom after all types and names`() {
        val packageInfoKey = "${JavaTypeCategory.BOTTOM_FILE.sortPrefix}package-info.java"

        val sampleKeys = listOf(
            "${JavaTypeCategory.INTERFACE.sortPrefix}ZZZ_Interface",
            "${JavaTypeCategory.CLASS.sortPrefix}ZZZ_Class",
            "${JavaTypeCategory.RECORD.sortPrefix}ZZZ_Record",
            "${JavaTypeCategory.ENUM.sortPrefix}ZZZ_Enum",
            "${JavaTypeCategory.ANNOTATION.sortPrefix}ZZZ_Annotation",
            "${JavaTypeCategory.OTHER_FILE.sortPrefix}zzz_other.txt"
        )

        for (key in sampleKeys) {
            assertTrue(
                packageInfoKey > key,
                "Bottom file '$packageInfoKey' must sort after '$key'"
            )
        }
    }
}
