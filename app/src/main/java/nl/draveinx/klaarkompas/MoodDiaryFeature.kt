package nl.draveinx.klaarkompas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.util.Base64
import java.util.UUID

/** A private, device-local reflection. Drawing coordinates are normalized to 0..1. */
data class MoodDiaryEntry(
    val id: String,
    val date: String,
    val mood: String,
    val note: String,
    val drawing: String = ""
)

object MoodDiaryCodec {
    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()

    fun encode(entries: List<MoodDiaryEntry>): String = entries
        .filter { it.id.isNotBlank() && it.mood in moods && runCatching { LocalDate.parse(it.date) }.isSuccess }
        .distinctBy { it.id }
        .takeLast(MAX_ENTRIES)
        .joinToString("\n") { entry ->
            listOf(entry.id, entry.date, entry.mood, entry.note.take(MAX_NOTE_LENGTH), entry.drawing.take(MAX_DRAWING_LENGTH))
                .joinToString("|") { part -> encoder.encodeToString(part.toByteArray(Charsets.UTF_8)) }
        }

    fun decode(raw: String?): List<MoodDiaryEntry> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.lineSequence().mapNotNull { line ->
            runCatching {
                val fields = line.split("|")
                require(fields.size == 5)
                val values = fields.map { String(decoder.decode(it), Charsets.UTF_8) }
                require(values[0].isNotBlank() && values[2] in moods)
                LocalDate.parse(values[1])
                require(values[3].length <= MAX_NOTE_LENGTH && values[4].length <= MAX_DRAWING_LENGTH)
                MoodDiaryEntry(values[0], values[1], values[2], values[3], values[4])
            }.getOrNull()
        }.distinctBy { it.id }.takeLast(MAX_ENTRIES)
    }

    const val MAX_ENTRIES = 100
    const val MAX_NOTE_LENGTH = 3000
    const val MAX_DRAWING_LENGTH = 200_000
    val moods = listOf("Blij", "Rustig", "Neutraal", "Verdrietig", "Boos", "Onrustig")
}

@Composable
fun MoodDiaryScreen(
    entries: List<MoodDiaryEntry>,
    largeText: Boolean,
    onSave: (MoodDiaryEntry) -> Unit,
    onReadAloud: (String) -> Unit
) {
    var selectedMood by rememberSaveable { mutableStateOf(MoodDiaryCodec.moods.first()) }
    var note by rememberSaveable { mutableStateOf("") }
    var strokes by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
    val textSize = if (largeText) 19.sp else 15.sp
    val inkColor = MaterialTheme.colorScheme.onSurface

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Mijn gevoel", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Kies hoe je je voelt. Schrijf iets op of maak een tekening. Je gegevens blijven op dit toestel.",
                fontSize = textSize
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MoodDiaryCodec.moods.take(3).forEach { mood ->
                    FilterChip(selected = selectedMood == mood, onClick = { selectedMood = mood }, label = { Text(mood) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MoodDiaryCodec.moods.drop(3).forEach { mood ->
                    FilterChip(selected = selectedMood == mood, onClick = { selectedMood = mood }, label = { Text(mood) })
                }
            }
        }
        item {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it.take(MoodDiaryCodec.MAX_NOTE_LENGTH) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Wat wil je onthouden?") },
                placeholder = { Text("Je mag ook maar een paar woorden schrijven.") },
                minLines = 3,
                maxLines = 7
            )
            OutlinedButton(
                onClick = { if (note.isNotBlank()) onReadAloud(note) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Lees mijn tekst voor") }
        }
        item {
            Text("Tekenen", style = MaterialTheme.typography.titleLarge)
            Text("Teken met je vinger. Je kunt de tekening samen met je dagboek bewaren.", fontSize = textSize)
            Card(
                modifier = Modifier.fillMaxWidth().height(210.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Canvas(
                    Modifier.fillMaxSize().padding(8.dp).pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { point ->
                                currentStroke = listOf(Offset(point.x / size.width.coerceAtLeast(1), point.y / size.height.coerceAtLeast(1)))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val next = Offset(
                                    (change.position.x / size.width.coerceAtLeast(1)).coerceIn(0f, 1f),
                                    (change.position.y / size.height.coerceAtLeast(1)).coerceIn(0f, 1f)
                                )
                                currentStroke = currentStroke + next
                            },
                            onDragEnd = {
                                if (currentStroke.isNotEmpty()) strokes = (strokes + listOf(currentStroke)).takeLast(80)
                                currentStroke = emptyList()
                            },
                            onDragCancel = { currentStroke = emptyList() }
                        )
                    }
                ) {
                    (strokes + listOf(currentStroke).filter { it.isNotEmpty() }).forEach { points ->
                        if (points.isNotEmpty()) {
                            val path = Path().apply {
                                moveTo(points.first().x * size.width, points.first().y * size.height)
                                points.drop(1).forEach { lineTo(it.x * size.width, it.y * size.height) }
                            }
                            drawPath(path, inkColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
                            if (points.size == 1) drawCircle(inkColor, 2f, Offset(points[0].x * size.width, points[0].y * size.height))
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { strokes = strokes.dropLast(1); currentStroke = emptyList() }, modifier = Modifier.weight(1f)) { Text("Laatste lijn weg") }
                OutlinedButton(onClick = { strokes = emptyList(); currentStroke = emptyList() }, modifier = Modifier.weight(1f)) { Text("Wis tekening") }
            }
            Button(
                onClick = {
                    val drawing = strokes.joinToString("|") { stroke ->
                        stroke.joinToString(";") { point -> "${point.x},${point.y}" }
                    }.take(MoodDiaryCodec.MAX_DRAWING_LENGTH)
                    onSave(MoodDiaryEntry(UUID.randomUUID().toString(), LocalDate.now().toString(), selectedMood, note.trim(), drawing))
                    note = ""
                    strokes = emptyList()
                    currentStroke = emptyList()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Bewaar dagboekmoment") }
        }
        item { Text("Eerdere momenten", style = MaterialTheme.typography.titleLarge) }
        if (entries.isEmpty()) {
            item { Text("Je hebt nog niets bewaard. Je kunt op elk moment beginnen.", fontSize = textSize) }
        } else {
            items(entries.sortedByDescending { it.date }, key = { it.id }) { entry ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("${entry.date} • ${entry.mood}", style = MaterialTheme.typography.titleMedium)
                        if (entry.note.isNotBlank()) Text(entry.note, fontSize = textSize)
                        if (entry.drawing.isNotBlank()) Text("Bevat een tekening")
                        OutlinedButton(onClick = {
                            onReadAloud(buildString {
                                append("${entry.date}. Je voelde je ${entry.mood}. ")
                                if (entry.note.isNotBlank()) append(entry.note)
                            })
                        }) { Text("Lees voor") }
                    }
                }
            }
        }
        item { Spacer(Modifier.size(8.dp)) }
    }
}
