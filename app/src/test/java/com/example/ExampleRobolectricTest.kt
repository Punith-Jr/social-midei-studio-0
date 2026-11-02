package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Creator Studio", appName)
  }

  @Test
  fun `verify procedural scene image generates valid file for 9-16 format`() {
    kotlinx.coroutines.runBlocking {
      val context = ApplicationProvider.getApplicationContext<Context>()
      val scene = com.example.generator.StorySceneData(
        sceneNumber = 1,
        title = "Epic Discovery",
        characterDescription = "Astronaut in sleek suit",
        dialogue = "We found it!",
        voiceover = "An epic discovery made at the edge of the galaxy.",
        visualPrompt = "Space station glowing at twilight",
        durationSec = 5
      )
      val file = com.example.generator.StoryToVideoPipeline.generateProceduralSceneImage(
        context = context,
        scriptId = "test_script_1",
        scene = scene,
        format = com.example.generator.StoryVideoFormat.PORTRAIT_9_16,
        genre = com.example.generator.StoryGenre.EDUCATIONAL
      )
      org.junit.Assert.assertNotNull(file)
      org.junit.Assert.assertTrue(file.exists())
      org.junit.Assert.assertTrue(file.length() > 0)
    }
  }

  @Test
  fun `verify procedural scene image generates valid file for 16-9 format`() {
    kotlinx.coroutines.runBlocking {
      val context = ApplicationProvider.getApplicationContext<Context>()
      val scene = com.example.generator.StorySceneData(
        sceneNumber = 2,
        title = "High Stakes Conflict",
        characterDescription = "Hero facing the storm",
        dialogue = "Hold the line!",
        voiceover = "The confrontation began without warning.",
        visualPrompt = "Dramatic landscape with neon horizons",
        durationSec = 5
      )
      val file = com.example.generator.StoryToVideoPipeline.generateProceduralSceneImage(
        context = context,
        scriptId = "test_script_2",
        scene = scene,
        format = com.example.generator.StoryVideoFormat.LANDSCAPE_16_9,
        genre = com.example.generator.StoryGenre.ACTION
      )
      org.junit.Assert.assertNotNull(file)
      org.junit.Assert.assertTrue(file.exists())
      org.junit.Assert.assertTrue(file.length() > 0)
    }
  }

  @Test
  fun `verify StoryAssetStorageService stores and links scene assets`() {
    kotlinx.coroutines.runBlocking {
      val context = ApplicationProvider.getApplicationContext<Context>()
      val scriptId = "unit_test_proj"

      // 1. Save dummy bitmap for scene 1
      val testBitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)
      val savedImg = com.example.generator.StoryAssetStorageService.saveSceneImage(
        context = context,
        scriptId = scriptId,
        sceneNumber = 1,
        bitmap = testBitmap
      )
      org.junit.Assert.assertNotNull(savedImg)
      org.junit.Assert.assertTrue(savedImg.exists())

      val retrievedImg = com.example.generator.StoryAssetStorageService.getSceneImageFile(context, scriptId, 1)
      org.junit.Assert.assertNotNull(retrievedImg)
      assertEquals(savedImg.absolutePath, retrievedImg?.absolutePath)

      // 2. Save dummy audio bytes for scene 1
      val dummyWav = ByteArray(64) { 0 }
      val savedAudio = com.example.generator.StoryAssetStorageService.saveSceneAudioBytes(
        context = context,
        scriptId = scriptId,
        sceneNumber = 1,
        bytes = dummyWav
      )
      org.junit.Assert.assertNotNull(savedAudio)
      org.junit.Assert.assertTrue(savedAudio.exists())

      val retrievedAudio = com.example.generator.StoryAssetStorageService.getSceneAudioFile(context, scriptId, 1)
      org.junit.Assert.assertNotNull(retrievedAudio)
      assertEquals(savedAudio.absolutePath, retrievedAudio?.absolutePath)

      // 3. Test linkStoredAssetsToScript
      val script = com.example.generator.StoryScriptData(
        id = scriptId,
        title = "Storage Linked Project",
        genre = com.example.generator.StoryGenre.EDUCATIONAL,
        language = com.example.i18n.I18n.LANG_EN,
        targetDurationSec = 15,
        format = com.example.generator.StoryVideoFormat.PORTRAIT_9_16,
        characterAppearancePrompt = "Student in laboratory",
        introduction = "Welcome to science lab",
        scenes = listOf(
          com.example.generator.StorySceneData(
            sceneNumber = 1,
            title = "Intro Scene",
            characterDescription = "Student",
            dialogue = "Let's explore!",
            voiceover = "Today we discover chemistry.",
            visualPrompt = "Lab beakers bubbling",
            durationSec = 5
          )
        ),
        closingScene = "Subscribe for more experiments"
      )

      val linked = com.example.generator.StoryAssetStorageService.linkStoredAssetsToScript(context, script)
      assertEquals(com.example.generator.AssetStatus.READY, linked.scenes[0].imageStatus)
      assertEquals(savedImg.absolutePath, linked.scenes[0].imageFilePath)
      assertEquals(com.example.generator.AssetStatus.READY, linked.scenes[0].audioStatus)
      assertEquals(savedAudio.absolutePath, linked.scenes[0].audioFilePath)

      // 4. Test save and load project manifest
      val manifestFile = com.example.generator.StoryAssetStorageService.saveProjectManifest(context, linked)
      org.junit.Assert.assertTrue(manifestFile.exists())

      val loadedManifest = com.example.generator.StoryAssetStorageService.loadProjectManifest(context, scriptId)
      org.junit.Assert.assertNotNull(loadedManifest)
      assertEquals(scriptId, loadedManifest?.scriptId)
      assertEquals("Storage Linked Project", loadedManifest?.title)
      assertEquals(1, loadedManifest?.totalScenes)
      assertEquals(savedImg.absolutePath, loadedManifest?.scenes?.get(0)?.imagePath)

      // Clean up
      com.example.generator.StoryAssetStorageService.clearProjectAssets(context, scriptId)
    }
  }

  @Test
  fun `verify local auto-save persists project state and scenes to Room database`() {
    kotlinx.coroutines.runBlocking {
      val context = ApplicationProvider.getApplicationContext<Context>()
      val db = com.example.data.AppDatabase.getDatabase(context)

      val scenesJson = org.json.JSONArray().apply {
        put(org.json.JSONObject().apply {
          put("sceneNumber", 1)
          put("title", "Auto-Saved Scene 1")
          put("voiceover", "Narration text auto-saved successfully.")
          put("visualPrompt", "Cinematic 8k view")
          put("imageFilePath", "/data/user/0/com.example/files/scene_1.png")
          put("audioFilePath", "/data/user/0/com.example/files/scene_1.wav")
          put("imageStatus", "READY")
          put("audioStatus", "READY")
        })
      }.toString()

      val projectEntity = com.example.data.ContentProjectEntity(
        title = "Auto-Saved AI Story",
        topic = "AI Revolution",
        platform = "YouTube",
        format = "9:16 Shorts",
        status = "Production",
        scriptText = "Scene 1: Narration text auto-saved successfully.",
        hookText = "Narration text auto-saved successfully.",
        scenesJson = scenesJson,
        duration = "30s"
      )

      val insertedId = db.contentProjectDao().insertProject(projectEntity)
      org.junit.Assert.assertTrue(insertedId > 0)

      val retrieved = db.contentProjectDao().getProjectById(insertedId)
      org.junit.Assert.assertNotNull(retrieved)
      assertEquals("Auto-Saved AI Story", retrieved?.title)
      assertEquals("9:16 Shorts", retrieved?.format)
      org.junit.Assert.assertTrue(retrieved?.scenesJson?.contains("Auto-Saved Scene 1") == true)
      org.junit.Assert.assertTrue(retrieved?.scenesJson?.contains("READY") == true)
    }
  }
}




