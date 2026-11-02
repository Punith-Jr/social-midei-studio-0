package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarTaskEntity
import com.example.data.ContentProjectEntity
import com.example.data.CreatorSettingsEntity
import com.example.generator.ContentGenerator
import com.example.i18n.I18n
import com.example.ui.components.UploadPublishDialog
import com.example.ui.components.VideoGenerationProgressIndicator
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoCreatorScreen(
    currentLanguage: String,
    settings: CreatorSettingsEntity = CreatorSettingsEntity(),
    onSaveProject: (ContentProjectEntity) -> Unit,
    onPublishSuccess: (CalendarTaskEntity) -> Unit = {},
    onShowGlobalProgress: (title: String, subtitle: String, actionType: String, progress: Float?, onCancel: (() -> Unit)?) -> Unit = { _, _, _, _, _ -> },
    onHideGlobalProgress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val isKn = currentLanguage == I18n.LANG_KN

    var topic by remember { mutableStateOf("The Ultimate Creator Setup on a Budget") }
    var selectedStyle by remember { mutableStateOf("Cinematic") }
    var aspectRatio by remember { mutableStateOf("16:9 (YouTube)") }
    var duration by remember { mutableStateOf("10 Minutes") }
    var voiceStyle by remember { mutableStateOf("Deep & Storyteller") }
    var musicStyle by remember { mutableStateOf("Ambient Lo-Fi 95 BPM") }
    var showUploadDialog by remember { mutableStateOf(false) }

    var isGenerating by remember { mutableStateOf(false) }
    var progressStatusText by remember { mutableStateOf("") }
    var progressStepText by remember { mutableStateOf("") }

    var videoPlan by remember {
        mutableStateOf(
            ContentGenerator.generateTenMinuteVideoPlan(
                topic = topic,
                style = selectedStyle,
                language = currentLanguage
            )
        )
    }

    fun handleGenerateStandard() {
        scope.launch {
            isGenerating = true
            progressStatusText = "Generating video plan & storyboard..."
            progressStepText = "Step 1 of 2: Analyzing hooks & writing script"
            onShowGlobalProgress(
                "AI Video Engine API Request",
                "Analyzing audience hooks & generating video production plan...",
                "generate",
                0.3f
            ) {
                isGenerating = false
                onHideGlobalProgress()
            }
            delay(800)
            videoPlan = ContentGenerator.generateVideoPlan(
                topic = topic,
                style = selectedStyle,
                language = currentLanguage
            )
            isGenerating = false
            onHideGlobalProgress()
            Toast.makeText(context, "Video production plan generated!", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleGenerateTenMinute() {
        scope.launch {
            isGenerating = true
            duration = "10 Minutes"
            progressStatusText = "Generating 10-Minute Masterclass with 10 Scenes..."
            progressStepText = "Synthesizing AI visual cues, voiceovers & B-Roll"
            onShowGlobalProgress(
                "AI Video Masterclass API Request",
                "Synthesizing 10 scenes, AI visual cues, voiceovers & B-Roll...",
                "generate",
                0.45f
            ) {
                isGenerating = false
                onHideGlobalProgress()
            }
            delay(1000)
            videoPlan = ContentGenerator.generateTenMinuteVideoPlan(
                topic = topic,
                style = selectedStyle,
                language = currentLanguage
            )
            isGenerating = false
            onHideGlobalProgress()
            Toast.makeText(context, "Full 10-Minute Masterclass Generated with 10 Scenes!", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleSaveProject() {
        onSaveProject(
            ContentProjectEntity(
                title = videoPlan.title,
                topic = topic,
                platform = "YouTube",
                format = "Video 16:9",
                status = "Production",
                scriptText = videoPlan.scenes.joinToString("\n\n") { "Scene ${it.sceneNumber}: ${it.voiceover}" },
                hookText = videoPlan.scenes.firstOrNull()?.voiceover ?: "",
                ctaText = videoPlan.scenes.lastOrNull()?.voiceover ?: "",
                captionText = videoPlan.youtubeDescription,
                hashtagsText = videoPlan.hashtags,
                thumbnailTitle = topic.take(30),
                thumbnailStyle = selectedStyle,
                duration = duration
            )
        )
        Toast.makeText(context, "Project saved to Content Studio!", Toast.LENGTH_SHORT).show()
    }

    fun handleExportPackage() {
        val packageContent = """
========================================
CREATOR STUDIO PRODUCTION PACKAGE
Title: ${videoPlan.title}
Style: $selectedStyle | Ratio: $aspectRatio | Duration: $duration
Music Style: $musicStyle | Voice: $voiceStyle
========================================

--- SUMMARY ---
${videoPlan.ideaSummary}

--- SCENE BREAKDOWN ---
${videoPlan.scenes.joinToString("\n\n") { s ->
"""[SCENE ${s.sceneNumber}]
Visual: ${s.visual}
Voice-Over: ${s.voiceover}
On-Screen Text: ${s.onScreenText}
B-Roll Cues: ${s.bRoll}"""
}}

--- THUMBNAIL PROMPT ---
${videoPlan.thumbnailPrompt}

--- YOUTUBE DESCRIPTION & SEO ---
${videoPlan.youtubeDescription}

Tags: ${videoPlan.tags}
Hashtags: ${videoPlan.hashtags}
========================================
        """.trimIndent()

        clipboardManager.setText(AnnotatedString(packageContent))
        Toast.makeText(context, "Full production package copied! Ready to film/export", Toast.LENGTH_LONG).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = I18n.t("nav_create_video", currentLanguage),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isKn) "ದೀರ್ಘಾವಧಿ 16:9 ವೀಡಿಯೊ ಯೋಜನೆ, ದೃಶ್ಯ ವಿಭಜನೆ ಮತ್ತು B-Roll" else "16:9 Long-form video planner, scene-by-scene script & production package",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Circular progress indicator during video generation
        item {
            VideoGenerationProgressIndicator(
                isVisible = isGenerating,
                statusText = progressStatusText,
                stepText = progressStepText,
                actionType = "generate",
                onCancel = { isGenerating = false }
            )
        }

        // Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Video Topic / Core Subject") },
                        modifier = Modifier.fillMaxWidth().testTag("video_topic_input"),
                        singleLine = true
                    )

                    // Aspect Ratio
                    Text(text = "Aspect Ratio:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("16:9 (YouTube)", "9:16 (Shorts)", "1:1 (Square)").forEach { r ->
                            FilterChip(
                                selected = aspectRatio == r,
                                onClick = { aspectRatio = r },
                                label = { Text(r, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Style selector
                    Text(text = "Style / Genre:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "Cinematic", "Documentary", "Educational", "Motivational",
                            "News", "Storytelling", "Comedy", "Travel", "Business", "Gaming"
                        ).forEach { s ->
                            item {
                                FilterChip(
                                    selected = selectedStyle == s,
                                    onClick = { selectedStyle = s },
                                    label = { Text(s, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Audio & Voice Styles
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duration") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = voiceStyle,
                            onValueChange = { voiceStyle = it },
                            label = { Text("Voice Style") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { handleGenerateTenMinute() },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("generate_ten_minute_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("10-MIN AUTO VIDEO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { handleGenerateStandard() },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("generate_standard_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.MovieCreation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Quick Plan", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Generated Plan Output
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = videoPlan.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = videoPlan.ideaSummary,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    Text(
                        text = "🎬 SCENE-BY-SCENE BREAKDOWN:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = StudioCrimson
                    )

                    videoPlan.scenes.forEach { scene ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Scene ${scene.sceneNumber}: [${scene.onScreenText}]",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudioPurple
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "👁 Visual: ${scene.visual}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "🎙 Voice-Over: \"${scene.voiceover}\"",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "🎞 B-Roll: ${scene.bRoll}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Media3 VideoPlayerView to preview generated content
                    Text(
                        text = "Interactive Video Player Preview (Media3 PlayerView)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    VideoPlayerView(
                        videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        title = videoPlan.title,
                        aspectRatio = 16f / 9f,
                        sceneMarkers = videoPlan.scenes.take(5).map { "S${it.sceneNumber}" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("creator_video_player")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { handleExportPackage() },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("download_package_button"),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Export", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { handleSaveProject() },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("save_video_project_button"),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Save", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { showUploadDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioPurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.3f).testTag("open_upload_dialog_button"),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("AUTO UPLOAD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showUploadDialog) {
        UploadPublishDialog(
            initialTitle = videoPlan.title,
            initialDescription = videoPlan.youtubeDescription,
            initialPlatform = "YouTube",
            settings = settings,
            onPublishSuccess = { task ->
                onPublishSuccess(task)
                showUploadDialog = false
            },
            onDismiss = { showUploadDialog = false }
        )
    }
}
