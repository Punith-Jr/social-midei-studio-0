package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creator_settings")
data class CreatorSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val youtubeChannelUrl: String = "https://www.youtube.com",
    val youtubeStudioUrl: String = "https://studio.youtube.com",
    val instagramUrl: String = "https://www.instagram.com",
    val facebookUrl: String = "https://www.facebook.com",
    val channelName: String = "My Creator Channel",
    val defaultLanguage: String = "en",
    val theme: String = "dark",
    val wizardCompleted: Boolean = false,
    val youtubeSubscribers: Int = 12500,
    val instagramFollowers: Int = 8400,
    val facebookFollowers: Int = 5100,
    val monthlyViews: Long = 184500,
    val engagementRate: Float = 6.4f,
    val isGoogleAccountLoggedIn: Boolean = false,
    val isYouTubeConnected: Boolean = false,
    val youTubeEmail: String = "",
    val youTubeHandle: String = "@CreatorStudio",
    val googleAccountName: String = "Not Logged In",
    val googleAccountId: String = "",
    val isInstagramConnected: Boolean = false,
    val instagramHandle: String = "@creator_official",
    val isFacebookConnected: Boolean = false,
    val facebookPageName: String = "Creator Studio Community",
    // GitHub Data Link fields
    val isGitHubConnected: Boolean = true,
    val gitHubUsername: String = "Anil-matcha",
    val gitHubRepo: String = "AI-Youtube-Shorts-Generator",
    val gitHubBranch: String = "main",
    val gitHubToken: String = "",
    val gitHubLastSync: String = "Ready to Sync",
    val gitHubAvatarUrl: String = "https://avatars.githubusercontent.com/u/Anil-matcha",
    val gitHubBio: String = "AI-Youtube-Shorts-Generator: Automated text-to-shorts AI video generator pipeline with subtitles and audio (Anil-matcha/AI-Youtube-Shorts-Generator)",
    val gitHubPublicRepos: Int = 24,
    // AI Pipeline Credentials
    val geminiApiKey: String = "",
    val ffmpegPath: String = "builtin"
)

@Entity(tableName = "calendar_tasks")
data class CalendarTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val platform: String, // "YouTube", "Instagram", "Facebook", "All"
    val contentType: String, // "Video", "Short / Reel", "Post", "Story"
    val status: String = "Draft", // "Draft", "Scheduled", "Published"
    val scheduledDate: String, // YYYY-MM-DD
    val scheduledTime: String = "18:00",
    val notes: String = "",
    val projectId: Long? = null
)

@Entity(tableName = "content_projects")
data class ContentProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val topic: String,
    val platform: String = "YouTube",
    val format: String = "Video",
    val status: String = "Idea", // "Idea", "Script Ready", "Production", "Thumbnail Ready", "Ready to Publish", "Published"
    val scriptText: String = "",
    val hookText: String = "",
    val ctaText: String = "",
    val scenesJson: String = "",
    val captionText: String = "",
    val hashtagsText: String = "",
    val thumbnailTitle: String = "",
    val thumbnailStyle: String = "YouTube Viral",
    val thumbnailBadge: String = "NEW",
    val scheduledDate: String = "",
    val duration: String = "60s",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Videos", "Shorts", "Images", "Thumbnails", "Audio", "Scripts"
    val fileType: String,
    val uriOrPath: String,
    val sizeFormatted: String = "2.4 MB",
    val durationFormatted: String = "",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_scripts")
data class SavedScriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val topic: String,
    val platform: String,
    val language: String = "en",
    val duration: String = "60s",
    val tone: String = "Engaging",
    val hook: String = "",
    val bodyContent: String = "",
    val cta: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "auto_video_jobs")
data class AutoVideoJobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val topic: String,
    val platform: String = "YouTube", // "YouTube", "Instagram", "Facebook", "All"
    val status: String = "Uploaded", // "Generating", "Rendered", "Uploaded", "Scheduled", "Failed"
    val scriptText: String = "",
    val description: String = "",
    val visualPrompt1: String = "",
    val visualPrompt2: String = "",
    val visualPrompt3: String = "",
    val format: String = "9:16 Shorts", // "9:16 Shorts", "16:9 Long-Form"
    val durationSec: Int = 60,
    val uploadId: String = "",
    val uploadUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prompt_templates")
data class PromptTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Viral Shorts", "10-Min Video", "Hooks", "YouTube SEO", "Captions", "Kannada Culture", "Visual Prompts", "Tech & AI"
    val templateText: String,
    val sampleTopic: String = "",
    val platform: String = "All",
    val tags: String = "",
    val isFavorite: Boolean = false,
    val usageCount: Int = 0
)

@Entity(tableName = "video_metadata")
data class VideoMetadataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val topic: String = "",
    val format: String = "16:9 Long-Form", // "16:9 Long-Form", "9:16 Shorts", "1:1 Square"
    val resolution: String = "1080p", // "720p", "1080p", "4K"
    val fps: Int = 60,
    val durationSec: Int = 60,
    val fileUri: String = "",
    val thumbnailUri: String = "",
    val fileSizeBytes: Long = 0L,
    val tags: String = "",
    val platform: String = "YouTube", // "YouTube", "Instagram", "Facebook", "TikTok", "All"
    val draftStatus: String = "Draft", // "Draft", "Editing", "Rendering", "Ready", "Published", "Archived"
    val isDraft: Boolean = true,
    val completionProgress: Float = 0.25f, // 0.0 to 1.0 completion ratio
    val scenesJson: String = "", // serialized timeline scenes
    val audioTrackTitle: String = "Cinematic Ambient Beat",
    val voiceoverTone: String = "Energetic & Fast-Paced",
    val aspectRatio: String = "16:9",
    val bitRateKbps: Int = 12000,
    val videoCodec: String = "H.264 / AVC",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "manga_stories")
data class MangaStoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val japaneseTitle: String = "",
    val genre: String = "Action / Shonen", // "Action / Shonen", "Cyberpunk", "Dark Fantasy", "Supernatural", "Sci-Fi", "Mystery"
    val artStyle: String = "Modern Shonen", // "Modern Shonen", "Seinen Noir", "Webtoon Color", "Retro 90s Manga", "Chibi / Comedic"
    val synopsis: String,
    val coverImageUrl: String = "",
    val authorName: String = "Creator Studio AI",
    val status: String = "Ongoing", // "Draft", "Ongoing", "Completed"
    val totalChapters: Int = 1,
    val targetAudience: String = "Teens & Young Adults",
    val tags: String = "Manga, Anime, Epic, Superpowers, Weekly Jump",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "manga_chapters")
data class MangaChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storyId: Long,
    val chapterNumber: Int,
    val title: String,
    val summary: String,
    val contentScript: String = "", // dialogue, panels, sound effects
    val panelsJson: String = "", // serialized panels
    val audioUrl: String = "", // audio narration / voiceover track URL
    val videoUrl: String = "", // motion manga video overview URL
    val audioDurationSec: Int = 120,
    val videoDurationSec: Int = 60,
    val hasAudio: Boolean = false,
    val hasVideo: Boolean = false,
    val slideDeckJson: String = "", // 5-slide presentation deck
    val flashcardsJson: String = "", // interactive study/lore flashcards
    val infographicJson: String = "", // power scaling & timeline breakdown
    val reportText: String = "", // comprehensive narrative report / manuscript
    val status: String = "Draft", // "Draft", "Scripted", "Audio Ready", "Video Rendered", "Published"
    val downloadUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    val fileSizeBytes: Long = 48_500_000L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_github_repos")
data class CachedGitHubRepoEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val fullName: String,
    val description: String? = null,
    val htmlUrl: String,
    val language: String? = "Kotlin",
    val stargazersCount: Int = 0,
    val forksCount: Int = 0,
    val openIssuesCount: Int = 0,
    val defaultBranch: String = "main",
    val updatedAt: String? = null,
    val isPrivate: Boolean = false,
    val licenseName: String? = null,
    val licenseSpdxId: String? = null,
    val size: Int = 0,
    val cachedAt: Long = System.currentTimeMillis()
)



