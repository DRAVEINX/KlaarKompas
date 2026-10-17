package nl.draveinx.klaarkompas.support

/**
 * Offline-first domain foundation for "Samen-doen".
 *
 * A session can be shared with a helper by reading the current step together.
 * This model deliberately does not claim to synchronise between devices; that
 * requires authenticated, encrypted backend storage and explicit consent.
 */
enum class HelpStyle { SIMPLE, STANDARD, DETAILED }

data class TaskStep(
    val title: String,
    val instruction: String,
    val donePrompt: String = "Is dit gelukt?"
)

data class TogetherSession(
    val taskTitle: String,
    val steps: List<TaskStep>,
    val currentStep: Int = 0,
    val completedSteps: Set<Int> = emptySet(),
    val helperNote: String = ""
) {
    val isComplete: Boolean
        get() = steps.isNotEmpty() && completedSteps.size >= steps.size

    fun markCurrentStepDone(): TogetherSession {
        if (steps.isEmpty() || currentStep !in steps.indices) return this
        val updated = completedSteps + currentStep
        return copy(
            completedSteps = updated,
            currentStep = (steps.indices.firstOrNull { it !in updated } ?: steps.lastIndex)
        )
    }

    fun goToStep(index: Int): TogetherSession =
        if (index in steps.indices) copy(currentStep = index) else this

    fun withHelperNote(note: String): TogetherSession =
        copy(helperNote = note.take(MAX_NOTE_LENGTH))

    companion object {
        const val MAX_NOTE_LENGTH = 500
    }
}

/** Predictable, offline wording adaptation; no personal data leaves the device. */
object AdaptiveHelp {
    fun explain(title: String, instruction: String, style: HelpStyle): String {
        val cleanTitle = title.trim().ifEmpty { "Deze stap" }
        val cleanInstruction = instruction.trim()
        return when (style) {
            HelpStyle.SIMPLE -> "$cleanTitle. Doe één ding tegelijk. $cleanInstruction Neem rustig de tijd. Vraag iemand om hulp als je vastloopt."
            HelpStyle.STANDARD -> "$cleanTitle\n\n$cleanInstruction"
            HelpStyle.DETAILED -> "$cleanTitle\n\nWat je gaat doen:\n$cleanInstruction\n\nControleer daarna of het gelukt is. Zo niet, probeer het nog eens of vraag iemand om mee te kijken."
        }.trim()
    }
}

/**
 * Controlled guide publishing rules. User-created guides remain separate from
 * bundled guides; publishing/importing a guide must pass these checks first.
 */
object GuidePublicationRules {
    const val MAX_STEPS = 20
    const val MAX_TITLE_LENGTH = 100
    const val MAX_INSTRUCTION_LENGTH = 1200

    data class Validation(val accepted: Boolean, val issues: List<String>)

    fun validate(title: String, steps: List<TaskStep>): Validation {
        val issues = mutableListOf<String>()
        if (title.isBlank()) issues += "Vul een titel in."
        if (title.length > MAX_TITLE_LENGTH) issues += "De titel is te lang."
        if (steps.isEmpty()) issues += "Voeg minimaal één stap toe."
        if (steps.size > MAX_STEPS) issues += "Een gids mag maximaal $MAX_STEPS stappen bevatten."
        if (steps.any { it.title.isBlank() || it.instruction.isBlank() }) {
            issues += "Elke stap heeft een titel en uitleg nodig."
        }
        if (steps.any { it.instruction.length > MAX_INSTRUCTION_LENGTH }) {
            issues += "Een stap bevat te veel tekst."
        }
        return Validation(issues.isEmpty(), issues)
    }
}
