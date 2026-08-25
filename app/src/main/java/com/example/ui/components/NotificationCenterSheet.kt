package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import com.example.data.repository.VoxoraRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterSheet(
    repository: VoxoraRepository,
    onDismiss: () -> Unit,
    onNavigateToClass: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val notifications by repository.notifications.collectAsState()
    val unreadCount by repository.unreadNotificationsCount.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = GoldPrimary,
                            contentColor = Emerald900
                        ) {
                            Text(
                                text = "$unreadCount new",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (unreadCount > 0) {
                    TextButton(
                        onClick = {
                            repository.markAllNotificationsAsRead()
                            onShowSnackbar("Marked all as read")
                        },
                        modifier = Modifier.testTag("mark_all_read_button")
                    ) {
                        Text(
                            text = "Mark all read",
                            style = MaterialTheme.typography.labelMedium,
                            color = Emerald700,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No notifications right now",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        NotificationItemCard(
                            notification = notif,
                            onItemClick = {
                                repository.markNotificationAsRead(notif.id)
                                when (notif.type) {
                                    NotificationType.UPCOMING_CLASS -> {
                                        onDismiss()
                                        onNavigateToClass()
                                    }
                                    NotificationType.TEACHER_MESSAGE -> {
                                        onDismiss()
                                        onNavigateToQuran()
                                    }
                                    NotificationType.GROUP_ACTIVITY -> {
                                        onDismiss()
                                        onNavigateToCommunity()
                                    }
                                    NotificationType.CLASS_REMINDER -> {
                                        onDismiss()
                                        onNavigateToQuran()
                                    }
                                    NotificationType.ACHIEVEMENT_UNLOCKED -> {
                                        onDismiss()
                                        onNavigateToProgress()
                                    }
                                }
                            },
                            onDismissNotification = {
                                repository.clearNotification(notif.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    notification: AppNotification,
    onItemClick: () -> Unit,
    onDismissNotification: () -> Unit
) {
    val icon: ImageVector = when (notification.type) {
        NotificationType.UPCOMING_CLASS -> Icons.Default.VideoCameraFront
        NotificationType.TEACHER_MESSAGE -> Icons.Default.RecordVoiceOver
        NotificationType.GROUP_ACTIVITY -> Icons.Default.Groups
        NotificationType.CLASS_REMINDER -> Icons.Default.MenuBook
        NotificationType.ACHIEVEMENT_UNLOCKED -> Icons.Default.EmojiEvents
    }

    val iconContainerColor: Color = when (notification.type) {
        NotificationType.UPCOMING_CLASS -> Color(0xFFFEE2E2)
        NotificationType.TEACHER_MESSAGE -> Emerald100
        NotificationType.GROUP_ACTIVITY -> Color(0xFFE0F2FE)
        NotificationType.CLASS_REMINDER -> GoldContainer
        NotificationType.ACHIEVEMENT_UNLOCKED -> Color(0xFFFEF3C7)
    }

    val iconTintColor: Color = when (notification.type) {
        NotificationType.UPCOMING_CLASS -> Color(0xFFDC2626)
        NotificationType.TEACHER_MESSAGE -> Emerald800
        NotificationType.GROUP_ACTIVITY -> Color(0xFF0284C7)
        NotificationType.CLASS_REMINDER -> GoldOnContainer
        NotificationType.ACHIEVEMENT_UNLOCKED -> Color(0xFFB45309)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .testTag("notification_item_${notification.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) Emerald50.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = if (!notification.isRead) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald300)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = notification.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (notification.actionLabel != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onItemClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Emerald700,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = notification.actionLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            IconButton(
                onClick = onDismissNotification,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
