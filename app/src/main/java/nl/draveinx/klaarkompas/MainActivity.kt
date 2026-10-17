package nl.draveinx.klaarkompas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import nl.draveinx.klaarkompas.data.CustomGuideCodec
import nl.draveinx.klaarkompas.data.Guide
import nl.draveinx.klaarkompas.data.GuideStep
import nl.draveinx.klaarkompas.data.GuideRepository
import nl.draveinx.klaarkompas.support.AdaptiveHelp
import nl.draveinx.klaarkompas.support.HelpStyle

private val Context.dataStore by preferencesDataStore(name = "klaarkompas_preferences")
private val FavoritesKey = stringSetPreferencesKey("favorite_guides")
private val CompletedKey = stringSetPreferencesKey("completed_guides")
private val ProgressKey = stringPreferencesKey("guide_progress")
private val LargeTextKey = booleanPreferencesKey("large_text")
private val CustomGuidesKey = stringPreferencesKey("custom_guides_v1")
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
        setContent { KlaarKompasTheme { KlaarKompasApp() } }
    }
}

private enum class Destination(val label: String, val icon: ImageVector) {
    Explore("Ontdekken", Icons.Outlined.Explore),
    Saved("Mijn gidsen", Icons.Outlined.BookmarkBorder),
    Together("Samen-doen", Icons.Outlined.Lightbulb),
    Create("Gids maken", Icons.Outlined.Add),
    Settings("Instellingen", Icons.Outlined.Settings)
}

@Composable
private fun KlaarKompasTheme(content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        primary = Teal,
        onPrimary = AppBackground,
        primaryContainer = Mint,
        onPrimaryContainer = Ink,
        secondary = Gold,
        onSecondary = AppBackground,
        background = AppBackground,
        onBackground = Ink,
        surface = Panel,
        onSurface = Ink,
        surfaceVariant = PanelElevated,
        onSurfaceVariant = Muted,
        outline = Border,
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
    val customGuides = remember(preferences[CustomGuidesKey]) { CustomGuideCodec.decode(preferences[CustomGuidesKey]) }
    val guideLibrary = remember(customGuides) { GuideRepository.guides + customGuides }

    var destination by rememberSaveable { mutableStateOf(Destination.Explore) }
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
                                onExit = { activeGuideId = null },
                                onProgress = saveProgress,
                                onComplete = finishGuide,
                                onSpeak = speechController::speak
                            )
                        } else {
                            when (destination) {
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
                                    onToggleFavorite = toggleFavorite
                                )
                                Destination.Together -> TogetherScreen(
                                    largeText = largeText,
                                    onSpeak = speechController::speak
                                )
                                Destination.Create -> CreateGuideScreen(
                                    largeText = largeText,
                                    onCancel = { destination = Destination.Explore },
                                    onSave = { guide ->
                                        scope.launch {
                                            context.dataStore.edit { values ->
                                                val existing = CustomGuideCodec.decode(values[CustomGuidesKey])
                                                values[CustomGuidesKey] = CustomGuideCodec.encode(existing + guide)
                                            }
                                        }
                                        completionMessage = "Je eigen handleiding is opgeslagen en bewaard bij Mijn gidsen."
                                        destination = Destination.Saved
                                    }
                                )
                                Destination.Settings -> SettingsScreen(
                                    largeText = largeText,
                                    onLargeTextChange = { enabled ->
                                        scope.launch { context.dataStore.edit { it[LargeTextKey] = enabled } }
                                    }
                                )
                            }
                        }
                    }
                    if (!tabletLayout && activeGuide == null) {
                        BottomNavigation(selected = destination, onSelect = {
                            destination = it
                            completionMessage = null
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
    onToggleFavorite: (String) -> Unit
) {
    val savedGuides = guides.filter { guide ->
        guide.id in favorites || (guide.id in progress.keys && guide.id !in completed)
    }
    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            item {
                Text("Mijn gidsen", fontSize = if (largeText) 28.sp else 24.sp, fontWeight = FontWeight.Bold, color = Ink)
                Spacer(Modifier.height(4.dp))
                Text("Bewaar wat je vaker gebruikt en hervat je voortgang.", color = Muted, fontSize = if (largeText) 17.sp else 14.sp)
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
                item { EmptyState("Je gidsen staan hier", "Bewaar een handleiding met het bladwijzer-icoon of start er een om later verder te gaan.", Icons.Outlined.BookmarkBorder) }
            } else {
                item { SectionHeading("Bewaard en bezig", "Je favorieten en handleidingen met voortgang.") }
                items(savedGuides, key = { "saved-${it.id}" }) { guide ->
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
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGuideScreen(
    largeText: Boolean,
    onCancel: () -> Unit,
    onSave: (Guide) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var summary by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Leren & maken") }
    var minutesText by rememberSaveable { mutableStateOf("10") }
    var stepsText by rememberSaveable { mutableStateOf("") }
    val parsedSteps = stepsText.lineSequence().map { it.trim() }.filter { it.isNotBlank() }
        .mapIndexed { index, line ->
            val parts = line.split(":", limit = 2)
            if (parts.size == 2) GuideStep(parts[0].trim().ifBlank { "Stap ${index + 1}" }, parts[1].trim())
            else GuideStep("Stap ${index + 1}", line)
        }.filter { it.instruction.isNotBlank() }.take(20).toList()

    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(onClick = onCancel, modifier = Modifier.semantics { contentDescription = "Gids maken annuleren" }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            }
            Column(Modifier.weight(1f)) {
                Text("Maak je eigen handleiding", fontSize = if (largeText) 27.sp else 23.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Schrijf het op in kleine, duidelijke stappen.", color = Muted)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it.take(90) }, modifier = Modifier.fillMaxWidth(), label = { Text("Titel van je handleiding") }, placeholder = { Text("Bijv. Mijn tablet klaarmaken") }, singleLine = true)
                OutlinedTextField(value = summary, onValueChange = { summary = it.take(220) }, modifier = Modifier.fillMaxWidth(), label = { Text("Korte uitleg (optioneel)") }, minLines = 2, maxLines = 4)
                OutlinedTextField(value = minutesText, onValueChange = { minutesText = it.filter(Char::isDigit).take(3) }, modifier = Modifier.fillMaxWidth(), label = { Text("Geschatte tijd in minuten") }, singleLine = true)
                Text("Kies een categorie", color = Ink, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(GuideRepository.categories.drop(1)) { item ->
                        FilterChip(selected = category == item, onClick = { category = item }, label = { Text(item) })
                    }
                }
                OutlinedTextField(
                    value = stepsText,
                    onValueChange = { stepsText = it.lineSequence().take(20).joinToString("\n").take(12000) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Stappen (maximaal 20)") },
                    placeholder = { Text("Eén stap per regel. Gebruik eventueel ‘Titel: uitleg’.") },
                    minLines = 5,
                    maxLines = 12,
                    supportingText = { Text("${parsedSteps.size}/20 stappen") }
                )
            }
        }
        Text("Tip: gebruik één handeling per regel. Je kunt je eigen gids later offline openen.", color = Muted, fontSize = if (largeText) 16.sp else 14.sp)
        Button(
            onClick = {
                onSave(Guide(
                    id = "custom-${UUID.randomUUID()}",
                    title = title.trim(),
                    summary = summary.trim().ifBlank { "Een persoonlijke handleiding." },
                    category = category,
                    minutes = minutesText.toIntOrNull()?.coerceIn(1, 600) ?: 10,
                    tags = listOf("mijn gids", "persoonlijk", category.lowercase(Locale.ROOT)),
                    steps = parsedSteps
                ))
            },
            enabled = title.isNotBlank() && parsedSteps.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Teal)
        ) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Handleiding opslaan")
        }
        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Annuleren") }
    }
}

@Composable
private fun SettingsScreen(largeText: Boolean, onLargeTextChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Instellingen", fontSize = if (largeText) 28.sp else 24.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("Maak KlaarKompas passend bij jouw manier van werken.", color = Muted, fontSize = if (largeText) 17.sp else 14.sp)
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = null, tint = Teal, modifier = Modifier.size(28.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Grotere tekst", fontWeight = FontWeight.SemiBold, fontSize = if (largeText) 19.sp else 16.sp)
                    Text("Gebruik ruimere tekst in de handleidingen en overzichten.", color = Muted, fontSize = if (largeText) 16.sp else 13.sp)
                }
                Switch(checked = largeText, onCheckedChange = onLargeTextChange, modifier = Modifier.semantics { contentDescription = "Grotere tekst inschakelen" })
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Offline en privacy", fontWeight = FontWeight.SemiBold, fontSize = if (largeText) 19.sp else 16.sp)
                Text("Gidsen, favorieten, voortgang en eigen gidsen worden lokaal bewaard. Deze versie heeft geen account of eigen synchronisatie. Bij voorlezen gebruikt Android de tekst-naar-spraakdienst die op jouw toestel is ingesteld.", color = Muted, fontSize = if (largeText) 16.sp else 14.sp)
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PrivacyPolicyUrl))
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.semantics { contentDescription = "Lees de privacyverklaring" }
                ) {
                    Text("Lees de privacyverklaring", fontSize = if (largeText) 16.sp else 14.sp)
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Mint), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Kleine stappen. Echte vooruitgang.", fontWeight = FontWeight.Bold, color = Ink, fontSize = if (largeText) 19.sp else 16.sp)
                Text("KlaarKompas is een startversie. Controleer belangrijke veiligheids-, juridische of financiële informatie altijd bij een passende, betrouwbare bron.", color = Ink, fontSize = if (largeText) 16.sp else 13.sp)
            }
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
