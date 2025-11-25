package com.example.teamproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackingScreen(
    drinkViewModel: DrinkViewModel = viewModel()
) {
    var waterRecords by remember { mutableStateOf(listOf<WaterRecord>()) }
    var showDialog by remember { mutableStateOf(false) }
    var amountInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }

    // Observe drink records state
    val drinkRecordsState by drinkViewModel.drinkRecordsState.collectAsState()
    val createDrinkRecordState by drinkViewModel.createDrinkRecordState.collectAsState()

    // Get lifecycle owner to observe lifecycle events
    val lifecycleOwner = LocalLifecycleOwner.current

    // Load today's records when screen resumes (becomes visible)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                drinkViewModel.loadTodayRecords()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                // Reload today's records to show the new record
                drinkViewModel.loadTodayRecords()
                drinkViewModel.resetCreateDrinkRecordState()
            }
            is CreateDrinkRecordUiState.Error -> {
                // Error handling can be added here (e.g., show snackbar)
            }
            else -> {}
        }
    }

    // 오늘 마신 물의 총량 계산
    val todayRecords = waterRecords.filter {
        it.getFormattedDate() == LocalDate.now().toString()
    }
    val todayTotal = todayRecords.sumOf { it.amount }
    val recommendedAmount = 2000  // 임시로 2000ml 설정

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "물 마시기 기록",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 오늘의 진행 상황
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
                    text = "오늘 마신 물",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$todayTotal ml",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LinearProgressIndicator(
                    progress = { (todayTotal.toFloat() / recommendedAmount).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(8.dp),
                )
                Text(
                    text = "목표: $recommendedAmount ml",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // 빠른 추가 버튼들
        Text(
            text = "빠른 추가",
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

        // 기록 목록
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "오늘의 기록",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "물 기록 추가")
            }
        }

        if (todayRecords.isEmpty()) {
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
                        text = "아직 오늘의 기록이 없습니다",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todayRecords.sortedByDescending { it.timestamp }) { record ->
                    WaterRecordItem(
                        record = record,
                        onDelete = {
                            waterRecords = waterRecords.filter { it.id != record.id }
                        }
                    )
                }
            }
        }
    }

    // 물 기록 추가 다이얼로그
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("물 마신 기록 추가") },
            text = {
                Column {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("양 (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("메모 (선택사항)") },
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
                    Text("추가")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
fun WaterRecordItem(
    record: WaterRecord,
    onDelete: () -> Unit
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
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "기록 삭제",
                    tint = MaterialTheme.colorScheme.error
                )
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
