package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class VideoExportConfig(
    val resolution: String = "1080p", // 720p, 1080p, 4K
    val includeWatermark: Boolean = false,
    val frameRate: Int = 60,
    val format: String = "MP4"
)

object VideoExportHelper {

    /**
     * Saves a video to the device MediaStore Movies folder without requiring dangerous runtime permissions on modern Android (API 29+).
     */
    suspend fun saveVideoToGallery(
        context: Context,
        videoUriOrUrl: String,
        videoTitle: String,
        config: VideoExportConfig
    ): Uri? = withContext(Dispatchers.IO) {
        val sanitizedTitle = videoTitle.replace(Regex("[^a-zA-Z0-9_]"), "_").take(30).ifBlank { "CreatorStudio_Export" }
        val filename = "${sanitizedTitle}_${config.resolution}_${System.currentTimeMillis()}.mp4"

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, filename)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/CreatorStudio")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val itemUri = resolver.insert(collection, contentValues) ?: return@withContext null

        try {
            if (!isVideoSource(context, videoUriOrUrl)) {
                throw IllegalArgumentException("Export source is not a supported video")
            }

            resolver.openOutputStream(itemUri)?.use { out ->
                if (videoUriOrUrl.startsWith("http://") || videoUriOrUrl.startsWith("https://")) {
                    URL(videoUriOrUrl).openStream().use { input ->
                        input.copyTo(out)
                    }
                } else if (videoUriOrUrl.startsWith("content://") || videoUriOrUrl.startsWith("file://")) {
                    resolver.openInputStream(Uri.parse(videoUriOrUrl))?.use { input ->
                        input.copyTo(out)
                    } ?: throw IllegalArgumentException("Could not read export source")
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                resolver.update(itemUri, contentValues, null, null)
            }
            itemUri
        } catch (e: Exception) {
            try {
                resolver.delete(itemUri, null, null)
            } catch (_: Exception) {}
            null
        }
    }

    private fun isVideoSource(context: Context, source: String): Boolean {
        if (source.startsWith("http://") || source.startsWith("https://")) {
            return source.substringBefore('?').lowercase().let { url ->
                VIDEO_EXTENSIONS.any { extension -> url.endsWith(extension) }
            }
        }

        val uri = Uri.parse(source)
        if (uri.scheme == "content") {
            return context.contentResolver.getType(uri)?.startsWith("video/") == true
        }

        if (uri.scheme == "file") {
            return VIDEO_EXTENSIONS.any { extension ->
                uri.path?.lowercase()?.endsWith(extension) == true
            }
        }

        return false
    }

    private val VIDEO_EXTENSIONS = setOf(".mp4", ".m4v", ".mov", ".mkv", ".webm")

    /**
     * Launches Android Share Sheet with the video.
     */
    fun shareVideo(context: Context, videoUri: Uri?, title: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                if (videoUri != null) {
                    type = "video/mp4"
                    putExtra(Intent.EXTRA_STREAM, videoUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Check out my video '$title' created with Creator Studio AI!")
                }
                putExtra(Intent.EXTRA_SUBJECT, title)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Video via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open share sheet: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
