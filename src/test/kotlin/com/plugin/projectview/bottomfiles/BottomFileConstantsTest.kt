package com.plugin.projectview.bottomfiles

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BottomFileConstantsTest {

    @Test
    fun `test target file identification`() {
        assertTrue(BottomFileConstants.isBottomFile("package-info.java"))
        assertFalse(BottomFileConstants.isBottomFile("User.java"))
        assertFalse(BottomFileConstants.isBottomFile("A.java"))
        assertFalse(BottomFileConstants.isBottomFile(null))
        assertFalse(BottomFileConstants.isBottomFile(""))
    }

    @Test
    fun `test sort prefix guarantees lexicographical bottom ordering`() {
        val normalFiles = listOf(
            "A.java",
            "B.java",
            "User.java",
            "Zebra.java",
            "_test.java",
            "~temp.java"
        )

        val bottomSortKey = BottomFileConstants.BOTTOM_SORT_PREFIX + "package-info.java"

        for (fileName in normalFiles) {
            assertTrue(
                bottomSortKey > fileName,
                "Bottom file sort key ($bottomSortKey) should sort after $fileName"
            )
        }
    }

    @Test
    fun `test bottom sort weight exceeds directory and file defaults`() {
        val standardDirectoryWeight = 0
        val standardFileWeight = 0

        assertTrue(
            BottomFileConstants.BOTTOM_SORT_WEIGHT > standardDirectoryWeight,
            "Bottom sort weight must exceed standard directory weight"
        )
        assertTrue(
            BottomFileConstants.BOTTOM_SORT_WEIGHT > standardFileWeight,
            "Bottom sort weight must exceed standard file weight"
        )
    }
}
