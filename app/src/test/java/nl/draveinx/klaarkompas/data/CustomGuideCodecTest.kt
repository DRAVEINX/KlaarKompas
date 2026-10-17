package nl.draveinx.klaarkompas.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomGuideCodecTest {
    @Test fun customGuidesRoundTripWithUnicodeDelimitersAndNewlines() {
        val original = Guide(
            id = "custom-test-123",
            title = "Mijn gids | voorjaar, thuis",
            summary = "Eerste regel\nTweede regel: uitleg",
            category = "Thuis",
            minutes = 12,
            tags = listOf("mijn gids"),
            steps = listOf(
                GuideStep("Stap 1: begin", "Doe dit, en daarna dat.\nRustig aan.", "Tip: niets delen | bewaren"),
                GuideStep("Laatste stap", "Controleer het resultaat", null)
            )
        )
        val restored = CustomGuideCodec.decode(CustomGuideCodec.encode(listOf(original))).single()
        assertEquals(original.id, restored.id)
        assertEquals(original.title, restored.title)
        assertEquals(original.summary, restored.summary)
        assertEquals(original.category, restored.category)
        assertEquals(original.minutes, restored.minutes)
        assertEquals(original.steps, restored.steps)
    }

    @Test fun emptyAndCorruptRecordsAreIgnored() {
        assertTrue(CustomGuideCodec.decode(null).isEmpty())
        assertTrue(CustomGuideCodec.decode("").isEmpty())
        assertTrue(CustomGuideCodec.decode("this-is-not-a-valid-record").isEmpty())
    }

    @Test fun duplicateIdsAreCollapsedAndBuiltInGuidesAreNotSerialized() {
        val custom = Guide("custom-one", "Eigen", "", "Thuis", 5, emptyList(), listOf(GuideStep("Start", "Begin hier")))
        val builtIn = custom.copy(id = "wifi-check", title = "Niet overschrijven")
        val encoded = CustomGuideCodec.encode(listOf(custom, custom, builtIn))
        val restored = CustomGuideCodec.decode(encoded)
        assertEquals(1, restored.size)
        assertEquals("Eigen", restored.single().title)
        assertNull(restored.single().safetyNote)
    }
}
