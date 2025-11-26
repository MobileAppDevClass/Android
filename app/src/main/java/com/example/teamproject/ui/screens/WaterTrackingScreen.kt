package com.example.teamproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.teamproject.data.WaterRecord
import com.example.teamproject.data.api.DrinkRecord
import com.example.teamproject.viewmodel.CreateDrinkRecordUiState
import com.example.teamproject.viewmodel.DrinkRecordsUiState
import com.example.teamproject.viewmodel.DrinkViewModel
import com.example.teamproject.viewmodel.UserUiState
import com.example.teamproject.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackingScreen(
    drinkViewModel: DrinkViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    var waterRecords by remember { mutableStateOf(listOf<WaterRecord>()) }
    var showDialog by remember { mutableStateOf(false) }
    var amountInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Observe drink records state
    val drinkRecordsState by drinkViewModel.drinkRecordsState.collectAsState()
    val createDrinkRecordState by drinkViewModel.createDrinkRecordState.collectAsState()

    // Observe user state to get recommended water amount
    val userState by userViewModel.userState.collectAsState()

    // Get lifecycle owner to observe lifecycle events
    val lifecycleOwner = LocalLifecycleOwner.current

    // Load user profile on first composition
    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    // Function to load records for the selected date
    fun loadRecordsForDate(date: LocalDate) {
        val isToday = date == LocalDate.now()

        if (isToday) {
            // For today, don't send dates to get all today's records
            drinkViewModel.loadDrinkRecords(
                startDate = null,
                endDate = null
            )
        } else {
            // For other dates, send local datetime format (no timezone)
            val startOfDay = date.atStartOfDay()
            val endOfDay = date.atTime(23, 59, 59)

            // Format as local datetime: 2025-11-25T00:00:00
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

            drinkViewModel.loadDrinkRecords(
                startDate = formatter.format(startOfDay),
                endDate = formatter.format(endOfDay)
            )
        }
    }

    // Load records when screen resumes (becomes visible)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                loadRecordsForDate(selectedDate)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Load records when selected date changes
    LaunchedEffect(selectedDate) {
        loadRecordsForDate(selectedDate)
    }

    // Update waterRecords when API data is loaded
    LaunchedEffect(drinkRecordsState) {
        when (val state = drinkRecordsState) {
            is DrinkRecordsUiState.Success -> {
                // Convert API records to WaterRecord
                waterRecords = state.data.records.map { drinkRecord ->
                    convertDrinkRecordToWaterRecord(drinkRecord)
                }
            }
            else -> {}
        }
    }

    // Handle create drink record success
    LaunchedEffect(createDrinkRecordState) {
        when (createDrinkRecordState) {
            is CreateDrinkRecordUiState.Success -> {
                // Reload selected date's records to show the new record
                loadRecordsForDate(selectedDate)
                drinkViewModel.resetCreateDrinkRecordState()
            }
            is CreateDrinkRecordUiState.Error -> {
                // Error handling can be added here (e.g., show snackbar)
            }
            else -> {}
        }
    }

    // 선택된 날짜의 총 섭취량 계산
    val dailyTotal = waterRecords.sumOf { it.amount }

    // Get recommended amount from user profile, fallback to 2000ml if not available
    val recommendedAmount = if (userState is UserUiState.Success) {
        val user = (userState as UserUiState.Success).user
        user.profile?.recommendAmount ?: 2000
    } else {
        2000
    }

    val isToday = selectedDate == LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Water Intake Tracking",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 날짜 네비게이션
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 이전 날짜 버튼
                IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous day")
                }

                // 날짜 표시
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (!isToday) {
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // 다음 날짜 버튼 (오늘 이후는 비활성화)
                IconButton(
                    onClick = { selectedDate = selectedDate.plusDays(1) },
                    enabled = selectedDate < LocalDate.now()
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next day")
                }
            }

            // 오늘로 돌아가기 버튼 (오늘이 아닐 때만 표시)
            if (!isToday) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledTonalButton(
                        onClick = { selectedDate = LocalDate.now() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Today,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back to Today")
                    }
                }
            }
        }

        // 선택된 날짜의 진행 상황
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isToday) "Today's Water Intake" else "Water Intake",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$dailyTotal ml",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LinearProgressIndicator(
                    progress = { (dailyTotal.toFloat() / recommendedAmount).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(8.dp),
                )
                Text(
                    text = "Goal: $recommendedAmount ml",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // 빠른 추가 버튼들 (오늘만 표시)
        if (isToday) {
            Text(
                text = "Quick Add",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(100, 200, 300, 500).forEach { amount ->
                    FilledTonalButton(
                        onClick = {
                            // Call API to create drink record
                            drinkViewModel.createDrinkRecord(amount)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = createDrinkRecordState !is CreateDrinkRecordUiState.Loading
                    ) {
                        Text("${amount}ml")
                    }
                }
            }
        }

        // 기록 목록
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isToday) "Today's Records" else "Records",
                style = MaterialTheme.typography.titleMedium
            )
            // 추가 버튼은 오늘만 표시
            if (isToday) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add water record")
                }
            }
        }

        // Show loading or data based on state
        when (drinkRecordsState) {
            is DrinkRecordsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DrinkRecordsUiState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Unable to load records",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            else -> {
                if (waterRecords.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isToday) "No records yet for today" else "No records for this date",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(waterRecords.sortedByDescending { it.timestamp }) { record ->
                            WaterRecordItem(
                                record = record,
                                onDelete = {
                                    // 삭제는 오늘만 가능하도록 (API가 있다면 여기서 호출)
                                    waterRecords = waterRecords.filter { it.id != record.id }
                                },
                                showDelete = isToday
                            )
                        }
                    }
                }
            }
        }
    }

    // 물 기록 추가 다이얼로그
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Water Record") },
            text = {
                Column {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Note (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = amountInput.toIntOrNull()
                        if (amount != null && amount > 0) {
                            // Call API to create drink record
                            drinkViewModel.createDrinkRecord(amount)
                            amountInput = ""
                            noteInput = ""
                            showDialog = false
                        }
                    },
                    enabled = createDrinkRecordState !is CreateDrinkRecordUiState.Loading
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WaterRecordItem(
    record: WaterRecord,
    onDelete: () -> Unit,
    showDelete: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${record.amount} ml",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = record.getFormattedTime(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (record.note.isNotEmpty()) {
                    Text(
                        text = record.note,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            if (showDelete) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete record",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Convert DrinkRecord from API to WaterRecord for UI
 */
fun convertDrinkRecordToWaterRecord(drinkRecord: DrinkRecord): WaterRecord {
    // Parse ISO 8601 date string to LocalDateTime
    val timestamp = try {
        LocalDateTime.parse(drinkRecord.date, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        LocalDateTime.now()
    }

    return WaterRecord(
        id = drinkRecord.id.toString(),
        amount = drinkRecord.amount,
        timestamp = timestamp,
        note = ""
    )
}
