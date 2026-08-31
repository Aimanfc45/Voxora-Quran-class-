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
import com.example.ui.components.ChoicePill
import com.example.ui.components.GuestAccountBottomSheet
import com.example.ui.theme.*

enum class CommunityViewTab {
    FEED,
    STUDY_GROUPS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    repository: VoxoraRepository,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by repository.posts.collectAsState()
    val groups by repository.communityGroups.collectAsState()

    var selectedTab by remember { mutableStateOf(CommunityViewTab.FEED) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var showGuestAccountSheet by remember { mutableStateOf(false) }
    var activePostForComments by remember { mutableStateOf<CommunityPost?>(null) }
    var activePostForModeration by remember { mutableStateOf<CommunityPost?>(null) }
    val user by repository.userProfile.collectAsState()

    val categories = listOf("All", "Tajwid Tips", "Memorization", "Reflections", "Questions")

    val filteredPosts = remember(posts, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") posts
        else posts.filter {
            when (selectedCategoryFilter) {
                "Tajwid Tips" -> it.isTeacherPost || it.category == "Tajwid Tip"
                "Memorization" -> (it.groupName?.contains("Juz") == true) || (it.groupName?.contains("Hafazan") == true) || it.category == "Memorization"
                "Reflections" -> it.category == "Reflection"
                "Questions" -> it.category == "Question"
                else -> true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (selectedTab == CommunityViewTab.FEED) {
                FloatingActionButton(
                    onClick = {
                        if (user.isGuest) {
                            showGuestAccountSheet = true
                        } else {
                            showCreatePostDialog = true
                        }
                    },
                    containerColor = GoldPrimary,
                    contentColor = Emerald950,
                    modifier = Modifier.testTag("create_community_post_fab")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Create Post")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChoicePill(
                    label = "Community Feed",
                    isSelected = selectedTab == CommunityViewTab.FEED,
                    onClick = { selectedTab = CommunityViewTab.FEED }
                )
                ChoicePill(
                    label = "Study Circles & Groups (${groups.size})",
                    isSelected = selectedTab == CommunityViewTab.STUDY_GROUPS,
                    onClick = { selectedTab = CommunityViewTab.STUDY_GROUPS }
                )
            }

            when (selectedTab) {
                CommunityViewTab.FEED -> {
                    // Category Chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(categories) { cat ->
                            ChoicePill(
                                label = cat,
                                isSelected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Feed Posts List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredPosts, key = { it.id }) { post ->
                            CommunityPostCard(
                                post = post,
                                onLikeClick = { repository.toggleLikePost(post.id) },
                                onCommentsClick = { activePostForComments = post },
                                onMoreOptionsClick = { activePostForModeration = post },
                                onFollowAuthorClick = {
                                    repository.toggleFollowAuthor(post.authorName)
                                    onShowSnackbar(if (!post.isFollowingAuthor) "Following ${post.authorName}" else "Unfollowed ${post.authorName}")
                                }
                            )
                        }
                    }
                }

                CommunityViewTab.STUDY_GROUPS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Global Quran Study Groups",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Join dedicated groups for peer review, Juz targets, and live discussions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        items(groups, key = { it.id }) { group ->
                            CommunityGroupCard(
                                group = group,
                                onToggleJoin = {
                                    val joined = repository.toggleGroupJoin(group.id)
                                    onShowSnackbar(if (joined) "Joined ${group.name}!" else "Left ${group.name}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Post Dialog
    if (showCreatePostDialog) {
        CreatePostDialog(
            groups = groups,
            onSubmit = { content, surahRef, groupName, category ->
                repository.createPost(content, surahRef, groupName, category)
                showCreatePostDialog = false
                onShowSnackbar("Post shared with community!")
            },
            onDismiss = { showCreatePostDialog = false }
        )
    }

    if (showGuestAccountSheet) {
        GuestAccountBottomSheet(
            user = user,
            repository = repository,
            onDismiss = { showGuestAccountSheet = false },
            onSuccess = { onShowSnackbar(it) }
        )
    }

    // Comments Bottom Sheet
    if (activePostForComments != null) {
        val currentPost = posts.find { it.id == activePostForComments!!.id } ?: activePostForComments!!
        PostCommentsBottomSheet(
            post = currentPost,
            onDismiss = { activePostForComments = null },
            onAddComment = { text ->
                repository.addCommentToPost(currentPost.id, text)
                onShowSnackbar("Comment posted")
            }
        )
    }

    // Post Moderation Menu Dialog
    if (activePostForModeration != null) {
        val targetPost = activePostForModeration!!
        AlertDialog(
            onDismissRequest = { activePostForModeration = null },
            title = { Text("Post Options & Safety") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextButton(
                        onClick = {
                            repository.reportContent(targetPost.id, "Inappropriate Content")
                            activePostForModeration = null
                            onShowSnackbar("Post reported for review. JazakAllahu Khair.")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Report Inappropriate Content", color = Color(0xFFEF4444))
                    }

                    TextButton(
                        onClick = {
                            repository.muteUser(targetPost.authorName)
                            activePostForModeration = null
                            onShowSnackbar("Muted ${targetPost.authorName}")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.VolumeMute, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mute ${targetPost.authorName}", color = MaterialTheme.colorScheme.onSurface)
                    }

                    TextButton(
                        onClick = {
                            repository.blockUser(targetPost.authorName)
                            activePostForModeration = null
                            onShowSnackbar("Blocked ${targetPost.authorName}")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Block User", color = Color(0xFFEF4444))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { activePostForModeration = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun CommunityPostCard(
    post: CommunityPost,
    onLikeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    onFollowAuthorClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (post.isTeacherPost) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald300)) else CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (post.isTeacherPost) Emerald800 else Emerald600),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1),
                            color = if (post.isTeacherPost) GoldPrimary else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.isTeacherPost) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = GoldContainer) {
                                    Text("Ustaz", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldOnContainer, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                        Text(
                            text = "${post.groupName} • ${post.timeAgo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!post.isTeacherPost && post.authorRole != "Me") {
                        TextButton(
                            onClick = onFollowAuthorClick,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (post.isFollowingAuthor) "Following" else "+ Follow",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (post.isFollowingAuthor) MaterialTheme.colorScheme.onSurfaceVariant else Emerald700
                            )
                        }
                    }

                    IconButton(
                        onClick = onMoreOptionsClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Surah reference chip
            if (post.surahReference != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Emerald50,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Emerald800, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.surahReference,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Emerald900
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions row (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Like button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onLikeClick() }
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${post.likesCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Comments button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCommentsClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${post.commentsCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = post.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommunityGroupCard(
    group: CommunityGroup,
    onToggleJoin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("group_card_${group.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(group.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${group.memberCount} members • ${group.category}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onToggleJoin,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (group.isJoined) MaterialTheme.colorScheme.surfaceVariant else Emerald700,
                        contentColor = if (group.isJoined) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (group.isJoined) "Joined" else "+ Join",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (group.announcements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Emerald50
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Emerald800, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = group.announcements.first(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald900
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePostDialog(
    groups: List<CommunityGroup>,
    onSubmit: (content: String, surahRef: String?, groupName: String, category: String) -> Unit,
    onDismiss: () -> Unit
) {
    var content by remember { mutableStateOf("") }
    var surahRef by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf(groups.firstOrNull()?.name ?: "Global Quran Learners") }
    var selectedCategory by remember { mutableStateOf("Reflection") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Share with Community", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("What are you learning or reflecting on today?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = surahRef,
                    onValueChange = { surahRef = it },
                    placeholder = { Text("Optional Surah Ref (e.g. Surah Al-Mulk 67:1-5)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Post Category:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Reflection", "Tajwid Tip", "Question").forEach { cat ->
                        ChoicePill(
                            label = cat,
                            isSelected = selectedCategory == cat,
                            onClick = { selectedCategory = cat }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(content, surahRef, selectedGroup, selectedCategory) },
                enabled = content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostCommentsBottomSheet(
    post: CommunityPost,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Comments (${post.comments.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (post.comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Be the first to comment!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(post.comments) { c ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = c.authorName + if (c.isTeacher) " (Ustaz)" else "",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (c.isTeacher) GoldPrimary else Emerald800
                                    )
                                    Text(c.timeAgo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(c.text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Write a comment...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            onAddComment(replyText)
                            replyText = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Emerald700)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}
