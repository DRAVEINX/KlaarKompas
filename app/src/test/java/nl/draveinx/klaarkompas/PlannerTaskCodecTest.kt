package nl.draveinx.klaarkompas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlannerTaskCodecTest {
    @Test fun plannerTasksRoundTripWithDelimitersAndNewlines() {
        val tasks = listOf(
            PlannerTask(
                id = "task-one",
                title = "Afspraak | 10:30, uitleg\nextra regel",
                date = "2026-10-17",
                time = "08:45",
                completed = false,
                reminderEnabled = true
            ),
            PlannerTask(
                id = "task-two",
                title = "Boodschappen doen",
                date = "2026-10-18",
                completed = true
            )
        )
        assertEquals(tasks, PlannerTaskCodec.decode(PlannerTaskCodec.encode(tasks)))
    }

    @Test fun malformedPlannerRecordsAreIgnored() {
        assertTrue(PlannerTaskCodec.decode(null).isEmpty())
        assertTrue(PlannerTaskCodec.decode("not-a-planner-record").isEmpty())
        assertTrue(
            PlannerTaskCodec.decode(
                PlannerTaskCodec.encode(listOf(PlannerTask("bad-date", "Taak", "not-a-date")))
            ).isEmpty()
        )
    }

    @Test fun duplicateTaskIdsAreCollapsed() {
        val first = PlannerTask("same", "Eerste", "2026-10-17")
        val second = PlannerTask("same", "Tweede", "2026-10-18")
        assertEquals(listOf(first), PlannerTaskCodec.decode(PlannerTaskCodec.encode(listOf(first, second))))
    }
}
