package nl.draveinx.klaarkompas.data

import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Small, version-independent serializer for user-created guides stored locally in DataStore.
 * Fields are URL-safe Base64 encoded so separators and newlines in user input cannot corrupt data.
 */
object CustomGuideCodec {
    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()
    private const val NULL_SENTINEL = "~null~"
    private const val CUSTOM_PREFIX = "custom-"

    fun encode(guides: List<Guide>): String = guides
        .filter { it.id.startsWith(CUSTOM_PREFIX) && it.steps.isNotEmpty() }
        .distinctBy { it.id }
        .joinToString("\n") { guide ->
            val fields = listOf(
                guide.id,
                encodeText(guide.title),
                encodeText(guide.summary),
                encodeText(guide.category),
                guide.minutes.coerceIn(1, 600).toString(),
                guide.steps.joinToString(",") { step ->
                    listOf(
                        encodeText(step.title),
                        encodeText(step.instruction),
                        encodeText(step.tip ?: NULL_SENTINEL),
                        encodeText(step.imageUri ?: NULL_SENTINEL)
                    ).joinToString(".")
                },
                encodeText(guide.coverImageUri ?: NULL_SENTINEL),
                encodeText(guide.safetyNote ?: NULL_SENTINEL)
            )
            fields.joinToString("|")
        }

    fun decode(serialized: String?): List<Guide> {
        if (serialized.isNullOrBlank()) return emptyList()
        return serialized.lineSequence().mapNotNull guideLine@ { line ->
            if (line.isBlank()) return@guideLine null
            runCatching {
                val fields = line.split('|')
                require(fields.size in 6..8)
                val id = fields[0]
                require(id.startsWith(CUSTOM_PREFIX))
                val title = decodeText(fields[1]).trim()
                val summary = decodeText(fields[2]).trim()
                val decodedCategory = decodeText(fields[3]).trim()
                val category = decodedCategory.takeIf { it in GuideRepository.categories.drop(1) } ?: "Leren & maken"
                val minutes = fields[4].toInt().coerceIn(1, 600)
                val steps = fields[5].split(',').mapNotNull stepPart@ { encodedStep ->
                    val parts = encodedStep.split('.')
                    if (parts.size !in 3..4) return@stepPart null
                    val stepTitle = decodeText(parts[0]).trim()
                    val instruction = decodeText(parts[1]).trim()
                    val decodedTip = decodeText(parts[2])
                    val decodedImage = parts.getOrNull(3)?.let(::decodeText)
                    if (stepTitle.isBlank() || instruction.isBlank()) null
                    else GuideStep(
                        stepTitle,
                        instruction,
                        decodedTip.takeUnless { it == NULL_SENTINEL || it.isBlank() },
                        decodedImage?.takeUnless { it == NULL_SENTINEL || it.isBlank() }
                    )
                }
                require(title.isNotBlank() && steps.isNotEmpty())
                Guide(
                    id = id,
                    title = title,
                    summary = summary.ifBlank { "Een persoonlijke handleiding." },
                    category = category,
                    minutes = minutes,
                    tags = listOf("mijn gids", category.lowercase()),
                    steps = steps,
                    coverImageUri = fields.getOrNull(6)?.let(::decodeText)
                        ?.takeUnless { it == NULL_SENTINEL || it.isBlank() },
                    safetyNote = fields.getOrNull(7)?.let(::decodeText)
                        ?.takeUnless { it == NULL_SENTINEL || it.isBlank() }
                )
            }.getOrNull()
        }.distinctBy { it.id }.toList()
    }

    private fun encodeText(value: String): String = encoder.encodeToString(value.toByteArray(StandardCharsets.UTF_8))

    private fun decodeText(value: String): String = String(decoder.decode(value), StandardCharsets.UTF_8)
}
