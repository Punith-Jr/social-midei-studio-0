package com.example

import com.example.generator.ContentGenerator
import com.example.i18n.I18n
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testContentGeneratorScriptCreation() {
        val script = ContentGenerator.generateScript(
            topic = "YouTube Editing Workflow",
            platform = "YouTube",
            language = I18n.LANG_EN,
            duration = "60s",
            tone = "Viral"
        )
        assertNotNull(script.openingHook)
        assertTrue(script.openingHook.isNotBlank())
        assertTrue(script.mainContent.isNotBlank())
        assertTrue(script.callToAction.isNotBlank())
    }

    @Test
    fun testHashtagGeneration() {
        val hashtags = ContentGenerator.generateHashtags("Creator", "YouTube")
        assertTrue(hashtags.isNotEmpty())
        assertTrue(hashtags.any { it.contains("creator", ignoreCase = true) })
    }

    @Test
    fun testTenMinuteVideoPlanGeneration() {
        val plan = ContentGenerator.generateTenMinuteVideoPlan(
            topic = "The 10 Minute Viral Masterclass",
            style = "Cinematic",
            language = I18n.LANG_EN
        )
        assertNotNull(plan)
        assertEquals(10, plan.scenes.size)
        assertTrue(plan.scenes.first().onScreenText.contains("00:00"))
        assertTrue(plan.scenes.last().onScreenText.contains("09:00"))
        assertTrue(plan.youtubeDescription.contains("TIMESTAMPS"))
    }

    @Test
    fun testGitHubAndVideoEditorTranslations() {
        assertEquals("Video Editor & AI", I18n.t("nav_video_editor", I18n.LANG_EN))
        assertEquals("GitHub Data Link", I18n.t("nav_github_link", I18n.LANG_EN))
        assertEquals("ವೀಡಿಯೊ ಸಂಪಾದಕ & AI", I18n.t("nav_video_editor", I18n.LANG_KN))
        assertEquals("ಗಿಟ್‌ಹಬ್ ಡೇಟಾ ಲಿಂಕ್", I18n.t("nav_github_link", I18n.LANG_KN))
    }

    @Test
    fun testCreatorSettingsDefaultGitHubAndGoogleFields() {
        val settings = com.example.data.CreatorSettingsEntity()
        assertEquals("", settings.youTubeEmail)
        assertEquals("Not Logged In", settings.googleAccountName)
        assertEquals("Anil-matcha", settings.gitHubUsername)
        assertEquals("AI-Youtube-Shorts-Generator", settings.gitHubRepo)
        assertEquals("main", settings.gitHubBranch)
    }

    @Test
    fun testVideoAspectRatioMapping() {
        val shorts = com.example.ui.components.VideoAspectRatio.fromFormatString("9:16 Shorts/Reels")
        assertEquals(com.example.ui.components.VideoAspectRatio.SHORTS_9_16, shorts)
        assertEquals(9f / 16f, shorts.ratio, 0.001f)
        assertEquals("1080 × 1920", shorts.standardResolution)

        val landscape = com.example.ui.components.VideoAspectRatio.fromFormatString("16:9 Long-Form")
        assertEquals(com.example.ui.components.VideoAspectRatio.LANDSCAPE_16_9, landscape)
        assertEquals(16f / 9f, landscape.ratio, 0.001f)
        assertEquals("1920 × 1080", landscape.standardResolution)

        val square = com.example.ui.components.VideoAspectRatio.fromFormatString("1:1 Square")
        assertEquals(com.example.ui.components.VideoAspectRatio.SQUARE_1_1, square)
        assertEquals(1.0f, square.ratio, 0.001f)

        val cinema = com.example.ui.components.VideoAspectRatio.fromFormatString("21:9 Cinema")
        assertEquals(com.example.ui.components.VideoAspectRatio.CINEMA_21_9, cinema)

        val social = com.example.ui.components.VideoAspectRatio.fromFormatString("4:5 Social")
        assertEquals(com.example.ui.components.VideoAspectRatio.PORTRAIT_4_5, social)
    }

    @Test
    fun testVideoAspectRatioFromRatio() {
        assertEquals(com.example.ui.components.VideoAspectRatio.SHORTS_9_16, com.example.ui.components.VideoAspectRatio.fromRatio(9f / 16f))
        assertEquals(com.example.ui.components.VideoAspectRatio.LANDSCAPE_16_9, com.example.ui.components.VideoAspectRatio.fromRatio(16f / 9f))
        assertEquals(com.example.ui.components.VideoAspectRatio.SQUARE_1_1, com.example.ui.components.VideoAspectRatio.fromRatio(1.0f))
    }

    @Test
    fun testGeminiApiServiceEffectiveApiKeyResolution() {
        // User key provided
        val customKey = "AIzaSyCustomKey12345"
        assertEquals(customKey, com.example.generator.GeminiApiService.getEffectiveApiKey(customKey))

        // Whitespace trimmed
        assertEquals(customKey, com.example.generator.GeminiApiService.getEffectiveApiKey("  $customKey  "))

        // Placeholder ignored
        val placeholder = "MY_GEMINI_API_KEY"
        val resolved = com.example.generator.GeminiApiService.getEffectiveApiKey(placeholder)
        assertNotEquals(placeholder, resolved)
    }

    @Test
    fun testStoryToVideoPipelineStructuredStoryEnglish() {
        val script = com.example.generator.StoryToVideoPipeline.generateStructuredStory(
            topic = "Exploring Deep Space",
            genre = com.example.generator.StoryGenre.EDUCATIONAL,
            language = I18n.LANG_EN,
            durationPreset = com.example.generator.StoryDurationPreset.SEC_15,
            format = com.example.generator.StoryVideoFormat.PORTRAIT_9_16
        )

        assertNotNull(script)
        assertTrue(script.title.contains("Deep Space", ignoreCase = true))
        assertEquals(3, script.scenes.size)
        assertEquals(15, script.targetDurationSec)
        assertTrue(script.characterAppearancePrompt.isNotBlank())
        assertTrue(script.introduction.isNotBlank())
        assertTrue(script.closingScene.isNotBlank())

        for (scene in script.scenes) {
            assertTrue(scene.title.isNotBlank())
            assertTrue(scene.voiceover.isNotBlank())
            assertTrue(scene.dialogue.isNotBlank())
            assertTrue(scene.visualPrompt.isNotBlank())
            assertTrue(scene.durationSec > 0)
        }
    }

    @Test
    fun testStoryToVideoPipelineStructuredStoryKannada() {
        val script = com.example.generator.StoryToVideoPipeline.generateStructuredStory(
            topic = "ಬಾಹ್ಯಾಕಾಶ ಯಾತ್ರೆ",
            genre = com.example.generator.StoryGenre.MOTIVATION,
            language = I18n.LANG_KN,
            durationPreset = com.example.generator.StoryDurationPreset.SEC_60,
            format = com.example.generator.StoryVideoFormat.LANDSCAPE_16_9
        )

        assertNotNull(script)
        assertEquals(I18n.LANG_KN, script.language)
        assertEquals(4, script.scenes.size)
        assertEquals(60, script.targetDurationSec)
        assertTrue(script.title.contains("ಬಾಹ್ಯಾಕಾಶ ಯಾತ್ರೆ"))
        assertTrue(script.scenes.first().voiceover.isNotBlank())
        assertTrue(script.scenes.first().dialogue.contains("!"))
    }

    @Test
    fun testFFmpegExportCommandGeneration() {
        val script = com.example.generator.StoryToVideoPipeline.generateStructuredStory(
            topic = "Tech Tutorial",
            genre = com.example.generator.StoryGenre.ACTION,
            language = I18n.LANG_EN,
            durationPreset = com.example.generator.StoryDurationPreset.SEC_30,
            format = com.example.generator.StoryVideoFormat.PORTRAIT_9_16
        )

        val cmd = com.example.generator.StoryToVideoPipeline.getFFmpegExportCommand(script, "final_render.mp4")
        assertTrue(cmd.startsWith("ffmpeg -y"))
        assertTrue(cmd.contains("-i scene_%02d.png"))
        assertTrue(cmd.contains("-i narration.wav"))
        assertTrue(cmd.contains("-c:v libx264"))
        assertTrue(cmd.contains("-t 30"))
        assertTrue(cmd.contains("\"final_render.mp4\""))
    }

    @Test
    fun testTaskStatusDashboardStateTransitions() {
        val stages = listOf(
            com.example.ui.components.PipelineStepId.STORY_GENERATION,
            com.example.ui.components.PipelineStepId.IMAGE_GENERATION,
            com.example.ui.components.PipelineStepId.NARRATION_AUDIO,
            com.example.ui.components.PipelineStepId.FFMPEG_RENDERING
        )
        assertEquals(4, stages.size)
        assertEquals(com.example.ui.components.PipelineStepId.STORY_GENERATION, stages[0])
        assertEquals(com.example.ui.components.PipelineStepId.IMAGE_GENERATION, stages[1])
        assertEquals(com.example.ui.components.PipelineStepId.NARRATION_AUDIO, stages[2])
        assertEquals(com.example.ui.components.PipelineStepId.FFMPEG_RENDERING, stages[3])

        val states = com.example.ui.components.PipelineStepState.values()
        assertTrue(states.contains(com.example.ui.components.PipelineStepState.PENDING))
        assertTrue(states.contains(com.example.ui.components.PipelineStepState.RUNNING))
        assertTrue(states.contains(com.example.ui.components.PipelineStepState.COMPLETED))
        assertTrue(states.contains(com.example.ui.components.PipelineStepState.FAILED))
    }

    @Test
    fun testFFmpegDiagnosticLogLevels() {
        val levels = com.example.ui.components.LogLevel.values()
        assertTrue(levels.contains(com.example.ui.components.LogLevel.ALL))
        assertTrue(levels.contains(com.example.ui.components.LogLevel.INFO))
        assertTrue(levels.contains(com.example.ui.components.LogLevel.WARN))
        assertTrue(levels.contains(com.example.ui.components.LogLevel.ERROR))

        val entry = com.example.ui.components.DiagnosticLogEntry(
            tag = "[RENDER]",
            level = com.example.ui.components.LogLevel.INFO,
            message = "Export completed successfully"
        )
        assertEquals(com.example.ui.components.LogLevel.INFO, entry.level)
        assertEquals("[RENDER]", entry.tag)
        assertEquals("Export completed successfully", entry.message)
    }

    @Test
    fun testStoryAssetStorageServiceFormatBytes() {
        assertEquals("0 B", com.example.generator.StoryAssetStorageService.formatBytes(0L))
        assertEquals("500 B", com.example.generator.StoryAssetStorageService.formatBytes(500L))
        assertEquals("1.0 KB", com.example.generator.StoryAssetStorageService.formatBytes(1024L))
        assertEquals("1.5 MB", com.example.generator.StoryAssetStorageService.formatBytes((1.5 * 1024 * 1024).toLong()))
    }

    @Test
    fun testStoryScriptDataModificationsBeforeImageGeneration() {
        val original = com.example.generator.StoryToVideoPipeline.generateStructuredStory(
            topic = "Origin Story",
            genre = com.example.generator.StoryGenre.MOTIVATION,
            language = I18n.LANG_EN,
            durationPreset = com.example.generator.StoryDurationPreset.SEC_15,
            format = com.example.generator.StoryVideoFormat.PORTRAIT_9_16
        )

        // Modify title, narration, dialogue, visual prompt
        val updatedTitle = "Custom Masterpiece Title"
        val updatedNarration = "This is the newly polished narration written by the user."
        val updatedPrompt = "Hyperrealistic portrait shot, volumetric lighting, 8k"

        val scenesCopy = original.scenes.map { it.copy() }.toMutableList()
        scenesCopy[0].title = "Custom Act 1"
        scenesCopy[0].voiceover = updatedNarration
        scenesCopy[0].visualPrompt = updatedPrompt
        scenesCopy[0].durationSec = 8

        val modifiedScript = original.copy(
            title = updatedTitle,
            characterAppearancePrompt = "Hero with radiant aura",
            scenes = scenesCopy,
            isApproved = true
        )

        assertEquals("Custom Masterpiece Title", modifiedScript.title)
        assertTrue(modifiedScript.isApproved)
        assertEquals("Custom Act 1", modifiedScript.scenes[0].title)
        assertEquals(updatedNarration, modifiedScript.scenes[0].voiceover)
        assertEquals(updatedPrompt, modifiedScript.scenes[0].visualPrompt)
        assertEquals(8, modifiedScript.scenes[0].durationSec)
    }

    @Test
    fun testPromptSuggestionEngineEnglishNarration() {
        val prompt = com.example.generator.PromptSuggestionEngine.suggestImagePrompt(
            narration = "The spaceship blasted past the asteroid belt into deep space.",
            sceneTitle = "Space Odyssey",
            genre = com.example.generator.StoryGenre.EDUCATIONAL,
            characterPrompt = "Astronaut in sleek suit",
            language = I18n.LANG_EN
        )
        assertNotNull(prompt)
        assertTrue(prompt.contains("Astronaut in sleek suit"))
        assertTrue(prompt.contains("8k"))
        assertTrue(prompt.contains("photorealistic"))
        assertTrue(prompt.contains("Wide-angle cosmic panoramic shot"))
    }

    @Test
    fun testPromptSuggestionEngineKannadaNarration() {
        val prompt = com.example.generator.PromptSuggestionEngine.suggestImagePrompt(
            narration = "ಕಠಿಣ ಪರಿಶ್ರಮ ಮತ್ತು ಛಲದಿಂದ ಅಂತಿಮವಾಗಿ ಮಹತ್ತರ ಗೆಲುವು ಸಾಧಿಸಲಾಯಿತು.",
            sceneTitle = "ಅಂತಿಮ ವಿಜಯ",
            genre = com.example.generator.StoryGenre.MOTIVATION,
            characterPrompt = "ಯುವ ಸಾಹಸಿ",
            language = I18n.LANG_KN
        )
        assertNotNull(prompt)
        assertTrue(prompt.contains("Heroic upward-angled golden hour shot"))
        assertTrue(prompt.contains("ಯುವ ಸಾಹಸಿ"))
        assertTrue(prompt.contains("8k"))
    }

    @Test
    fun testPromptSuggestionEngineSuggestAllPrompts() {
        val script = com.example.generator.StoryToVideoPipeline.generateStructuredStory(
            topic = "Cyber Security",
            genre = com.example.generator.StoryGenre.THRILLER,
            language = I18n.LANG_EN,
            durationPreset = com.example.generator.StoryDurationPreset.SEC_15,
            format = com.example.generator.StoryVideoFormat.PORTRAIT_9_16
        )

        val updated = com.example.generator.PromptSuggestionEngine.suggestAllPrompts(script)
        assertEquals(script.scenes.size, updated.scenes.size)
        for (scene in updated.scenes) {
            assertTrue(scene.visualPrompt.isNotBlank())
            assertTrue(scene.visualPrompt.contains("photorealistic 8k render"))
        }
    }

    @Test
    fun testSceneReorderAndRenumberingLogic() {
        val s1 = com.example.generator.StorySceneData(sceneNumber = 1, title = "First", characterDescription = "", dialogue = "", voiceover = "One", visualPrompt = "")
        val s2 = com.example.generator.StorySceneData(sceneNumber = 2, title = "Second", characterDescription = "", dialogue = "", voiceover = "Two", visualPrompt = "")
        val s3 = com.example.generator.StorySceneData(sceneNumber = 3, title = "Third", characterDescription = "", dialogue = "", voiceover = "Three", visualPrompt = "")

        val list = mutableListOf(s1, s2, s3)

        // Move s1 down to position 2
        val moved = list.removeAt(0)
        list.add(1, moved)

        // Re-index
        val reindexed = list.mapIndexed { idx, s -> s.copy(sceneNumber = idx + 1) }

        assertEquals("Second", reindexed[0].title)
        assertEquals(1, reindexed[0].sceneNumber)

        assertEquals("First", reindexed[1].title)
        assertEquals(2, reindexed[1].sceneNumber)

        assertEquals("Third", reindexed[2].title)
        assertEquals(3, reindexed[2].sceneNumber)
    }

    @Test
    fun testAssetStatusEnumValues() {
        val statuses = com.example.generator.AssetStatus.values()
        assertTrue(statuses.contains(com.example.generator.AssetStatus.PENDING))
        assertTrue(statuses.contains(com.example.generator.AssetStatus.GENERATING))
        assertTrue(statuses.contains(com.example.generator.AssetStatus.READY))
        assertTrue(statuses.contains(com.example.generator.AssetStatus.FAILED))
    }

    @Test
    fun testTenVideoCreationCategories() {
        val categories = com.example.generator.VideoCreationCategory.values()
        assertEquals(10, categories.size)

        val ids = categories.map { it.id }
        assertTrue(ids.contains("manga"))
        assertTrue(ids.contains("youtube_long"))
        assertTrue(ids.contains("facebook"))
        assertTrue(ids.contains("instagram"))
        assertTrue(ids.contains("shorts"))
        assertTrue(ids.contains("reels"))
        assertTrue(ids.contains("cinematic"))
        assertTrue(ids.contains("photo_to_video"))
        assertTrue(ids.contains("comic"))
        assertTrue(ids.contains("custom"))

        for (cat in categories) {
            assertTrue(cat.titleEn.isNotBlank())
            assertTrue(cat.titleKn.isNotBlank())
            assertTrue(cat.iconEmoji.isNotBlank())
            assertNotNull(cat.defaultAspect)
        }
    }

    @Test
    fun testSubtitleGeneratorServiceSrtAndVttFormatting() {
        val scenes = listOf(
            com.example.generator.StorySceneData(
                sceneNumber = 1,
                title = "Scene 1",
                voiceover = "First scene narration line in Kannada and English",
                durationSec = 5
            ),
            com.example.generator.StorySceneData(
                sceneNumber = 2,
                title = "Scene 2",
                voiceover = "Second scene narration line with full subtitle timing",
                durationSec = 6
            )
        )

        val entries = com.example.generator.SubtitleGeneratorService.generateSubtitleEntries(scenes)
        assertTrue(entries.isNotEmpty())
        assertEquals(0L, entries[0].startMs)
        assertEquals(5000L, entries[0].endMs)

        val srt = com.example.generator.SubtitleGeneratorService.generateSrtContent(entries)
        assertTrue(srt.contains("00:00:00,000 --> 00:00:05,000"))
        assertTrue(srt.contains("First scene narration line"))

        val vtt = com.example.generator.SubtitleGeneratorService.generateVttContent(entries)
        assertTrue(vtt.startsWith("WEBVTT"))
        assertTrue(vtt.contains("00:00:00.000 --> 00:00:05.000"))
    }

    @Test
    fun testAudioAndSfxEngineLibrary() {
        val bgmTracks = com.example.generator.AudioAndSfxEngine.BGM_LIBRARY
        assertTrue(bgmTracks.size >= 6)
        assertTrue(bgmTracks.any { it.genre == "Cinematic" })
        assertTrue(bgmTracks.any { it.genre == "Emotional" })
        assertTrue(bgmTracks.any { it.genre == "Action" })

        val sfx = com.example.generator.AudioAndSfxEngine.SFX_LIBRARY
        assertTrue(sfx.size >= 6)
        assertTrue(sfx.any { it.id == "whoosh_fast" })
        assertTrue(sfx.any { it.id == "impact_boom" })
    }

    @Test
    fun testAllSeventeenScenePropertiesGenerated() {
        val script = com.example.generator.StoryToVideoPipeline.generateStructuredStory(
            topic = "Cyberpunk Neo Bengaluru 2077",
            genre = com.example.generator.StoryGenre.ACTION,
            language = I18n.LANG_EN,
            durationPreset = com.example.generator.StoryDurationPreset.SEC_15,
            format = com.example.generator.StoryVideoFormat.LANDSCAPE_16_9
        )

        for (scene in script.scenes) {
            assertTrue(scene.sceneNumber > 0)
            assertTrue(scene.title.isNotBlank())
            assertTrue(scene.description.isNotBlank())
            assertTrue(scene.charactersAppearing.isNotBlank())
            assertTrue(scene.locationBackground.isNotBlank())
            assertTrue(scene.characterActions.isNotBlank())
            assertTrue(scene.cameraAngle.isNotBlank())
            assertTrue(scene.cameraMovement.isNotBlank())
            assertTrue(scene.lightingAtmosphere.isNotBlank())
            assertTrue(scene.visualPrompt.isNotBlank())
            assertTrue(scene.negativePrompt.isNotBlank())
            assertTrue(scene.narration.isNotBlank())
            assertTrue(scene.dialogue.isNotBlank())
            assertTrue(scene.subtitle.isNotBlank())
            assertTrue(scene.durationSec > 0)
            assertTrue(scene.bgmSoundEffects.isNotBlank())
            assertEquals(com.example.generator.AssetStatus.PENDING, scene.imageStatus)
            assertEquals(com.example.generator.AssetStatus.PENDING, scene.videoStatus)
        }
    }
}




