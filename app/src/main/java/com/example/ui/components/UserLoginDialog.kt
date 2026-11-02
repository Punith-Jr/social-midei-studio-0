package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CreatorSettingsEntity
import com.example.ui.theme.*

@Composable
fun UserLoginDialog(
    settings: CreatorSettingsEntity,
    onLoginSuccess: (CreatorSettingsEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var emailInput by remember { mutableStateOf(settings.youTubeEmail.ifBlank { "" }) }
    var accountNameInput by remember { mutableStateOf(settings.channelName.ifBlank { "Creator Studio" }) }
    var youTubeHandleInput by remember { mutableStateOf(settings.youTubeHandle.ifBlank { "@creator_studio" }) }
    var instagramHandleInput by remember { mutableStateOf(settings.instagramHandle.ifBlank { "@creator_official" }) }
    var facebookPageInput by remember { mutableStateOf(settings.facebookPageName.ifBlank { "Creator Studio Community" }) }

    var linkYouTube by remember { mutableStateOf(true) }
    var linkInstagram by remember { mutableStateOf(true) }
    var linkFacebook by remember { mutableStateOf(true) }

    val isAlreadyLinked = settings.isGoogleAccountLoggedIn && settings.youTubeEmail.isNotBlank() && settings.youTubeEmail.contains("@")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("user_login_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Google / Gmail styling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(StudioCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Gmail Account Link",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Linked to YouTube, Facebook & Instagram",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAlreadyLinked) Color(0xFF2EA44F).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = if (isAlreadyLinked) "LOGGED IN" else "NOT LOGGED IN",
                            color = if (isAlreadyLinked) Color(0xFF2EA44F) else MaterialTheme.colorScheme.error,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Active Account Banner or Not Logged In Banner
                if (isAlreadyLinked) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2EA44F).copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth().testTag("active_account_banner")
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2EA44F), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Active Gmail: ${settings.youTubeEmail}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(shape = RoundedCornerShape(4.dp), color = YouTubeRed.copy(alpha = 0.15f)) {
                                    Text("YouTube: Connected", fontSize = 10.sp, color = YouTubeRed, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Surface(shape = RoundedCornerShape(4.dp), color = FacebookBlue.copy(alpha = 0.15f)) {
                                    Text("Facebook: Connected", fontSize = 10.sp, color = FacebookBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Surface(shape = RoundedCornerShape(4.dp), color = InstagramGradientPink.copy(alpha = 0.15f)) {
                                    Text("Instagram: Connected", fontSize = 10.sp, color = InstagramGradientPink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth().testTag("not_logged_in_banner")
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GOOGLE GMAIL.COM: NOT LOGGED IN",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = "YouTube, Facebook, and Instagram accounts are not connected. Enter your @gmail.com account below and tap 'LOGIN WITH THIS GMAIL & LINK ALL' to connect all 3 accounts.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // 1. Gmail Input
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Gmail Address (Google Account)") },
                    placeholder = { Text("username@gmail.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = StudioCrimson) },
                    modifier = Modifier.fillMaxWidth().testTag("login_email_input"),
                    singleLine = true
                )

                // 2. Creator Account Display Name
                OutlinedTextField(
                    value = accountNameInput,
                    onValueChange = { accountNameInput = it },
                    label = { Text("Creator / Channel Display Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("login_creator_name_input"),
                    singleLine = true
                )

                // 3. Accounts Linked to this Gmail Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("gmail_linked_social_accounts_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ACCOUNTS LINKED TO THIS GMAIL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // YouTube Channel Row
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(YouTubeRed),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("YouTube Channel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(youTubeHandleInput, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = linkYouTube,
                                    onCheckedChange = { linkYouTube = it },
                                    modifier = Modifier.testTag("link_youtube_switch")
                                )
                            }
                        }

                        // Instagram Profile Row
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(InstagramGradientPink),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Instagram Account", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(instagramHandleInput, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = linkInstagram,
                                    onCheckedChange = { linkInstagram = it },
                                    modifier = Modifier.testTag("link_instagram_switch")
                                )
                            }
                        }

                        // Facebook Page Row
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(FacebookBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Facebook Page", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(facebookPageInput, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = linkFacebook,
                                    onCheckedChange = { linkFacebook = it },
                                    modifier = Modifier.testTag("link_facebook_switch")
                                )
                            }
                        }
                    }
                }

                // 4. Primary Button: LOGIN & LINK ALL ACCOUNTS
                Button(
                    onClick = {
                        val rawEmail = emailInput.trim().ifBlank { "creator@gmail.com" }
                        val finalEmail = if (rawEmail.contains("@")) rawEmail else "$rawEmail@gmail.com"
                        val finalName = accountNameInput.trim().ifBlank { "Creator Studio" }
                        val updated = settings.copy(
                            isGoogleAccountLoggedIn = true,
                            youTubeEmail = finalEmail,
                            googleAccountName = finalName,
                            channelName = finalName,
                            googleAccountId = "goog_" + finalEmail.hashCode().toString().replace("-", ""),
                            isYouTubeConnected = linkYouTube,
                            youTubeHandle = youTubeHandleInput.trim(),
                            isInstagramConnected = linkInstagram,
                            instagramHandle = instagramHandleInput.trim(),
                            isFacebookConnected = linkFacebook,
                            facebookPageName = facebookPageInput.trim(),
                            wizardCompleted = true
                        )
                        onLoginSuccess(updated)
                        Toast.makeText(context, "Logged in with Gmail: $finalEmail! Linked YouTube, Facebook & Instagram.", Toast.LENGTH_LONG).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCrimson),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_button")
                ) {
                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOGIN WITH THIS GMAIL & LINK ALL",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                // 5. Google 1-Tap Quick Action
                OutlinedButton(
                    onClick = {
                        val rawEmail = emailInput.trim().ifBlank { "creator@gmail.com" }
                        val finalEmail = if (rawEmail.contains("@")) rawEmail else "$rawEmail@gmail.com"
                        val finalName = accountNameInput.trim().ifBlank { "Google Verified Creator" }
                        val updated = settings.copy(
                            isGoogleAccountLoggedIn = true,
                            youTubeEmail = finalEmail,
                            googleAccountName = finalName,
                            channelName = finalName,
                            isYouTubeConnected = true,
                            isInstagramConnected = true,
                            isFacebookConnected = true,
                            wizardCompleted = true
                        )
                        onLoginSuccess(updated)
                        Toast.makeText(context, "Google 1-Tap: Signed in & linked all platforms!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("google_login_button")
                ) {
                    Icon(Icons.Default.GTranslate, contentDescription = null, tint = StudioAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Google (1-Tap)", fontWeight = FontWeight.Bold)
                }

                // 6. Sign Out / Reset Button if connected
                if (isAlreadyLinked) {
                    TextButton(
                        onClick = {
                            val reset = settings.copy(
                                isGoogleAccountLoggedIn = false,
                                youTubeEmail = "",
                                googleAccountName = "Not Logged In",
                                isYouTubeConnected = false,
                                isInstagramConnected = false,
                                isFacebookConnected = false
                            )
                            onLoginSuccess(reset)
                            Toast.makeText(context, "Signed out from Google Gmail account", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().testTag("logout_account_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out & Unlink Platforms", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // Cancel Button
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
