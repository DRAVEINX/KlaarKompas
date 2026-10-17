package nl.draveinx.klaarkompas.support

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskCompanionTest {
    @Test fun sessionTracksCompletedStepsAndMovesForward() {
        val session = TogetherSession("Thee zetten", listOf(
            TaskStep("Water", "Vul de waterkoker."),
            TaskStep("Aan", "Zet de waterkoker aan.")
        ))
        val next = session.markCurrentStepDone()
        assertEquals(setOf(0), next.completedSteps)
        assertEquals(1, next.currentStep)
        assertFalse(next.isComplete)
        assertTrue(next.markCurrentStepDone().isComplete)
    }

    @Test fun invalidStepDoesNotChangeSession() {
        val session = TogetherSession("Taak", listOf(TaskStep("Start", "Begin")))
        assertEquals(session, session.goToStep(99))
    }

    @Test fun helperNoteIsBounded() {
        val session = TogetherSession("Taak", emptyList()).withHelperNote("x".repeat(900))
        assertEquals(TogetherSession.MAX_NOTE_LENGTH, session.helperNote.length)
    }

    @Test fun simpleAndDetailedHelpAdaptTheExplanation() {
        val simple = AdaptiveHelp.explain("Kraan", "Draai de kraan open.", HelpStyle.SIMPLE)
        val detailed = AdaptiveHelp.explain("Kraan", "Draai de kraan open.", HelpStyle.DETAILED)
        assertTrue(simple.contains("Doe één ding tegelijk"))
        assertTrue(detailed.contains("Wat je gaat doen"))
        assertFalse(simple == detailed)
    }

    @Test fun guidePublicationRejectsEmptyAndOversizedGuides() {
        assertFalse(GuidePublicationRules.validate("", emptyList()).accepted)
        val oversized = (1..21).map { TaskStep("Stap $it", "Uitleg") }
        assertFalse(GuidePublicationRules.validate("Gids", oversized).accepted)
        assertTrue(GuidePublicationRules.validate("Gids", listOf(TaskStep("Begin", "Doe dit"))).accepted)
    }
}
