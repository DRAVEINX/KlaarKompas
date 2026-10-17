package nl.draveinx.klaarkompas

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Base64
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal val FavoritesKey = stringSetPreferencesKey("favorite_guides")
internal val CompletedKey = stringSetPreferencesKey("completed_guides")
internal val ProgressKey = stringPreferencesKey("guide_progress")
internal val LargeTextKey = booleanPreferencesKey("large_text")
internal val CustomGuidesKey = stringPreferencesKey("custom_guides_v1")
internal val PlannerKey = stringPreferencesKey("daily_planner_v1")
internal val RecentGuidesKey = stringPreferencesKey("recent_guides_v1")
internal val HighContrastKey = booleanPreferencesKey("high_contrast")
internal val SimpleLanguageKey = booleanPreferencesKey("simple_language")
internal val AutoReadKey = booleanPreferencesKey("auto_read_steps")
internal val MoodDiaryKey = stringPreferencesKey("mood_diary_v1")

/** A locally stored daily planner item. Date uses ISO-8601; reminder time is HH:mm. */
data class PlannerTask(
    val id: String,
    val title: String,
    val date: String,
    val time: String = "",
    val completed: Boolean = false,
    val reminderEnabled: Boolean = false
)

object PlannerTaskCodec {
    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()

    fun encode(tasks: List<PlannerTask>): String = tasks
        .distinctBy { it.id }
        .joinToString("\n") { task ->
            listOf(
                task.id, task.title, task.date, task.time,
                task.completed.toString(), task.reminderEnabled.toString()
            ).joinToString("|") { encodePart(it) }
        }

    fun decode(raw: String?): List<PlannerTask> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.lineSequence().mapNotNull { line ->
            runCatching {
                val fields = line.split("|")
                require(fields.size == 6)
                val values = fields.map(::decodePart)
                require(values[0].isNotBlank() && values[1].isNotBlank())
                LocalDate.parse(values[2])
                if (values[3].isNotBlank()) LocalTime.parse(values[3])
                PlannerTask(
                    id = values[0],
                    title = values[1].take(160),
                    date = values[2],
                    time = values[3],
                    completed = values[4].toBooleanStrict(),
                    reminderEnabled = values[5].toBooleanStrict()
                )
            }.getOrNull()
        }.distinctBy { it.id }.toList()
    }

    private fun encodePart(value: String): String =
        encoder.encodeToString(value.toByteArray(Charsets.UTF_8))

    private fun decodePart(value: String): String =
        String(decoder.decode(value), Charsets.UTF_8)
}

/** Copies picked images into app-owned storage so permissions and backups survive. */
internal fun copyGuideImage(context: Context, source: Uri): String? = runCatching {
    val mimeType = context.contentResolver.getType(source).orEmpty()
    val extension = when (mimeType.lowercase()) {
        "image/png" -> "png"
        "image/webp" -> "webp"
        "image/gif" -> "gif"
        else -> "jpg"
    }
    val directory = File(context.filesDir, "guide-images").apply { mkdirs() }
    val target = File(directory, "${UUID.randomUUID()}.$extension")
    val input = context.contentResolver.openInputStream(source) ?: error("De afbeelding kon niet worden geopend.")
    input.use { sourceStream ->
        target.outputStream().buffered().use { output ->
            val buffer = ByteArray(8192)
            var total = 0L
            while (true) {
                val count = sourceStream.read(buffer)
                if (count < 0) break
                total += count
                require(total <= MAX_SINGLE_IMAGE_BYTES) { "Kies een afbeelding kleiner dan 10 MB." }
                output.write(buffer, 0, count)
            }
        }
    }
    "app-image:${target.name}"
}.getOrNull()

internal fun guideImageFile(context: Context, reference: String?): File? {
    if (reference.isNullOrBlank() || !reference.startsWith("app-image:")) return null
    val name = reference.removePrefix("app-image:")
    if (!IMAGE_NAME_REGEX.matches(name)) return null
    val base = File(context.filesDir, "guide-images").canonicalFile
    val file = File(base, name).canonicalFile
    if (file.parentFile != base || !file.isFile) return null
    return file
}

private const val MAX_SINGLE_IMAGE_BYTES = 10L * 1024L * 1024L
private const val MAX_BACKUP_TOTAL_BYTES = 80L * 1024L * 1024L
private val IMAGE_NAME_REGEX = Regex("[A-Za-z0-9_-]+\\.(jpg|jpeg|png|webp|gif)", RegexOption.IGNORE_CASE)
private const val REMINDER_CHANNEL_ID = "klaarkompas_daily_reminders"

object PlannerReminderScheduler {
    fun schedule(context: Context, task: PlannerTask) {
        if (!task.reminderEnabled || task.time.isBlank()) {
            cancel(context, task.id)
            return
        }
        val triggerAt = runCatching {
            LocalDate.parse(task.date).atTime(LocalTime.parse(task.time))
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrNull() ?: return
        if (triggerAt <= System.currentTimeMillis()) {
            cancel(context, task.id)
            return
        }
        val intent = Intent(context, PlannerReminderReceiver::class.java).apply {
            action = "nl.draveinx.klaarkompas.PLANNER_REMINDER"
            data = Uri.parse("klaarkompas://reminder/${Uri.encode(task.id)}")
            putExtra("task_id", task.id)
            putExtra("task_title", task.title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    fun cancel(context: Context, taskId: String) {
        val intent = Intent(context, PlannerReminderReceiver::class.java).apply {
            action = "nl.draveinx.klaarkompas.PLANNER_REMINDER"
            data = Uri.parse("klaarkompas://reminder/${Uri.encode(taskId)}")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent)
        pendingIntent.cancel()
    }
}

class PlannerReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "KlaarKompas-herinneringen",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Herinneringen voor persoonlijke taken" }
            )
        }
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val title = intent.getStringExtra("task_title").orEmpty().ifBlank { "Je geplande taak" }
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("KlaarKompas: tijd voor je taak")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Het is tijd voor: $title"))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(
                intent.getStringExtra("task_id").orEmpty().hashCode(),
                notification
            )
        }
    }
}

class PlannerBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val preferences = context.dataStore.data
                    .catch { emit(emptyPreferences()) }
                    .first()
                PlannerTaskCodec.decode(preferences[PlannerKey])
                    .filter { it.reminderEnabled }
                    .forEach { PlannerReminderScheduler.schedule(context, it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

/**
 * Portable ZIP backup for app preferences plus app-owned guide images.
 * Import validates the format first and restores only known, correctly typed keys.
 */
object KlaarKompasBackup {
    private const val MANIFEST_NAME = "backup.json"
    private val knownKeys = setOf(
        FavoritesKey.name, CompletedKey.name, ProgressKey.name, LargeTextKey.name,
        CustomGuidesKey.name, PlannerKey.name, RecentGuidesKey.name, HighContrastKey.name,
        SimpleLanguageKey.name, AutoReadKey.name, MoodDiaryKey.name
    )

    fun write(context: Context, preferences: Preferences, destination: Uri) {
        val output = context.contentResolver.openOutputStream(destination)
            ?: error("Het back-upbestand kon niet worden aangemaakt.")
        ZipOutputStream(BufferedOutputStream(output)).use { zip ->
            val preferenceObject = JSONObject()
            preferences.asMap().forEach { (key, value) ->
                val item = JSONObject()
                when (value) {
                    is Boolean -> item.put("type", "boolean").put("value", value)
                    is String -> item.put("type", "string").put("value", value)
                    is Set<*> -> item.put("type", "stringSet").put(
                        "value", JSONArray(value.map { it.toString() }.sorted())
                    )
                    else -> error("Onverwacht instellingstype in de back-up.")
                }
                preferenceObject.put(key.name, item)
            }
            val manifest = JSONObject()
                .put("format", "klaarkompas-backup")
                .put("schemaVersion", 1)
                .put("createdAt", System.currentTimeMillis())
                .put("preferences", preferenceObject)
            zip.putNextEntry(ZipEntry(MANIFEST_NAME))
            zip.write(manifest.toString(2).toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            val imageDirectory = File(context.filesDir, "guide-images")
            imageDirectory.listFiles()?.filter { it.isFile && IMAGE_NAME_REGEX.matches(it.name) }?.forEach { file ->
                zip.putNextEntry(ZipEntry("guide-images/${file.name}"))
                file.inputStream().buffered().use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }
    }

    fun restore(context: Context, source: Uri) {
        val input = context.contentResolver.openInputStream(source)
            ?: error("Het back-upbestand kon niet worden geopend.")
        var totalBytes = 0L
        var manifest: JSONObject? = null
        val stagedImages = linkedMapOf<String, ByteArray>()
        ZipInputStream(BufferedInputStream(input)).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                require(!entry.isDirectory) { "De back-up bevat een ongeldig bestand." }
                val bytes = zip.readLimitedBytes(MAX_BACKUP_TOTAL_BYTES - totalBytes)
                totalBytes += bytes.size
                require(totalBytes <= MAX_BACKUP_TOTAL_BYTES) { "De back-up is groter dan 80 MB." }
                when {
                    entry.name == MANIFEST_NAME -> {
                        require(manifest == null) { "Dubbel back-upmanifest gevonden." }
                        manifest = JSONObject(String(bytes, Charsets.UTF_8))
                    }
                    entry.name.startsWith("guide-images/") -> {
                        val name = entry.name.removePrefix("guide-images/")
                        require('/' !in name && IMAGE_NAME_REGEX.matches(name)) { "Ongeldige afbeeldingsnaam in de back-up." }
                        stagedImages[name] = bytes
                    }
                    else -> error("De back-up bevat een onverwacht bestand.")
                }
                zip.closeEntry()
            }
        }

        val root = manifest ?: error("Dit is geen geldige KlaarKompas-back-up.")
        require(root.optString("format") == "klaarkompas-backup") { "Dit back-upbestand hoort niet bij KlaarKompas." }
        require(root.optInt("schemaVersion", -1) == 1) { "Deze back-upversie wordt niet ondersteund." }
        val serializedPreferences = root.optJSONObject("preferences")
            ?: error("Instellingen ontbreken in de back-up.")
        validatePreferenceTypes(serializedPreferences)

        val imageDirectory = File(context.filesDir, "guide-images").apply { mkdirs() }
        stagedImages.forEach { (name, bytes) ->
            val target = File(imageDirectory, name).canonicalFile
            require(target.parentFile == imageDirectory.canonicalFile) { "Ongeldige afbeeldingslocatie." }
            target.outputStream().buffered().use { it.write(bytes) }
        }

        kotlinx.coroutines.runBlocking {
            context.dataStore.edit { values ->
                values.remove(FavoritesKey)
                values.remove(CompletedKey)
                values.remove(ProgressKey)
                values.remove(LargeTextKey)
                values.remove(CustomGuidesKey)
                values.remove(PlannerKey)
                values.remove(RecentGuidesKey)
                values.remove(HighContrastKey)
                values.remove(SimpleLanguageKey)
                values.remove(AutoReadKey)
                values.remove(MoodDiaryKey)

                val iterator = serializedPreferences.keys()
                while (iterator.hasNext()) {
                    val name = iterator.next()
                    if (name !in knownKeys) continue
                    val item = serializedPreferences.getJSONObject(name)
                    val type = item.getString("type")
                    when (name) {
                        FavoritesKey.name -> values[FavoritesKey] = item.getJSONArray("value").toStringSet()
                        CompletedKey.name -> values[CompletedKey] = item.getJSONArray("value").toStringSet()
                        ProgressKey.name -> values[ProgressKey] = item.getString("value")
                        LargeTextKey.name -> values[LargeTextKey] = item.getBoolean("value")
                        CustomGuidesKey.name -> values[CustomGuidesKey] = item.getString("value")
                        PlannerKey.name -> values[PlannerKey] = item.getString("value")
                        RecentGuidesKey.name -> values[RecentGuidesKey] = item.getString("value")
                        HighContrastKey.name -> values[HighContrastKey] = item.getBoolean("value")
                        SimpleLanguageKey.name -> values[SimpleLanguageKey] = item.getBoolean("value")
                        AutoReadKey.name -> values[AutoReadKey] = item.getBoolean("value")
                        MoodDiaryKey.name -> values[MoodDiaryKey] = item.getString("value")
                    }
                    @Suppress("UNUSED_VARIABLE")
                    val validatedType = type
                }
            }
        }
    }

    private fun validatePreferenceTypes(preferences: JSONObject) {
        val iterator = preferences.keys()
        while (iterator.hasNext()) {
            val name = iterator.next()
            if (name !in knownKeys) continue
            val item = preferences.getJSONObject(name)
            when (name) {
                FavoritesKey.name, CompletedKey.name -> {
                    require(item.getString("type") == "stringSet")
                    item.getJSONArray("value").toStringList().forEach { require(it.length <= 256) }
                }
                ProgressKey.name, CustomGuidesKey.name, PlannerKey.name, RecentGuidesKey.name, MoodDiaryKey.name -> {
                    require(item.getString("type") == "string")
                    require(item.getString("value").length <= 5_000_000)
                }
                LargeTextKey.name, HighContrastKey.name, SimpleLanguageKey.name, AutoReadKey.name -> {
                    require(item.getString("type") == "boolean")
                }
            }
        }
    }

    private fun JSONArray.toStringList(): List<String> =
        (0 until length()).map { getString(it) }

    private fun JSONArray.toStringSet(): Set<String> = toStringList().toSet()
}

private fun java.util.zip.ZipInputStream.readLimitedBytes(remainingLimit: Long): ByteArray {
    require(remainingLimit >= 0) { "Back-up is te groot." }
    val out = ByteArrayOutputStream()
    val buffer = ByteArray(8192)
    var countTotal = 0L
    while (true) {
        val count = read(buffer)
        if (count < 0) break
        countTotal += count
        require(countTotal <= remainingLimit) { "De back-up is groter dan toegestaan." }
        out.write(buffer, 0, count)
    }
    return out.toByteArray()
}
