package nl.draveinx.klaarkompas

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import java.util.UUID
import nl.draveinx.klaarkompas.data.CustomGuideCodec
import nl.draveinx.klaarkompas.data.Guide
import nl.draveinx.klaarkompas.data.GuideStep
import nl.draveinx.klaarkompas.data.GuideRepository
import nl.draveinx.klaarkompas.support.AdaptiveHelp
import nl.draveinx.klaarkompas.support.HelpStyle

internal val Context.dataStore by preferencesDataStore(name = "klaarkompas_preferences")
private const val PrivacyPolicyUrl = "https://github.com/DRAVEINX/KlaarKompas/blob/main/docs/PRIVACY_POLICY_NL.md"

private val Ink = Color(0xFFEAF5FC)
private val Teal = Color(0xFF39C6E8)
private val Mint = Color(0xFF113448)
private val AppBackground = Color(0xFF061321)
private val Muted = Color(0xFFA6BECC)
private val Gold = Color(0xFFFFCF70)
private val Panel = Color(0xFF0B2032)
private val PanelElevated = Color(0xFF102B40)
private val Border = Color(0xFF234357)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appContext = this@MainActivity
            val preferencesFlow = remember(appContext) {
                appContext.dataStore.data.catch { emit(emptyPreferences()) }
            }
            val preferences by preferencesFlow.collectAsState(initial = emptyPreferences())
            KlaarKompasTheme(highContrast = preferences[HighContrastKey] ?: false) {
                KlaarKompasApp()
            }
        }
    }
}

private enum class Destination(val label: String, val icon: ImageVector) {
    Home("Start", Icons.Outlined.Home),
    Explore("Ontdekken", Icons.Outlined.Explore),
    Saved("Mijn gidsen", Icons.Outlined.BookmarkBorder),
    Planner("Dagplanner", Icons.Outlined.CalendarMonth),
    Together("Samen-doen", Icons.Outlined.Lightbulb),
    Create("Gids maken", Icons.Outlined.Add),
    More("Meer", Icons.AutoMirrored.Outlined.MenuBook),
    Settings("Instellingen", Icons.Outlined.Settings)
}

@Composable
private fun KlaarKompasTheme(highContrast: Boolean = false, content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        primary = if (highContrast) Color(0xFFFFF176) else Teal,
        onPrimary = Color(0xFF000000),
        primaryContainer = Mint,
        onPrimaryContainer = Ink,
        secondary = if (highContrast) Color.White else Gold,
        onSecondary = Color(0xFF000000),
        background = AppBackground,
        onBackground = Ink,
        surface = Panel,
        onSurface = Ink,
        surfaceVariant = PanelElevated,
        onSurfaceVariant = Muted,
        outline = if (highContrast) Color.White else Border,
        error = Color(0xFFFF887F)
    )
    MaterialTheme(colorScheme = scheme, content = content)
}

@Composable
private fun KlaarKompasApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesFlow = remember(context) {
        context.dataStore.data.catch { emit(emptyPreferences()) }
    }
    val preferences by preferencesFlow.collectAsState(initial = emptyPreferences())
    val favorites = preferences[FavoritesKey] ?: emptySet()
    val completed = preferences[CompletedKey] ?: emptySet()
    val progress = remember(preferences[ProgressKey]) { parseProgress(preferences[ProgressKey]) }
    val largeText = preferences[LargeTextKey] ?: false
    val highContrast = preferences[HighContrastKey] ?: false
    val simpleLanguage = preferences[SimpleLanguageKey] ?: false
    val autoReadSteps = preferences[AutoReadKey] ?: false
    val customGuides = remember(preferences[CustomGuidesKey]) { CustomGuideCodec.decode(preferences[CustomGuidesKey]) }
    val plannerTasks = remember(preferences[PlannerKey]) { PlannerTaskCodec.decode(preferences[PlannerKey]) }
    val recentGuideIds = remember(preferences[RecentGuidesKey]) {
        preferences[RecentGuidesKey].orEmpty().split("|").filter { it.isNotBlank() }.distinct().take(8)
    }
    val guideLibrary = remember(customGuides) { GuideRepository.guides + customGuides }

    var destination by rememberSaveable { mutableStateOf(Destination.Home) }
    var editingGuideId by rememberSaveable { mutableStateOf<String?>(null) }
    var activeGuideId by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Alles") }
    var completionMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val activeGuide = guideLibrary.firstOrNull { it.id == activeGuideId }
    val speechController = remember(context) { SpeechController(context) }
    DisposableEffect(speechController) { onDispose { speechController.release() } }

    val toggleFavorite: (String) -> Unit = { id ->
        scope.launch {
            context.dataStore.edit { values ->
                val current = values[FavoritesKey] ?: emptySet()
                values[FavoritesKey] = if (id in current) current - id else current + id
            }
        }
    }
    val saveProgress: (String, Int) -> Unit = { id, nextStep ->
        scope.launch {
            context.dataStore.edit { values ->
                val current = parseProgress(values[ProgressKey]).toMutableMap()
                current[id] = nextStep
                values[ProgressKey] = serializeProgress(current)
                values[CompletedKey] = (values[CompletedKey] ?: emptySet()) - id
            }
        }
    }
    val finishGuide: (String) -> Unit = { id ->
        val title = guideLibrary.firstOrNull { it.id == id }?.title ?: "de handleiding"
        scope.launch {
            context.dataStore.edit { values ->
                val current = parseProgress(values[ProgressKey]).toMutableMap()
                current.remove(id)
                values[ProgressKey] = serializeProgress(current)
                values[CompletedKey] = (values[CompletedKey] ?: emptySet()) + id
            }
        }
        completionMessage = "Goed gedaan! Je hebt ‘$title’ afgerond."
        activeGuideId = null
        destination = Destination.Saved
    }
    val openGuide: (String) -> Unit = { id ->
        activeGuideId = id
        completionMessage = null
        scope.launch {
            context.dataStore.edit { values ->
                val current = values[RecentGuidesKey].orEmpty().split("|").filter { it.isNotBlank() }
                values[RecentGuidesKey] = (listOf(id) + current.filterNot { it == id }).take(8).joinToString("|")
            }
        }
    }

    val backupSaveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null) scope.launch(Dispatchers.IO) {
            val result = runCatching { KlaarKompasBackup.write(context, preferences, uri) }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    if (result.isSuccess) "Back-up gemaakt, inclusief lokale gidsfoto's." else "Back-up mislukt: ${result.exceptionOrNull()?.message ?: "onbekende fout"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    val backupOpenLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) scope.launch(Dispatchers.IO) {
            val result = runCatching { KlaarKompasBackup.restore(context, uri) }
            if (result.isSuccess) {
                val updated = context.dataStore.data.catch { emit(emptyPreferences()) }.first()
                PlannerTaskCodec.decode(updated[PlannerKey]).filter { it.reminderEnabled }.forEach {
                    runCatching { PlannerReminderScheduler.schedule(context, it) }
                }
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    if (result.isSuccess) "Back-up teruggezet." else "Terugzetten mislukt: ${result.exceptionOrNull()?.message ?: "ongeldig bestand"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val savePlannerTask: (PlannerTask) -> Unit = { task ->
        scope.launch {
            context.dataStore.edit { values ->
                val current = PlannerTaskCodec.decode(values[PlannerKey]).filterNot { it.id == task.id }
                values[PlannerKey] = PlannerTaskCodec.encode(current + task)
            }
            if (task.reminderEnabled) PlannerReminderScheduler.schedule(context, task)
            else PlannerReminderScheduler.cancel(context, task.id)
        }
    }
    val deletePlannerTask: (PlannerTask) -> Unit = { task ->
        scope.launch {
            context.dataStore.edit { values ->
                values[PlannerKey] = PlannerTaskCodec.encode(
                    PlannerTaskCodec.decode(values[PlannerKey]).filterNot { it.id == task.id }
                )
            }
            PlannerReminderScheduler.cancel(context, task.id)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = AppBackground) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val tabletLayout = maxWidth >= 760.dp
            Row(Modifier.fillMaxSize()) {
                if (tabletLayout && activeGuide == null) {
                    SideNavigation(
                        selected = destination,
                        onSelect = { destination = it; completionMessage = null }
                    )
                }
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    if (activeGuide == null) {
                        BrandHeader(onSettings = { destination = Destination.Settings })
                    }
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        if (activeGuide != null) {
                            GuidePlayer(
                                guide = activeGuide,
                                initialStep = progress[activeGuide.id] ?: 0,
                                largeText = largeText,
                                tabletLayout = tabletLayout,
                                simpleLanguage = simpleLanguage,
                                autoRead = autoReadSteps,
                                onExit = { activeGuideId = null },
                                onProgress = saveProgress,
                                onComplete = finishGuide,
                                onSpeak = speechController::speak,
                                onShare = { guide ->
                                    val instructions = buildString {
                                        appendLine(guide.title)
                                        appendLine(guide.summary)
                                        appendLine()
                                        guide.steps.forEachIndexed { index, step ->
                                            appendLine("${index + 1}. ${step.title}")
                                            appendLine(step.instruction)
                                            step.tip?.let { appendLine("Tip: $it") }
                                            appendLine()
                                        }
                                        guide.safetyNote?.let { appendLine("Let op: $it") }
                                        appendLine()
                                        append("Gedeeld vanuit KlaarKompas. De ontvanger kan deze tekst zelf opslaan als persoonlijke handleiding.")
                                    }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, guide.title)
                                        putExtra(Intent.EXTRA_TEXT, instructions)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Deel handleiding"))
                                }
                            )
                        } else {
                            when (destination) {
                                Destination.Home -> DashboardScreen(
                                    guides = guideLibrary,
                                    favorites = favorites,
                                    progress = progress,
                                    completed = completed,
                                    recentGuideIds = recentGuideIds,
                                    tasks = plannerTasks,
                                    largeText = largeText,
                                    onOpen = openGuide,
                                    onToggleFavorite = toggleFavorite,
                                    onNavigate = { destination = it }
                                )
                                Destination.Explore -> ExploreScreen(
                                    guides = guideLibrary,
                                    searchQuery = searchQuery,
                                    onSearchChange = { searchQuery = it },
                                    selectedCategory = selectedCategory,
                                    onCategoryChange = { selectedCategory = it },
                                    favorites = favorites,
                                    progress = progress,
                                    completed = completed,
                                    largeText = largeText,
                                    tabletLayout = tabletLayout,
                                    onOpen = openGuide,
                                    onToggleFavorite = toggleFavorite
                                )
                                Destination.Saved -> SavedScreen(
                                    guides = guideLibrary,
                                    favorites = favorites,
                                    progress = progress,
                                    completed = completed,
                                    completionMessage = completionMessage,
                                    largeText = largeText,
                                    onDismissMessage = { completionMessage = null },
                                    onOpen = openGuide,
                                    onToggleFavorite = toggleFavorite,
                                    onEdit = { guide -> editingGuideId = guide.id; destination = Destination.Create },
                                    onDelete = { guide ->
                                        scope.launch {
                                            context.dataStore.edit { values ->
                                                val custom = CustomGuideCodec.decode(values[CustomGuidesKey]).filterNot { it.id == guide.id }
                                                values[CustomGuidesKey] = CustomGuideCodec.encode(custom)
                                                values[FavoritesKey] = (values[FavoritesKey] ?: emptySet()) - guide.id
                                                values[CompletedKey] = (values[CompletedKey] ?: emptySet()) - guide.id
                                                val currentProgress = parseProgress(values[ProgressKey]).toMutableMap()
                                                currentProgress.remove(guide.id)
                                                values[ProgressKey] = serializeProgress(currentProgress)
                                                values[RecentGuidesKey] = values[RecentGuidesKey].orEmpty().split("|").filter { it.isNotBlank() && it != guide.id }.joinToString("|")
                                            }
                                        }
                                        completionMessage = "‘${guide.title}’ is verwijderd."
                                    },
                                    onDuplicate = { guide ->
                                        val copy = guide.copy(id = "custom-${UUID.randomUUID()}", title = "${guide.title} (kopie)")
                                        scope.launch {
                                            context.dataStore.edit { values ->
                                                val existing = CustomGuideCodec.decode(values[CustomGuidesKey])
                                                values[CustomGuidesKey] = CustomGuideCodec.encode(existing + copy)
                                            }
                                        }
                                        completionMessage = "Kopie van ‘${guide.title}’ gemaakt."
                                    }
                                )
                                Destination.Planner -> PlannerScreen(
                                    tasks = plannerTasks,
                                    largeText = largeText,
                                    onSaveTask = savePlannerTask,
                                    onDeleteTask = deletePlannerTask,
                                    onToggleTask = { task, done ->
                                        savePlannerTask(task.copy(completed = done, reminderEnabled = if (done) false else task.reminderEnabled))
                                    },
                                    onSpeak = speechController::speak
                                )
                                Destination.Together -> TogetherScreen(
                                    largeText = largeText,
                                    onSpeak = speechController::speak
                                )
                                Destination.Create -> CreateGuideScreen(
                                    largeText = largeText,
                                    initialGuide = editingGuideId?.let { id -> guideLibrary.firstOrNull { it.id == id } },
                                    onCancel = { editingGuideId = null; destination = Destination.Saved },
                                    onSave = { guide ->
                                        scope.launch {
                                            context.dataStore.edit { values ->
                                                val existing = CustomGuideCodec.decode(values[CustomGuidesKey])
                                                values[CustomGuidesKey] = CustomGuideCodec.encode(existing.filterNot { it.id == guide.id } + guide)
                                            }
                                        }
                                        completionMessage = if (editingGuideId == null) "Je handleiding is opgeslagen bij Mijn gidsen." else "Je handleiding is bijgewerkt."
                                        editingGuideId = null
                                        destination = Destination.Saved
                                    }
                                )
                                Destination.More -> MoreScreen(
                                    largeText = largeText,
                                    onNavigate = { destination = it }
                                )
                                Destination.Settings -> SettingsScreen(
                                    largeText = largeText,
                                    highContrast = highContrast,
                                    simpleLanguage = simpleLanguage,
                                    autoRead = autoReadSteps,
                                    onLargeTextChange = { enabled ->
                                        scope.launch { context.dataStore.edit { it[LargeTextKey] = enabled } }
                                    },
                                    onHighContrastChange = { enabled ->
                                        scope.launch { context.dataStore.edit { it[HighContrastKey] = enabled } }
                                    },
                                    onSimpleLanguageChange = { enabled ->
                                        scope.launch { context.dataStore.edit { it[SimpleLanguageKey] = enabled } }
                                    },
                                    onAutoReadChange = { enabled ->
                                        scope.launch { context.dataStore.edit { it[AutoReadKey] = enabled } }
                                    },
                                    onBackup = { backupSaveLauncher.launch("KlaarKompas-back-up-${LocalDate.now()}.zip") },
                                    onRestore = { backupOpenLauncher.launch(arrayOf("application/zip", "application/octet-stream")) }
                                )
                            }
                        }
                    }
                    if (!tabletLayout && activeGuide == null) {
                        BottomNavigation(selected = destination, onSelect = {
                            destination = it
                            completionMessage = null
                            editingGuideId = null
                        })
                    }
                }
            }
        }
    }
}

private object GuideLookup {
    fun byId(id: String?): nl.draveinx.klaarkompas.data.Guide? =
        if (id == null) null else nl.draveinx.klaarkompas.data.GuideRepository.guides.firstOrNull { it.id == id }
}

private fun parseProgress(raw: String?): Map<String, Int> {
    if (raw.isNullOrBlank()) return emptyMap()
    return raw.split(';').mapNotNull { item ->
        val parts = item.split(':', limit = 2)
        val index = parts.getOrNull(1)?.toIntOrNull()
        if (parts.size == 2 && index != null && index >= 0) parts[0] to index else null
    }.toMap()
}

private fun serializeProgress(progress: Map<String, Int>): String =
    progress.entries.joinToString(";") { "${it.key}:${it.value}" }

@Composable
private fun BrandHeader(onSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Panel).padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(Teal), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Explore, contentDescription = "KlaarKompas-kompaslogo", tint = Color.White, modifier = Modifier.size(29.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("KlaarKompas", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Kleine stappen. Echte vooruitgang.", color = Color(0xFFC4D3DE), fontSize = 12.sp)
        }
        IconButton(onClick = onSettings, modifier = Modifier.semantics { contentDescription = "Open instellingen" }) {
            Icon(Icons.Outlined.Settings, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
private fun SideNavigation(selected: Destination, onSelect: (Destination) -> Unit) {
    Column(
        Modifier.width(246.dp).fillMaxHeight().background(Panel).padding(horizontal = 14.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Explore, contentDescription = null, tint = Teal, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(9.dp))
            Text("KlaarKompas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))
        Destination.entries.forEach { item ->
            val active = item == selected
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp))
                    .background(if (active) PanelElevated else Color.Transparent)
                    .clickable { onSelect(item) }
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(item.icon, contentDescription = null, tint = if (active) Teal else Color(0xFFC4D3DE))
                Text(item.label, color = Color.White, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("Praktische hulp, stap voor stap.", color = Color(0xFFC4D3DE), fontSize = 12.sp, modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun BottomNavigation(selected: Destination, onSelect: (Destination) -> Unit) {
    NavigationBar(containerColor = Panel, windowInsets = WindowInsets.navigationBars) {
        Destination.entries.forEach { item ->
            NavigationBarItem(
                selected = item == selected,
                onClick = { onSelect(item) },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
private fun TrailHeroCard(
    largeText: Boolean,
    tabletLayout: Boolean,
    onExplore: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, Border.copy(alpha = 0.85f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (tabletLayout) 290.dp else 282.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF071827),
                            Color(0xFF0D3448),
                            Color(0xFF092033)
                        )
                    )
                )
        ) {
            Canvas(Modifier.matchParentSize()) {
                // Decorative topographic contour lines: visual texture only, not a real map.
                for (i in 0..8) {
                    val shift = i * 19f
                    val contour = Path().apply {
                        moveTo(size.width * 0.58f + shift, -22f)
                        cubicTo(
                            size.width * 0.95f + shift, size.height * 0.12f,
                            size.width * 0.39f - shift, size.height * 0.29f,
                            size.width * 0.70f + shift, size.height * 0.48f
                        )
                        cubicTo(
                            size.width * 0.95f + shift, size.height * 0.66f,
                            size.width * 0.43f - shift, size.height * 0.81f,
                            size.width * 0.63f + shift, size.height + 24f
                        )
                    }
                    drawPath(
                        contour,
                        color = Teal.copy(alpha = 0.08f + i * 0.012f),
                        style = Stroke(width = 1.25f)
                    )
                }
                val route = Path().apply {
                    moveTo(size.width * 0.76f, size.height * 0.86f)
                    cubicTo(
                        size.width * 0.55f, size.height * 0.70f,
                        size.width * 0.93f, size.height * 0.53f,
                        size.width * 0.72f, size.height * 0.33f
                    )
                    cubicTo(
                        size.width * 0.61f, size.height * 0.23f,
                        size.width * 0.79f, size.height * 0.18f,
                        size.width * 0.85f, size.height * 0.08f
                    )
                }
                drawPath(
                    route,
                    color = Teal.copy(alpha = 0.9f),
                    style = Stroke(
                        width = 2.4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(7f, 10f), 0f)
                    )
                )
                drawCircle(
                    color = Teal,
                    radius = 5.5f,
                    center = Offset(size.width * 0.76f, size.height * 0.86f)
                )
                drawCircle(
                    color = Teal.copy(alpha = 0.22f),
                    radius = 13f,
                    center = Offset(size.width * 0.76f, size.height * 0.86f)
                )
                drawCircle(
                    color = Color(0xFF76F1C6),
                    radius = 4f,
                    center = Offset(size.width * 0.85f, size.height * 0.08f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(if (tabletLayout) 0.66f else 0.82f)
                    .padding(
                        horizontal = if (tabletLayout) 26.dp else 18.dp,
                        vertical = if (tabletLayout) 24.dp else 20.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Teal.copy(alpha = 0.13f))
                        .padding(horizontal = 11.dp, vertical = 6.dp)
                ) {
                    Text(
                        "JOUW PERSOONLIJKE KOMPAS",
                        color = Teal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text(
                        "Vind je weg in",
                        color = Color.White,
                        fontSize = if (tabletLayout) 34.sp else if (largeText) 29.sp else 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = if (tabletLayout) 40.sp else 31.sp
                    )
                    Text(
                        "kleine stappen",
                        color = Teal,
                        fontSize = if (tabletLayout) 34.sp else if (largeText) 29.sp else 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = if (tabletLayout) 40.sp else 31.sp
                    )
                }
                Text(
                    "Duidelijke uitleg voor dagelijkse taken — rustig, overzichtelijk en op jouw tempo.",
                    color = Color(0xFFC7DCE7),
                    fontSize = if (largeText) 16.sp else 13.sp,
                    lineHeight = if (largeText) 22.sp else 18.sp
                )
                Spacer(Modifier.height(2.dp))
                Button(
                    onClick = onExplore,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal),
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 11.dp)
                ) {
                    Icon(Icons.Outlined.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("Ontdek handleidingen", fontWeight = FontWeight.SemiBold)
                }
                Text(
                    "OOK ZONDER INTERNET BESCHIKBAAR",
                    color = Color(0xFFA8C3D2),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.45.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreScreen(
    guides: List<Guide>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    favorites: Set<String>,
    progress: Map<String, Int>,
    completed: Set<String>,
    largeText: Boolean,
    tabletLayout: Boolean,
    onOpen: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val allGuides = guides.filter { guide ->
        (selectedCategory == "Alles" || guide.category == selectedCategory) &&
            (searchQuery.isBlank() ||
                guide.title.contains(searchQuery, ignoreCase = true) ||
                guide.summary.contains(searchQuery, ignoreCase = true) ||
                guide.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
                guide.steps.any { it.title.contains(searchQuery, ignoreCase = true) || it.instruction.contains(searchQuery, ignoreCase = true) })
    }
    val inProgress = guides.filter { it.id in progress.keys && it.id !in completed }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        item {
            Text("Waar wil je mee aan de slag?", fontSize = if (largeText) 28.sp else 24.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(6.dp))
            Text("Zoek een duidelijke uitleg en voer de stappen op je eigen tempo uit.", color = Muted, fontSize = if (largeText) 17.sp else 14.sp)
            Spacer(Modifier.height(18.dp))
            TrailHeroCard(
                largeText = largeText,
                tabletLayout = tabletLayout,
                onExplore = {
                    onSearchChange("")
                    onCategoryChange("Alles")
                }
            )
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Zoek in alle handleidingen" },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Bijv. wifi, cv of foto's") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) }
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 2.dp)) {
                items(nl.draveinx.klaarkompas.data.GuideRepository.categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category) }
                    )
                }
            }
        }
        if (searchQuery.isBlank() && selectedCategory == "Alles" && inProgress.isNotEmpty()) {
            item { SectionHeading("Ga verder waar je gebleven was", "Je voortgang wordt op dit toestel bewaard.") }
            items(inProgress.take(3), key = { "resume-${it.id}" }) { guide ->
                GuideCard(guide, isFavorite = guide.id in favorites, progressIndex = progress[guide.id], isCompleted = false, largeText = largeText, onOpen = { onOpen(guide.id) }, onToggleFavorite = { onToggleFavorite(guide.id) })
            }
        }
        item {
            SectionHeading(
                if (searchQuery.isBlank()) "Ontdek handleidingen" else "Zoekresultaten",
                if (allGuides.isEmpty()) "Pas je zoekterm aan of kies een andere categorie." else "${allGuides.size} ${if (allGuides.size == 1) "handleiding" else "handleidingen"} beschikbaar, ook offline."
            )
        }
        if (allGuides.isEmpty()) {
            item { EmptyState("Geen handleidingen gevonden", "Probeer een korter woord of kies ‘Alles’.", Icons.Outlined.Search) }
        } else {
            items(allGuides, key = { it.id }) { guide ->
                GuideCard(
                    guide = guide,
                    isFavorite = guide.id in favorites,
                    progressIndex = progress[guide.id],
                    isCompleted = guide.id in completed,
                    largeText = largeText,
                    onOpen = { onOpen(guide.id) },
                    onToggleFavorite = { onToggleFavorite(guide.id) }
                )
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun SavedScreen(
    guides: List<Guide>,
    favorites: Set<String>,
    progress: Map<String, Int>,
    completed: Set<String>,
    completionMessage: String?,
    largeText: Boolean,
    onDismissMessage: () -> Unit,
    onOpen: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onEdit: (Guide) -> Unit,
    onDelete: (Guide) -> Unit,
    onDuplicate: (Guide) -> Unit
) {
    var pendingDelete by rememberSaveable { mutableStateOf<Guide?>(null) }
    val savedGuides = guides.filter { guide ->
        guide.id.startsWith("custom-") || guide.id in favorites ||
            (guide.id in progress.keys && guide.id !in completed)
    }
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            item {
                Text("Mijn gidsen", fontSize = if (largeText) 28.sp else 24.sp, fontWeight = FontWeight.Bold, color = Ink)
                Spacer(Modifier.height(4.dp))
                Text("Je eigen handleidingen, favorieten en taken die je wilt hervatten.", color = Muted, fontSize = if (largeText) 17.sp else 14.sp)
            }
            if (completionMessage != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Mint), shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Teal)
                            Text(completionMessage, color = Ink, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                            TextButton(onClick = onDismissMessage) { Text("Sluiten") }
                        }
                    }
                }
            }
            if (savedGuides.isEmpty()) {
                item { EmptyState("Je gidsen staan hier", "Bewaar een handleiding als favoriet, maak zelf een gids of start er een om later verder te gaan.", Icons.Outlined.BookmarkBorder) }
            } else {
                item { SectionHeading("Bewaard en zelfgemaakt", "Eigen gidsen kun je aanpassen, kopiëren of verwijderen.") }
                items(savedGuides, key = { "saved-\${it.id}" }) { guide ->
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        GuideCard(
                            guide = guide,
                            isFavorite = guide.id in favorites,
                            progressIndex = progress[guide.id],
                            isCompleted = guide.id in completed,
                            largeText = largeText,
                            onOpen = { onOpen(guide.id) },
                            onToggleFavorite = { onToggleFavorite(guide.id) }
                        )
                        if (guide.id.startsWith("custom-")) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { onEdit(guide) }) {
                                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(17.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Bewerken")
                                }
                                TextButton(onClick = { onDuplicate(guide) }) {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(17.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Kopiëren")
                                }
                                IconButton(onClick = { pendingDelete = guide }, modifier = Modifier.semantics { contentDescription = "Verwijder \${guide.title}" }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color(0xFFFF887F))
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
        val selectedForDelete = pendingDelete
        if (selectedForDelete != null) {
            AlertDialog(
                onDismissRequest = { pendingDelete = null },
                title = { Text("Handleiding verwijderen?") },
                text = { Text("‘\${selectedForDelete.title}’ wordt van dit apparaat verwijderd. Maak eerst een back-up als je hem wilt bewaren.") },
                confirmButton = {
                    TextButton(onClick = {
                        onDelete(selectedForDelete)
                        pendingDelete = null
                    }) { Text("Verwijderen") }
                },
                dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Annuleren") } },
                containerColor = Panel,
                titleContentColor = Ink,
                textContentColor = Muted
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGuideScreen(
    largeText: Boolean,
    initialGuide: Guide?,
    onCancel: () -> Unit,
    onSave: (Guide) -> Unit
) {
    val context = LocalContext.current
    var title by rememberSaveable(initialGuide?.id) { mutableStateOf(initialGuide?.title.orEmpty()) }
    var summary by rememberSaveable(initialGuide?.id) { mutableStateOf(initialGuide?.summary.orEmpty()) }
    var category by rememberSaveable(initialGuide?.id) { mutableStateOf(initialGuide?.category ?: "Leren & maken") }
    var minutesText by rememberSaveable(initialGuide?.id) { mutableStateOf(initialGuide?.minutes?.toString() ?: "10") }
    var safetyNote by rememberSaveable(initialGuide?.id) { mutableStateOf(initialGuide?.safetyNote.orEmpty()) }
    var stepsText by rememberSaveable(initialGuide?.id) {
        mutableStateOf(initialGuide?.steps?.joinToString("\n") { step ->
            "\${step.title}: \${step.instruction}" + (step.tip?.let { " | Tip: $it" } ?: "")
        }.orEmpty())
    }
    var coverImageUri by rememberSaveable(initialGuide?.id) { mutableStateOf(initialGuide?.coverImageUri) }
    var stepPhotosText by rememberSaveable(initialGuide?.id) {
        mutableStateOf(initialGuide?.steps?.joinToString(",") { it.imageUri.orEmpty() }.orEmpty())
    }
    var photoTarget by rememberSaveable(initialGuide?.id) { mutableStateOf("cover") }
    var selectedStepForPhoto by rememberSaveable(initialGuide?.id) { mutableIntStateOf(0) }
    var voiceTarget by rememberSaveable(initialGuide?.id) { mutableStateOf("steps") }

    val normalizedLines = stepsText.lineSequence().map { it.trim() }.filter { it.isNotBlank() }.take(20).toList()
    val photoUris = stepPhotosText.split(",")
    val parsedSteps = normalizedLines.mapIndexed { index, line ->
        val tipParts = line.split("| Tip:", limit = 2)
        val body = tipParts[0]
        val parts = body.split(":", limit = 2)
        val stepTitle = if (parts.size == 2) parts[0].trim().ifBlank { "Stap \${index + 1}" } else "Stap \${index + 1}"
        val instruction = if (parts.size == 2) parts[1].trim() else body
        GuideStep(
            title = stepTitle,
            instruction = instruction,
            tip = tipParts.getOrNull(1)?.trim()?.takeIf { it.isNotBlank() },
            imageUri = photoUris.getOrNull(index)?.takeIf { it.isNotBlank() }
        )
    }.filter { it.instruction.isNotBlank() }

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            when (voiceTarget) {
                "title" -> if (spoken.isNotBlank()) title = spoken.take(90)
                "summary" -> if (spoken.isNotBlank()) summary = spoken.take(220)
                "safety" -> if (spoken.isNotBlank()) safetyNote = spoken.take(600)
                else -> if (spoken.isNotBlank()) {
                    stepsText = (stepsText + if (stepsText.isBlank()) "" else "\n" + spoken).lineSequence().take(20).joinToString("\n").take(12000)
                }
            }
        }
    }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { sourceUri ->
        if (sourceUri != null) {
            val saved = copyGuideImage(context, sourceUri)
            if (saved != null) {
                if (photoTarget == "cover") {
                    coverImageUri = saved
                } else {
                    val index = photoTarget.removePrefix("step:").toIntOrNull() ?: 0
                    val photos = stepPhotosText.split(",").toMutableList()
                    while (photos.size <= index) photos.add("")
                    photos[index] = saved
                    stepPhotosText = photos.joinToString(",")
                    selectedStepForPhoto = index
                }
            } else {
                Toast.makeText(context, "De foto kon niet worden opgeslagen. Kies een afbeelding kleiner dan 10 MB.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun startDictation(target: String) {
        voiceTarget = target
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "nl-NL")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Spreek rustig in het Nederlands")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            runCatching { speechLauncher.launch(intent) }.onFailure {
                Toast.makeText(context, "Spraakinvoer is niet beschikbaar op dit apparaat.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Schakel een Android-spraakdienst in om tekst in te spreken.", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(onClick = onCancel, modifier = Modifier.semantics { contentDescription = "Gidseditor sluiten" }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            }
            Column(Modifier.weight(1f)) {
                Text(if (initialGuide == null) "Maak je eigen handleiding" else "Handleiding bewerken", fontSize = if (largeText) 27.sp else 23.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Typ of spreek de stappen in en voeg foto's toe.", color = Muted)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it.take(90) }, modifier = Modifier.fillMaxWidth(), label = { Text("Titel van je handleiding") }, placeholder = { Text("Bijv. Mijn tablet klaarmaken") }, singleLine = true)
                TextButton(onClick = { startDictation("title") }) {
                    Icon(Icons.Outlined.Mic, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Titel inspreken")
                }
                OutlinedTextField(value = summary, onValueChange = { summary = it.take(220) }, modifier = Modifier.fillMaxWidth(), label = { Text("Korte uitleg") }, minLines = 2, maxLines = 4)
                TextButton(onClick = { startDictation("summary") }) {
                    Icon(Icons.Outlined.Mic, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Uitleg inspreken")
                }
                OutlinedTextField(value = minutesText, onValueChange = { minutesText = it.filter(Char::isDigit).take(3) }, modifier = Modifier.fillMaxWidth(), label = { Text("Geschatte tijd in minuten") }, singleLine = true)
                Text("Kies een categorie", color = Ink, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(GuideRepository.categories.drop(1)) { item ->
                        FilterChip(selected = category == item, onClick = { category = item }, label = { Text(item) })
                    }
                }
                Text("Omslagfoto", color = Ink, fontWeight = FontWeight.SemiBold)
                if (coverImageUri != null) GuidePhoto(coverImageUri, Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(14.dp)))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { photoTarget = "cover"; photoPicker.launch(arrayOf("image/*")) }) {
                        Text(if (coverImageUri == null) "Omslagfoto kiezen" else "Omslagfoto wijzigen")
                    }
                    if (coverImageUri != null) TextButton(onClick = { coverImageUri = null }) { Text("Verwijderen") }
                }
                OutlinedTextField(
                    value = stepsText,
                    onValueChange = { stepsText = it.lineSequence().take(20).joinToString("\n").take(12000) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Stappen (maximaal 20)") },
                    placeholder = { Text("Titel: uitleg | Tip: extra uitleg") },
                    minLines = 5,
                    maxLines = 12,
                    supportingText = { Text("\${parsedSteps.size}/20 stappen") }
                )
                TextButton(onClick = { startDictation("steps") }) {
                    Icon(Icons.Outlined.Mic, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Volgende stap inspreken")
                }
                Text("Foto toevoegen aan stap", color = Ink, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = { selectedStepForPhoto = (selectedStepForPhoto - 1).coerceAtLeast(0) }, enabled = selectedStepForPhoto > 0) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Vorige stap")
                    }
                    Text("Stap \${selectedStepForPhoto + 1}", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { selectedStepForPhoto = (selectedStepForPhoto + 1).coerceAtMost((parsedSteps.size - 1).coerceAtLeast(0)) }, enabled = selectedStepForPhoto < parsedSteps.lastIndex) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Volgende stap")
                    }
                }
                val selectedPhoto = photoUris.getOrNull(selectedStepForPhoto)?.takeIf { it.isNotBlank() }
                if (selectedPhoto != null) GuidePhoto(selectedPhoto, Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(14.dp)))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { photoTarget = "step:$selectedStepForPhoto"; photoPicker.launch(arrayOf("image/*")) },
                        enabled = parsedSteps.isNotEmpty()
                    ) { Text("Foto kiezen") }
                    if (selectedPhoto != null) TextButton(onClick = {
                        val values = stepPhotosText.split(",").toMutableList()
                        if (selectedStepForPhoto < values.size) values[selectedStepForPhoto] = ""
                        stepPhotosText = values.joinToString(",")
                    }) { Text("Foto verwijderen") }
                }
                OutlinedTextField(
                    value = safetyNote,
                    onValueChange = { safetyNote = it.take(600) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Waarschuwing / let op (optioneel)") },
                    minLines = 2,
                    maxLines = 4
                )
                TextButton(onClick = { startDictation("safety") }) {
                    Icon(Icons.Outlined.Mic, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Waarschuwing inspreken")
                }
            }
        }
        Text("Tips zijn optioneel: gebruik ‘ | Tip: ’ achter een stap om extra uitleg toe te voegen.", color = Muted, fontSize = if (largeText) 16.sp else 14.sp)
        Button(
            onClick = {
                onSave(Guide(
                    id = initialGuide?.id ?: "custom-\${UUID.randomUUID()}",
                    title = title.trim(),
                    summary = summary.trim().ifBlank { "Een persoonlijke handleiding." },
                    category = category,
                    minutes = minutesText.toIntOrNull()?.coerceIn(1, 600) ?: 10,
                    tags = listOf("mijn gids", "persoonlijk", category.lowercase(Locale.ROOT)),
                    steps = parsedSteps,
                    safetyNote = safetyNote.trim().takeIf { it.isNotBlank() },
                    coverImageUri = coverImageUri
                ))
            },
            enabled = title.isNotBlank() && parsedSteps.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal)
        ) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (initialGuide == null) "Handleiding opslaan" else "Wijzigingen opslaan")
        }
        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Annuleren") }
    }
}

@Composable
private fun SettingsScreen(
    largeText: Boolean,
    highContrast: Boolean,
    simpleLanguage: Boolean,
    autoRead: Boolean,
    onLargeTextChange: (Boolean) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onSimpleLanguageChange: (Boolean) -> Unit,
    onAutoReadChange: (Boolean) -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit
) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Instellingen", fontSize = if (largeText) 28.sp else 24.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("Pas tekst, uitleg, geluid en je persoonlijke gegevens aan.", color = Muted, fontSize = if (largeText) 17.sp else 14.sp)
        SettingSwitchCard("Grotere tekst", "Ruimere tekst in handleidingen en overzichten.", largeText, onLargeTextChange)
        SettingSwitchCard("Hoog contrast", "Lichtere accenten en opvallendere randen voor betere zichtbaarheid.", highContrast, onHighContrastChange)
        SettingSwitchCard("Eenvoudige uitleg", "Kies kortere, duidelijkere teksten bij hulp en begeleiding.", simpleLanguage, onSimpleLanguageChange)
        SettingSwitchCard("Stappen automatisch voorlezen", "Lees iedere nieuwe stap hardop zodra je die opent.", autoRead, onAutoReadChange)
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Backup, contentDescription = null, tint = Teal, modifier = Modifier.size(28.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Back-up en terugzetten", fontWeight = FontWeight.SemiBold, fontSize = if (largeText) 19.sp else 16.sp)
                        Text("Bewaar gidsen, favorieten, dagplanning, voortgang en lokale foto's in één ZIP-bestand. Gebruik hetzelfde bestand om gegevens op een ander toestel terug te zetten.", color = Muted, fontSize = if (largeText) 16.sp else 13.sp)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onBackup, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Back-up maken")
                    }
                    OutlinedButton(onClick = onRestore, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Terugzetten")
                    }
                }
                Text("Terugzetten vervangt de huidige gegevens door de back-up. Bewaar eerst een back-up van de huidige gegevens als je ze nog nodig hebt.", color = Gold, fontSize = 12.sp)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Offline en privacy", fontWeight = FontWeight.SemiBold, fontSize = if (largeText) 19.sp else 16.sp)
                Text("Gidsen, favorieten, voortgang, foto's en je dagplanner worden lokaal bewaard. Deze versie heeft geen account, eigen cloud of online synchronisatie. Spraakherkenning gebruikt de Android-spraakdienst van je toestel. Herinneringen zijn lokale meldingen.", color = Muted, fontSize = if (largeText) 16.sp else 14.sp)
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PrivacyPolicyUrl))
                        if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
                    },
                    modifier = Modifier.semantics { contentDescription = "Lees de privacyverklaring" }
                ) { Text("Lees de privacyverklaring", fontSize = if (largeText) 16.sp else 14.sp) }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Mint), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Kleine stappen. Echte vooruitgang.", fontWeight = FontWeight.Bold, color = Ink, fontSize = if (largeText) 19.sp else 16.sp)
                Text("KlaarKompas werkt offline. Controleer belangrijke veiligheids-, juridische of financiële informatie altijd bij een passende, betrouwbare bron.", color = Ink, fontSize = if (largeText) 16.sp else 13.sp)
            }
        }
    }
}

@Composable
private fun SettingSwitchCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Border)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Ink, fontWeight = FontWeight.SemiBold)
                Text(description, color = Muted, fontSize = 13.sp)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange, m
@Composable
private fun DashboardScreen(
    guides: List<Guide>,
    favorites: Set<String>,
    progress: Map<String, Int>,
    completed: Set<String>,
    recentGuideIds: List<String>,
    tasks: List<PlannerTask>,
    largeText: Boolean,
    onOpen: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onNavigate: (Destination) -> Unit
) {
    val today = LocalDate.now().toString()
    val todayTasks = tasks.filter { it.date == today }
    val recentGuides = recentGuideIds.mapNotNull { id -> guides.firstOrNull { it.id == id } }
    val favoriteGuides = guides.filter { it.id in favorites }.take(4)
    val inProgress = guides.filter { it.id in progress.keys && it.id !in completed }.take(3)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Jouw KlaarKompas", color = Ink, fontSize = if (largeText) 30.sp else 26.sp, fontWeight = FontWeight.Bold)
            Text("Goed dat je er bent. Wat wil je vandaag doen?", color = Muted, fontSize = if (largeText) 18.sp else 14.sp)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Panel),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(Mint), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Home, contentDescription = null, tint = Teal, modifier = Modifier.size(28.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Een stap tegelijk", color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Text("Je gidsen en planning staan hier bij elkaar.", color = Muted, fontSize = 13.sp)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { onNavigate(Destination.Explore) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Outlined.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Ontdekken")
                        }
                        OutlinedButton(onClick = { onNavigate(Destination.Planner) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Dagplanner")
                        }
                    }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Teal, modifier = Modifier.size(26.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Vandaag", color = Ink, fontWeight = FontWeight.Bold)
                        Text(
                            when (todayTasks.count { it.completed }) {
                                0 -> "\${todayTasks.size} geplande \${if (todayTasks.size == 1) "taak" else "taken"}"
                                else -> "\${todayTasks.count { it.completed }} van \${todayTasks.size} taken afgerond"
                            },
                            color = Muted,
                            fontSize = 13.sp
                        )
                    }
                    TextButton(onClick = { onNavigate(Destination.Planner) }) { Text("Openen") }
                }
            }
        }
        if (inProgress.isNotEmpty()) {
            item { SectionHeading("Ga verder", "Je voortgang is op dit apparaat bewaard.") }
            items(inProgress, key = { "dash-progress-\${it.id}" }) { guide ->
                GuideCard(guide, guide.id in favorites, progress[guide.id], guide.id in completed, largeText, { onOpen(guide.id) }, { onToggleFavorite(guide.id) })
            }
        }
        if (recentGuides.isNotEmpty()) {
            item { SectionHeading("Recent geopend", "Pak een gids er snel weer bij.") }
            items(recentGuides.take(4), key = { "recent-\${it.id}" }) { guide ->
                GuideCard(guide, guide.id in favorites, progress[guide.id], guide.id in completed, largeText, { onOpen(guide.id) }, { onToggleFavorite(guide.id) })
            }
        }
        if (favoriteGuides.isNotEmpty()) {
            item { SectionHeading("Jouw favorieten", "Gidsen die je vaker gebruikt.") }
            items(favoriteGuides, key = { "dash-fav-\${it.id}" }) { guide ->
                GuideCard(guide, true, progress[guide.id], guide.id in completed, largeText, { onOpen(guide.id) }, { onToggleFavorite(guide.id) })
            }
        }
        if (recentGuides.isEmpty() && favoriteGuides.isEmpty() && inProgress.isEmpty()) {
            item {
                EmptyState("Je startscherm vult zich vanzelf", "Open een handleiding of bewaar een favoriet. Je vindt die daarna hier terug.", Icons.Outlined.Home)
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun PlannerScreen(
    tasks: List<PlannerTask>,
    largeText: Boolean,
    onSaveTask: (PlannerTask) -> Unit,
    onDeleteTask: (PlannerTask) -> Unit,
    onToggleTask: (PlannerTask, Boolean) -> Unit,
    onSpeak: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var newTitle by rememberSaveable { mutableStateOf("") }
    var newTime by rememberSaveable { mutableStateOf("") }
    var addReminder by rememberSaveable { mutableStateOf(false) }
    var pendingReminderTask by remember { mutableStateOf<PlannerTask?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val task = pendingReminderTask
        if (task != null) {
            if (granted) onSaveTask(task)
            else {
                onSaveTask(task.copy(reminderEnabled = false))
                Toast.makeText(context, "De taak is opgeslagen zonder melding. Je kunt meldingen later in Android inschakelen.", Toast.LENGTH_LONG).show()
            }
            pendingReminderTask = null
        }
    }
    val dayTasks = tasks.filter { it.date == selectedDate }.sortedWith(compareBy<PlannerTask> { it.time.isBlank() }.thenBy { it.time }.thenBy { it.title })

    fun submitTask() {
        if (newTitle.isBlank()) {
            Toast.makeText(context, "Vul eerst een taaknaam in.", Toast.LENGTH_SHORT).show()
            return
        }
        val time = newTime.trim()
        if (time.isNotBlank() && runCatching { LocalTime.parse(time) }.isFailure) {
            Toast.makeText(context, "Gebruik een tijd in de vorm 09:30.", Toast.LENGTH_SHORT).show()
            return
        }
        if (addReminder && time.isBlank()) {
            Toast.makeText(context, "Vul een tijd in om een herinnering te plannen.", Toast.LENGTH_SHORT).show()
            return
        }
        val task = PlannerTask(
            id = UUID.randomUUID().toString(),
            title = newTitle.trim().take(160),
            date = selectedDate,
            time = time,
            completed = false,
            reminderEnabled = addReminder
        )
        newTitle = ""
        if (task.reminderEnabled && Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingReminderTask = task
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onSaveTask(task)
        }
    }

    Column(Modifier.fillMaxSize().imePadding().padding(horizontal = 18.dp, vertical = 12.dp)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Mijn dagplanner", color = Ink, fontSize = if (largeText) 29.sp else 25.sp, fontWeight = FontWeight.Bold)
                Text("Plan je taken, vink af wat klaar is en stel optioneel een melding in.", color = Muted, fontSize = if (largeText) 17.sp else 14.sp)
            }
            item {
                val today = LocalDate.now()
                val chosenDate = runCatching { LocalDate.parse(selectedDate) }.getOrDefault(today)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(selected = selectedDate == today.toString(), onClick = { selectedDate = today.toString() }, label = { Text("Vandaag") })
                    FilterChip(selected = selectedDate == today.plusDays(1).toString(), onClick = { selectedDate = today.plusDays(1).toString() }, label = { Text("Morgen") })
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, day -> selectedDate = LocalDate.of(year, month + 1, day).toString() },
                            chosenDate.year,
                            chosenDate.monthValue - 1,
                            chosenDate.dayOfMonth
                        ).show()
                    }) { Text(chosenDate.toString()) }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Border)) {
                    Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Taak toevoegen", color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it.take(160) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Wat wil je doen?") },
                            placeholder = { Text("Bijv. boodschappen doen") },
                            singleLine = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newTime,
                                onValueChange = { newTime = it.filter { c -> c.isDigit() || c == ':' }.take(5) },
                                modifier = Modifier.weight(1f),
                                label = { Text("Tijd (optioneel)") },
                                placeholder = { Text("09:30") },
                                singleLine = true
                            )
                            Button(onClick = { onSpeak("Welke taak wil je plannen?") }) {
                                Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = "Lees taakplanning voor")
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Teal)
                            Column(Modifier.weight(1f)) {
                                Text("Herinnering instellen", color = Ink, fontWeight = FontWeight.Medium)
                                Text("Een melding op de gekozen datum en tijd.", color = Muted, fontSize = 12.sp)
                            }
                            Switch(checked = addReminder, onCheckedChange = { addReminder = it })
                        }
                        Button(onClick = { submitTask() }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                            Spacer(Modifier.width(7.dp))
                            Text("Taak toevoegen")
                        }
                    }
                }
            }
            item { SectionHeading("Taken voor deze dag", "\${dayTasks.count { it.completed }} van \${dayTasks.size} afgerond") }
            if (dayTasks.isEmpty()) {
                item { EmptyState("Nog geen taken gepland", "Voeg hierboven een taak toe. De planning blijft lokaal op dit apparaat.", Icons.Outlined.CalendarMonth) }
            } else {
                items(dayTasks, key = { "planner-\${it.id}" }) { task ->
                    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Border)) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(checked = task.completed, onCheckedChange = { onToggleTask(task, it) })
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(task.title, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = if (largeText) 18.sp else 15.sp)
                                val subtitle = buildString {
                                    if (task.time.isNotBlank()) append(task.time)
                                    if (task.reminderEnabled) {
                                        if (isNotEmpty()) append(" • ")
                                        append("herinnering aan")
                                    }
                                    if (isEmpty()) append("Geen tijd ingesteld")
                                }
                                Text(subtitle, color = if (task.reminderEnabled) Teal else Muted, fontSize = 12.sp)
                            }
                            IconButton(onClick = { onDeleteTask(task) }, modifier = Modifier.semantics { contentDescription = "Verwijder taak \${task.title}" }) {
                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = Muted)
                            }
                        }
                    }
                }
            }
            item {
                Text("Herinneringen gebruiken Android-meldingen. Zet meldingen voor KlaarKompas aan in de Android-instellingen als je geen melding ontvangt.", color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun MoreScreen(largeText: Boolean, onNavigate: (Destination) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Meer functies", color = Ink, fontSize = if (largeText) 29.sp else 25.sp, fontWeight = FontWeight.Bold)
        Text("Open extra hulpmiddelen en instellingen.", color = Muted)
        MoreNavigationCard("Samen-doen", "Maak samen een moeilijke taak eenvoudiger.", Icons.Outlined.Lightbulb) { onNavigate(Destination.Together) }
        MoreNavigationCard("Gids maken", "Schrijf een eigen handleiding, spreek tekst in of voeg foto's toe.", Icons.Outlined.Add) { onNavigate(Destination.Create) }
        MoreNavigationCard("Instellingen en back-up", "Pas toegankelijkheid aan en bewaar je gegevens.", Icons.Outlined.Settings) { onNavigate(Destination.Settings) }
    }
}

@Composable
private fun MoreNavigationCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(45.dp).clip(RoundedCornerShape(13.dp)).background(Mint), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Teal)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Ink, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Muted, fontSize = 13.sp)
            }
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Teal)
        }
    }
}

@Composable
private fun GuidePhoto(reference: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = reference) {
        value = withContext(Dispatchers.IO) {
            runCatching { guideImageFile(context, reference)?.let { BitmapFactory.decodeFile(it.absolutePath) } }.getOrNull()
        }
    }
    if (bitmap != null) {
        Image(bitmap!!.asImageBitmap(), contentDescription = "Foto bij deze handleiding", modifier = modifier, contentScale = ContentScale.Crop)
    } else {
        Box(modifier.background(PanelElevated), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Explore, contentDescription = "Geen foto beschikbaar", tint = Teal)
        }
    }
}

odifier = Modifier.semantics { contentDescription = title })
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(top = 5.dp, bottom = 2.dp)) {
        Text(title, color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, fontSize = 13.sp)
    }
}

@Composable
private fun GuideCard(
    guide: nl.draveinx.klaarkompas.data.Guide,
    isFavorite: Boolean,
    progressIndex: Int?,
    isCompleted: Boolean,
    largeText: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Mint), contentAlignment = Alignment.Center) {
                    Icon(categoryIcon(guide.category), contentDescription = null, tint = Teal, modifier = Modifier.size(22.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(guide.title, fontSize = if (largeText) 19.sp else 16.sp, fontWeight = FontWeight.SemiBold, color = Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(guide.summary, color = Muted, fontSize = if (largeText) 16.sp else 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(42.dp).semantics { contentDescription = if (isFavorite) "Verwijder ${guide.title} uit favorieten" else "Bewaar ${guide.title} als favoriet" }) {
                    Icon(if (isFavorite) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder, contentDescription = null, tint = if (isFavorite) Teal else Muted)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Muted, modifier = Modifier.size(16.dp))
                Text("Ongeveer ${guide.minutes} min", color = Muted, fontSize = 12.sp)
                Text("•", color = Muted)
                Text(guide.category, color = Teal, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                when {
                    isCompleted -> Text("Afgerond", color = Teal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    progressIndex != null -> Text("Verdergaan", color = Teal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            if (progressIndex != null && !isCompleted) {
                LinearProgressIndicator(
                    progress = { ((progressIndex + 1).toFloat() / guide.steps.size).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                    color = Teal,
                    trackColor = PanelElevated
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onOpen) {
                    Text(if (progressIndex != null && !isCompleted) "Ga verder" else if (isCompleted) "Opnieuw bekijken" else "Start handleiding")
                    Spacer(Modifier.width(5.dp))
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(17.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, message: String, icon: ImageVector) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, contentDescription = null, tint = Teal, modifier = Modifier.size(38.dp))
            Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(message, color = Muted, fontSize = 14.sp)
        }
    }
}

private fun categoryIcon(category: String): ImageVector = when (category) {
    "Digitale vaardigheden" -> Icons.Outlined.Explore
    "Thuis" -> Icons.Outlined.Home
    "Praktische zaken" -> Icons.Outlined.CheckCircle
    "Werk & studie" -> Icons.AutoMirrored.Outlined.MenuBook
    else -> Icons.Outlined.Lightbulb
}

@Composable
private fun GuidePlayer(
    guide: nl.draveinx.klaarkompas.data.Guide,
    initialStep: Int,
    largeText: Boolean,
    tabletLayout: Boolean,
    onExit: () -> Unit,
    onProgress: (String, Int) -> Unit,
    onComplete: (String) -> Unit,
    onSpeak: (String) -> Unit
) {
    var stepIndex by rememberSaveable(guide.id) { mutableIntStateOf(initialStep.coerceIn(0, guide.steps.lastIndex)) }
    var showStuckHelp by rememberSaveable(guide.id) { mutableStateOf(false) }
    LaunchedEffect(guide.id, initialStep) {
        if (stepIndex == 0 && initialStep > 0) stepIndex = initialStep.coerceIn(0, guide.steps.lastIndex)
    }
    val step = guide.steps[stepIndex]
    val progressFraction = (stepIndex + 1).toFloat() / guide.steps.size
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().background(PanelElevated).padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onExit, modifier = Modifier.semantics { contentDescription = "Terug naar de gidsen" }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(guide.category, color = Teal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(guide.title, color = Ink, fontSize = if (largeText) 19.sp else 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = { onSpeak("${guide.title}. Stap ${stepIndex + 1}. ${step.title}. ${step.instruction}") }, modifier = Modifier.semantics { contentDescription = "Lees deze stap voor" }) {
                Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null, tint = Teal)
            }
        }
        HorizontalDivider(color = Border)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = if (tabletLayout) 34.dp else 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Stap ${stepIndex + 1} van ${guide.steps.size}", color = Teal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = Teal,
                trackColor = PanelElevated
            )
            if (tabletLayout) {
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.Top) {
                    StepCard(step.title, step.instruction, step.tip, largeText, Modifier.weight(1.6f), onSpeak = { onSpeak("${step.title}. ${step.instruction}") })
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MaterialsCard(guide.steps.size, guide.minutes, largeText)
                        guide.safetyNote?.let { SafetyCard(it, largeText) }
                    }
                }
            } else {
                StepCard(step.title, step.instruction, step.tip, largeText, Modifier.fillMaxWidth(), onSpeak = { onSpeak("${step.title}. ${step.instruction}") })
                MaterialsCard(guide.steps.size, guide.minutes, largeText)
                guide.safetyNote?.let { SafetyCard(it, largeText) }
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF332A18)), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = Gold)
                        Text("Loop je vast?", color = Ink, fontWeight = FontWeight.Bold)
                    }
                    Text("Je hoeft niet te haasten. Lees de stap nog eens, probeer de tip of vraag iemand die je vertrouwt om mee te kijken.", color = Ink, fontSize = if (largeText) 16.sp else 14.sp)
                    if (showStuckHelp) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("1. Maak het kleiner", color = Teal, fontWeight = FontWeight.Bold)
                            Text("Splits deze stap op in één kleine handeling. Controleer alleen dat ene onderdeel voordat je verdergaat.", color = Ink, fontSize = if (largeText) 16.sp else 14.sp)
                            Text("2. Vraag gericht om hulp", color = Teal, fontWeight = FontWeight.Bold)
                            Text("Vertel wat je wilde doen, wat er gebeurde en welke foutmelding of vraag je ziet. Deel geen wachtwoorden of herstelcodes.", color = Ink, fontSize = if (largeText) 16.sp else 14.sp)
                            Text("3. Pauzeer en probeer later opnieuw", color = Teal, fontWeight = FontWeight.Bold)
                            Text("Als je gefrustreerd raakt of iets onveilig voelt, stop dan. Bewaar waar je bent gebleven en probeer het later opnieuw of vraag iemand die je vertrouwt.", color = Ink, fontSize = if (largeText) 16.sp else 14.sp)
                        }
                    }
                    TextButton(onClick = { showStuckHelp = !showStuckHelp }) {
                        Text(if (showStuckHelp) "Verberg extra hulp" else "Help me om de volgende stap te zetten")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                if (stepIndex > 0) {
                    OutlinedButton(
                        onClick = { stepIndex -= 1; onProgress(guide.id, stepIndex) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Vorige")
                    }
                }
                Button(
                    onClick = {
                        if (stepIndex == guide.steps.lastIndex) onComplete(guide.id)
                        else { stepIndex += 1; onProgress(guide.id, stepIndex); showStuckHelp = false }
                    },
                    modifier = Modifier.weight(if (stepIndex > 0) 1.35f else 1f).height(52.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal)
                ) {
                    Text(if (stepIndex == guide.steps.lastIndex) "Afronden" else "Volgende stap")
                    Spacer(Modifier.width(6.dp))
                    Icon(if (stepIndex == guide.steps.lastIndex) Icons.Outlined.CheckCircle else Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun StepCard(
    title: String,
    instruction: String,
    tip: String?,
    largeText: Boolean,
    modifier: Modifier = Modifier,
    onSpeak: () -> Unit
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, Border)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(50.dp).clip(RoundedCornerShape(16.dp)).background(Mint), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = Teal, modifier = Modifier.size(29.dp))
            }
            Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = if (largeText) 27.sp else 22.sp)
            Text(instruction, color = Ink, fontSize = if (largeText) 21.sp else 17.sp, lineHeight = if (largeText) 30.sp else 25.sp)
            if (!tip.isNullOrBlank()) {
                HorizontalDivider(color = Border)
                Text("Tip", color = Teal, fontWeight = FontWeight.Bold, fontSize = if (largeText) 17.sp else 14.sp)
                Text(tip, color = Muted, fontSize = if (largeText) 18.sp else 14.sp, lineHeight = if (largeText) 26.sp else 21.sp)
            }
            OutlinedButton(onClick = onSpeak, shape = RoundedCornerShape(13.dp)) {
                Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null)
                Spacer(Modifier.width(7.dp))
                Text("Lees stap voor")
            }
        }
    }
}

@Composable
private fun MaterialsCard(stepCount: Int, minutes: Int, largeText: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Over deze handleiding", fontWeight = FontWeight.Bold, color = Ink, fontSize = if (largeText) 18.sp else 15.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = null, tint = Teal)
                Text("$stepCount duidelijke stappen", color = Muted, fontSize = if (largeText) 16.sp else 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Teal)
                Text("Ongeveer $minutes minuten", color = Muted, fontSize = if (largeText) 16.sp else 13.sp)
            }
        }
    }
}

@Composable
private fun SafetyCard(note: String, largeText: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF332A18)), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Let op", color = Gold, fontWeight = FontWeight.Bold, fontSize = if (largeText) 18.sp else 15.sp)
            Text(note, color = Ink, fontSize = if (largeText) 16.sp else 13.sp, lineHeight = if (largeText) 23.sp else 19.sp)
        }
    }
}

private class SpeechController(context: Context) : TextToSpeech.OnInitListener {
    private var engine: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val languageResult = engine?.setLanguage(Locale.forLanguageTag("nl-NL"))
            ready = languageResult != TextToSpeech.LANG_MISSING_DATA && languageResult != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun speak(text: String) {
        if (ready) engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "klaarkompas-step")
    }

    fun release() {
        engine?.stop()
        engine?.shutdown()
        engine = null
    }
}


@Composable
private fun TogetherScreen(
    largeText: Boolean,
    onSpeak: (String) -> Unit
) {
    var taskTitle by rememberSaveable { mutableStateOf("Een taak samen doen") }
    var instruction by rememberSaveable { mutableStateOf("Lees de uitleg samen. Doe één stap tegelijk en vraag hulp als je vastloopt.") }
    var helperNote by rememberSaveable { mutableStateOf("") }
    var style by rememberSaveable { mutableStateOf(HelpStyle.SIMPLE) }
    var completed by rememberSaveable { mutableStateOf(false) }
    val titleSize = if (largeText) 28.sp else 24.sp
    val bodySize = if (largeText) 19.sp else 16.sp
    val explanation = AdaptiveHelp.explain(taskTitle, instruction, style)

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Samen-doen", fontSize = titleSize, fontWeight = FontWeight.Bold, color = Ink)
        Text("Kom je ergens niet uit? Vul samen in wat je wilt doen en kies uitleg die bij je past.", fontSize = bodySize, color = Muted)
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Welke taak ga je doen?", fontSize = bodySize, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it.take(100); completed = false },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Naam van de taak") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = instruction,
                    onValueChange = { instruction = it.take(1200); completed = false },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Wat is de volgende stap?") },
                    minLines = 3
                )
                Text("Hoe wil je de uitleg?", fontSize = bodySize, fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HelpStyle.entries.forEach { option ->
                        val label = when (option) {
                            HelpStyle.SIMPLE -> "Eenvoudig"
                            HelpStyle.STANDARD -> "Standaard"
                            HelpStyle.DETAILED -> "Extra uitleg"
                        }
                        val description = when (option) {
                            HelpStyle.SIMPLE -> "Korte uitleg, rustig en stap voor stap"
                            HelpStyle.STANDARD -> "De uitleg zoals je die hebt ingevuld"
                            HelpStyle.DETAILED -> "Met extra aanwijzingen en een controle achteraf"
                        }
                        FilterChip(
                            selected = style == option,
                            onClick = { style = option },
                            label = { Column { Text(label); Text(description, fontSize = 12.sp, color = Muted) } }
                        )
                    }
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Mint), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Jullie volgende stap", fontSize = bodySize, fontWeight = FontWeight.Bold, color = Ink)
                Text(explanation, fontSize = bodySize, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onSpeak(explanation) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Lees voor")
                    }
                    OutlinedButton(onClick = { completed = !completed }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (completed) "Opnieuw" else "Gelukt")
                    }
                }
                if (completed) {
                    Text("Goed gedaan! Bespreek samen wat de volgende stap is.", fontSize = bodySize, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Notitie voor jezelf of je helper", fontSize = bodySize, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = helperNote,
                    onValueChange = { helperNote = it.take(500) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Bijvoorbeeld: waar wil ik hulp bij?") },
                    minLines = 2
                )
                Text("Deze notitie blijft alleen in dit scherm zolang je het gebruikt. Er wordt niets online gedeeld.", fontSize = 13.sp, color = Muted)
            }
        }
    }
}
