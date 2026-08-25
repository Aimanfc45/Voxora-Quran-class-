package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ClassType
import com.example.data.model.Teacher
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.ChoicePill
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDiscoveryScreen(
    repository: VoxoraRepository,
    onBookTeacherSuccess: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val teachers by repository.teachers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedTeacherDetail by remember { mutableStateOf<Teacher?>(null) }
    var bookingTeacher by remember { mutableStateOf<Teacher?>(null) }

    val filterOptions = listOf("All", "Tajwid", "Hafazan", "Qiraat", "Children's Quran", "Beginner Quran")

    val filteredTeachers = remember(teachers, searchQuery, selectedFilter) {
        teachers.filter { teacher ->
            val matchesQuery = searchQuery.isBlank() ||
                    teacher.name.contains(searchQuery, ignoreCase = true) ||
                    teacher.country.contains(searchQuery, ignoreCase = true) ||
                    teacher.specializations.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesFilter = selectedFilter == "All" || teacher.specializations.contains(selectedFilter)

            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("teacher_search_input"),
                placeholder = { Text("Search by Ustaz name, Tajwid, country...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald700)
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Filter Pills Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    ChoicePill(
                        label = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teachers Directory List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredTeachers, key = { it.id }) { teacher ->
                    TeacherCard(
                        teacher = teacher,
                        onViewProfile = { selectedTeacherDetail = teacher },
                        onBookSession = { bookingTeacher = teacher }
                    )
                }
            }
        }
    }

    // Teacher Profile Bottom Sheet
    if (selectedTeacherDetail != null) {
        TeacherProfileBottomSheet(
            teacher = selectedTeacherDetail!!,
            onDismiss = { selectedTeacherDetail = null },
            onBookNow = {
                val t = selectedTeacherDetail!!
                selectedTeacherDetail = null
                bookingTeacher = t
            }
        )
    }

    // Booking Dialog
    if (bookingTeacher != null) {
        BookTeacherSessionDialog(
            teacher = bookingTeacher!!,
            onConfirmBooking = { slot, type ->
                val t = bookingTeacher!!
                bookingTeacher = null
                repository.bookClassWithTeacher(t, slot, type)
                onShowSnackbar("Booked session with ${t.name} for $slot!")
                onBookTeacherSuccess()
            },
            onDismiss = { bookingTeacher = null }
        )
    }
}

@Composable
private fun TeacherCard(
    teacher: Teacher,
    onViewProfile: () -> Unit,
    onBookSession: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewProfile() }
            .testTag("teacher_card_${teacher.id}"),
        shape = RoundedCornerShape(18.dp),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                    contentDescription = teacher.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${teacher.name} ${teacher.flagEmoji}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = teacher.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald700
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${teacher.rating} (${teacher.reviewsCount} reviews)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${teacher.experienceYears} yrs exp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specialization tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                teacher.specializations.take(3).forEach { spec ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Emerald50
                    ) {
                        Text(
                            text = spec,
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald900,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = teacher.hourlyRate,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Emerald800
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewProfile,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Profile", style = MaterialTheme.typography.labelSmall, color = Emerald700)
                    }

                    Button(
                        onClick = onBookSession,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).testTag("book_session_btn_${teacher.id}")
                    ) {
                        Text("Book 1-on-1", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeacherProfileBottomSheet(
    teacher: Teacher,
    onDismiss: () -> Unit,
    onBookNow: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = teacher.imageDrawableRes ?: R.drawable.img_teacher_ahmad),
                    contentDescription = teacher.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "${teacher.name} ${teacher.flagEmoji}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = teacher.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Emerald700
                    )
                    Text(
                        text = "Languages: ${teacher.languages.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "About the Ustaz",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = teacher.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Specializations",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                teacher.specializations.forEach { spec ->
                    Surface(shape = RoundedCornerShape(6.dp), color = Emerald100) {
                        Text(spec, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Emerald900, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBookNow,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Select Slot & Book Session (${teacher.hourlyRate})", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BookTeacherSessionDialog(
    teacher: Teacher,
    onConfirmBooking: (slot: String, type: ClassType) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSlot by remember { mutableStateOf(teacher.availableSlots.firstOrNull() ?: "Tomorrow 10:00 AM") }
    var classType by remember { mutableStateOf(ClassType.ONE_ON_ONE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Book Session with ${teacher.name}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Select available time slot:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                teacher.availableSlots.forEach { slot ->
                    val isSelected = selectedSlot == slot
                    Surface(
                        onClick = { selectedSlot = slot },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Emerald100 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Emerald700)) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = slot,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Emerald900 else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Emerald700)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChoicePill(
                        label = "1-on-1 Focus ($teacher.hourlyRate)",
                        isSelected = classType == ClassType.ONE_ON_ONE,
                        onClick = { classType = ClassType.ONE_ON_ONE }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmBooking(selectedSlot, classType) },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
            ) {
                Text("Confirm Booking")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
