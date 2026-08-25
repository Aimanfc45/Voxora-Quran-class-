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
import com.example.data.model.ClassType
import com.example.data.model.Teacher
import com.example.data.repository.VoxoraRepository
import com.example.ui.components.CategoryPill
import com.example.ui.components.VoxoraHeaderBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDiscoveryScreen(
    repository: VoxoraRepository,
    selectedTeacherFromNav: Teacher? = null,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val teachers by repository.teachers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecialization by remember { mutableStateOf("All") }
    var activeTeacherProfile by remember { mutableStateOf<Teacher?>(selectedTeacherFromNav) }
    var bookingTeacher by remember { mutableStateOf<Teacher?>(null) }

    // Booking state
    var selectedSlot by remember { mutableStateOf("") }
    var bookingClassType by remember { mutableStateOf(ClassType.ONE_ON_ONE) }

    val specializations = listOf("All", "Tajwid", "Hafazan", "Qiraat", "Beginner Quran", "Children's Quran")

    val filteredTeachers = remember(teachers, searchQuery, selectedSpecialization) {
        teachers.filter { teacher ->
            val matchesQuery = searchQuery.isBlank() ||
                    teacher.name.contains(searchQuery, ignoreCase = true) ||
                    teacher.country.contains(searchQuery, ignoreCase = true) ||
                    teacher.languages.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesSpec = selectedSpecialization == "All" ||
                    teacher.specializations.contains(selectedSpecialization)

            matchesQuery && matchesSpec
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VoxoraHeaderBar(
                title = "Find Verified Teachers",
                subtitle = "Certified Quran instructors with Sanad & Ijazah"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("teacher_search_field"),
                placeholder = { Text("Search by name, country, or language...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald700)
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Specialization Filter Pills
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(specializations) { spec ->
                    CategoryPill(
                        label = spec,
                        isSelected = selectedSpecialization == spec,
                        onClick = { selectedSpecialization = spec }
                    )
                }
            }

            // Teachers List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredTeachers) { teacher ->
                    TeacherCard(
                        teacher = teacher,
                        onViewProfile = { activeTeacherProfile = teacher },
                        onBookClass = {
                            bookingTeacher = teacher
                            selectedSlot = teacher.availableSlots.firstOrNull() ?: "Today 08:00 PM"
                        }
                    )
                }
            }
        }
    }

    // Teacher Profile Bottom Sheet
    if (activeTeacherProfile != null) {
        val teacher = activeTeacherProfile!!
        ModalBottomSheet(
            onDismissRequest = { activeTeacherProfile = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (teacher.imageDrawableRes != null) {
                        Image(
                            painter = painterResource(id = teacher.imageDrawableRes),
                            contentDescription = teacher.name,
                            modifier = Modifier.size(64.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(64.dp).clip(CircleShape).background(Emerald700),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(teacher.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = teacher.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = teacher.flagEmoji, fontSize = 18.sp)
                        }
                        Text(
                            text = teacher.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Emerald700
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${teacher.rating} (${teacher.reviewsCount} reviews)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Emerald100,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${teacher.experienceYears}+ Yrs", fontWeight = FontWeight.Bold, color = Emerald900)
                            Text("Experience", style = MaterialTheme.typography.bodySmall, color = Emerald800)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldContainer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${teacher.activeStudents}", fontWeight = FontWeight.Bold, color = GoldOnContainer)
                            Text("Students", style = MaterialTheme.typography.bodySmall, color = GoldDark)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(teacher.hourlyRate, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Rate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("About Instructor", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(teacher.bio, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(12.dp))

                Text("Languages", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Text(teacher.languages.joinToString(", "), style = MaterialTheme.typography.bodySmall, color = Emerald700)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val t = activeTeacherProfile
                        activeTeacherProfile = null
                        bookingTeacher = t
                        selectedSlot = t?.availableSlots?.firstOrNull() ?: "Today 08:00 PM"
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Book Session with ${teacher.name.split(" ").firstOrNull()}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Booking Dialog
    if (bookingTeacher != null) {
        val teacher = bookingTeacher!!
        AlertDialog(
            onDismissRequest = { bookingTeacher = null },
            title = {
                Text(
                    text = "Book Session with ${teacher.name}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Emerald700
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select Session Type:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = bookingClassType == ClassType.ONE_ON_ONE,
                            onClick = { bookingClassType = ClassType.ONE_ON_ONE },
                            label = { Text("1-on-1 (45m)") }
                        )
                        FilterChip(
                            selected = bookingClassType == ClassType.GROUP,
                            onClick = { bookingClassType = ClassType.GROUP },
                            label = { Text("Group (60m)") }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Available Time Slots:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    teacher.availableSlots.forEach { slot ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedSlot = slot },
                            color = if (selectedSlot == slot) Emerald100 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (selectedSlot == slot) CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Emerald700)
                            ) else null
                        ) {
                            Text(
                                text = slot,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedSlot == slot) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSlot == slot) Emerald900 else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bookedClass = repository.bookClass(teacher, selectedSlot, bookingClassType)
                        onShowSnackbar("Successfully booked session with ${teacher.name} for $selectedSlot!")
                        bookingTeacher = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    modifier = Modifier.testTag("confirm_booking_button")
                ) {
                    Text("Confirm Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingTeacher = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TeacherCard(
    teacher: Teacher,
    onViewProfile: () -> Unit,
    onBookClass: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onViewProfile() }
            .testTag("teacher_card_${teacher.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (teacher.imageDrawableRes != null) {
                    Image(
                        painter = painterResource(id = teacher.imageDrawableRes),
                        contentDescription = teacher.name,
                        modifier = Modifier.size(56.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(Emerald700),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(teacher.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = teacher.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(text = teacher.flagEmoji, fontSize = 16.sp)
                    }
                    Text(
                        text = teacher.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald700
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format("%.2f", teacher.rating),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = " (${teacher.reviewsCount}) • ${teacher.experienceYears}y exp",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Emerald800,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("View Profile")
                }

                Button(
                    onClick = onBookClass,
                    modifier = Modifier.weight(1f).height(40.dp).testTag("teacher_book_btn_${teacher.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Book Class", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
