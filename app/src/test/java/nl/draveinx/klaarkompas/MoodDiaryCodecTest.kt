package nl.draveinx.klaarkompas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoodDiaryCodecTest {
    @Test
    fun entriesRoundTripWithUnicodeSeparatorsAndNewlines() {
        val original = listOf(
            MoodDiaryEntry(
                id = "entry-1",
                date = "2026-10-17",
                mood = "Rustig",
                note = "Vandaag ging het goed | echt goed\nEn ik ben blij 🙂",
                drawing = "0.1,0.2;0.3,0.4|0.8,0.7"
            )
        )

        assertEquals(original, MoodDiaryCodec.decode(MoodDiaryCodec.encode(original)))
    }

    @Test
    fun invalidRecordsAreIgnoredAndDuplicateIdsAreDeduplicated() {
        val first = MoodDiaryEntry("same", "2026-10-17", "Blij", "eerste")
        val duplicate = first.copy(note = "tweede")
        val encoded = MoodDiaryCodec.encode(listOf(first, duplicate)) + "\nnot-a-record"

        assertEquals(listOf(first), MoodDiaryCodec.decode(encoded))
    }

    @Test
    fun unsupportedMoodAndInvalidDateAreNotSaved() {
        val invalidMood = MoodDiaryEntry("bad-mood", "2026-10-17", "Onbekend", "tekst")
        val invalidDate = MoodDiaryEntry("bad-date", "gisteren", "Blij", "tekst")

        assertTrue(MoodDiaryCodec.encode(listOf(invalidMood, invalidDate)).isEmpty())
        assertTrue(MoodDiaryCodec.decode("not-valid").isEmpty())
    }

    @Test
    fun historyIsBoundedToLatestOneHundredEntries() {
        val entries = (1..105).map {
            MoodDiaryEntry("entry-$it", "2026-10-17", "Neutraal", "notitie $it")
        }

        val decoded = MoodDiaryCodec.decode(MoodDiaryCodec.encode(entries))

        assertEquals(100, decoded.size)
        assertEquals("entry-6", decoded.first().id)
        assertEquals("entry-105", decoded.last().id)
    }
}
