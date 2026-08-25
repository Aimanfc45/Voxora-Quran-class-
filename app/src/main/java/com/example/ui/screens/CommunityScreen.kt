package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommunityGroup
import com.example.data.model.CommunityPost
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.CategoryPill
import com.example.ui.components.VoxoraHeaderBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    repository: VoxoraRepository,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by repository.posts.collectAsState()
    val groups by repository.communityGroups.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Feed, 1: Circles & Groups
    var selectedGroupDetails by remember { mutableStateOf<CommunityGroup?>(null) }
    var activeCommentsPost by remember { mutableStateOf<CommunityPost?>(null) }
    var newCommentText by remember { mutableStateOf("") }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    // Moderation dialog state
    var showModerationDialog by remember { mutableStateOf(false) }
    var moderationTarget by remember { mutableStateOf<String?>(null) }

    // Create post form
    var newPostContent by remember { mutableStateOf("") }
    var newPostSurahRef by remember { mutableStateOf("") }
    var newPostGroup by remember { mutableStateOf("Global Quran Learners") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoxoraHeaderBar(
                title = "Quran Community",
                subtitle = "Share reflections, progress, & mutual testing"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePostDialog = true },
                containerColor = Emerald700,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("create_post_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Post", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Emerald700
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Global Feed", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Circles & Groups (${groups.size})", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(posts) { post ->
                            CommunityPostCard(
                                post = post,
                                onLike = { repository.toggleLikePost(post.id) },
                                onComment = { activeCommentsPost = post },
                                onShare = { onShowSnackbar("Post link copied to clipboard!") },
                                onModerate = {
                                    moderationTarget = post.authorName
                                    showModerationDialog = true
                                }
                            )
                        }
                    }
                }

                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(groups) { group ->
                            CommunityGroupCard(
                                group = group,
                                onToggleJoin = {
                                    val joined = repository.toggleGroupJoin(group.id)
                                    onShowSnackbar(if (joined) "Joined ${group.name}!" else "Left ${group.name}")
                                },
                                onClick = { selectedGroupDetails = group }
                            )
                        }
                    }
                }
            }
        }
    }

    // Comments Sheet
    if (activeCommentsPost != null) {
        val post = activeCommentsPost!!
        ModalBottomSheet(
            onDismissRequest = { activeCommentsPost = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Comments (${post.comments.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Emerald700
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (post.comments.isEmpty()) {
                        item {
                            Text(
                                text = "No comments yet. Be the first to share a thought!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(post.comments) { c ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = c.authorName,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (c.isTeacher) Emerald700 else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = c.timeAgo,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = c.text, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        modifier = Modifier.weight(1f).testTag("post_comment_input"),
                        placeholder = { Text("Write a comment or reflection...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                repository.addCommentToPost(post.id, newCommentText)
                                onShowSnackbar("Comment posted!")
                                newCommentText = ""
                                activeCommentsPost = repository.posts.value.find { it.id == post.id }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Emerald700)
                            .testTag("post_comment_send_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }

    // Group Details Sheet
    if (selectedGroupDetails != null) {
        val group = selectedGroupDetails!!
        ModalBottomSheet(
            onDismissRequest = { selectedGroupDetails = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(group.iconEmoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${group.memberCount} members • Category: ${group.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald700
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = group.description, style = MaterialTheme.typography.bodyMedium)

                if (group.announcements.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Announcements", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    group.announcements.forEach { ann ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Emerald50,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "📢 $ann",
                                style = MaterialTheme.typography.bodySmall.copy(color = Emerald900),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val joined = repository.toggleGroupJoin(group.id)
                        onShowSnackbar(if (joined) "Joined circle!" else "Left circle")
                        selectedGroupDetails = null
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (group.isJoined) Color(0xFFDC2626) else Emerald700
                    )
                ) {
                    Text(if (group.isJoined) "Leave Circle" else "Join Circle", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Create Post Dialog
    if (showCreatePostDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePostDialog = false },
            title = {
                Text("Create Community Post", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Emerald700)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newPostContent,
                        onValueChange = { newPostContent = it },
                        label = { Text("What did you learn today?") },
                        placeholder = { Text("Share reflections, Tajwid notes, or questions...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp).testTag("create_post_content_field"),
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPostSurahRef,
                        onValueChange = { newPostSurahRef = it },
                        label = { Text("Surah Reference (Optional)") },
                        placeholder = { Text("e.g., Surah Al-Fatihah (1:7)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPostContent.isNotBlank()) {
                            repository.createPost(newPostContent, newPostSurahRef, newPostGroup)
                            onShowSnackbar("Post shared with community!")
                            newPostContent = ""
                            newPostSurahRef = ""
                            showCreatePostDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    modifier = Modifier.testTag("submit_create_post_btn")
                ) {
                    Text("Publish Post")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePostDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Moderation Dialog (Report / Block / Mute)
    if (showModerationDialog) {
        AlertDialog(
            onDismissRequest = { showModerationDialog = false },
            title = { Text("Content & User Moderation") },
            text = {
                Column {
                    Text("Select an action for user ${moderationTarget ?: ""}:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            repository.reportContent(moderationTarget ?: "", "Inappropriate content")
                            onShowSnackbar("Report submitted. Our moderation team will review this post.")
                            showModerationDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Report Content")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            repository.muteUser(moderationTarget ?: "")
                            onShowSnackbar("User muted successfully.")
                            showModerationDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mute User")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModerationDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun CommunityPostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onModerate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (post.isTeacherPost) GoldPrimary else Emerald700),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1),
                            fontWeight = FontWeight.Bold,
                            color = if (post.isTeacherPost) Emerald900 else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            if (post.isTeacherPost) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = GoldContainer) {
                                    Text(
                                        "Teacher",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldOnContainer
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${post.timeAgo} • in ${post.groupName ?: "Community"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onModerate) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (post.surahReference != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Emerald50
                ) {
                    Text(
                        text = "📖 Ref: ${post.surahReference}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Emerald800
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLike,
                        modifier = Modifier.testTag("like_post_btn_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${post.likesCount}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onComment() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentsCount} comments",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommunityGroupCard(
    group: CommunityGroup,
    onToggleJoin: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(group.iconEmoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${group.memberCount} members • ${group.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            FilledTonalButton(
                onClick = onToggleJoin,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (group.isJoined) Emerald100 else Emerald700,
                    contentColor = if (group.isJoined) Emerald900 else Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (group.isJoined) "Joined" else "Join",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
