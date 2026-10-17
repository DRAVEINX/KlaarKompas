package nl.draveinx.klaarkompas.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GuideRepositoryTest {
    @Test fun starterLibraryHasAtLeastThirtyGuides() {
        assertTrue("De startbibliotheek moet substantieel zijn", GuideRepository.guides.size >= 30)
    }

    @Test fun everyGuideHasUniqueIdAndUsableSteps() {
        val guides = GuideRepository.guides
        assertEquals(guides.size, guides.map { it.id }.distinct().size)
        guides.forEach { guide ->
            assertTrue(guide.id.isNotBlank())
            assertTrue(guide.title.isNotBlank())
            assertTrue(guide.steps.isNotEmpty())
            guide.steps.forEach { step ->
                assertFalse(step.title.isBlank())
                assertFalse(step.instruction.isBlank())
            }
        }
    }

    @Test fun searchIsCaseInsensitiveAndCategoryAware() {
        assertTrue(GuideRepository.search("WIFI").any { it.id == "wifi-check" })
        assertTrue(GuideRepository.search("cv", "Werk & studie").any { it.id == "cv-start" })
        assertTrue(GuideRepository.search("cv", "Thuis").none { it.id == "cv-start" })
    }

    @Test fun everyGuideUsesADeclaredCategory() {
        val validCategories = GuideRepository.categories - "Alles"
        assertTrue(GuideRepository.guides.all { it.category in validCategories })
    }
}
