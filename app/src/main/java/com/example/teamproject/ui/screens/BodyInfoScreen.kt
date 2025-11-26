package com.example.teamproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.teamproject.data.BodyInfo
import com.example.teamproject.data.api.ActivityLevel
import com.example.teamproject.data.api.Gender
import com.example.teamproject.viewmodel.UserProfileUiState
import com.example.teamproject.viewmodel.UserUiState
import com.example.teamproject.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyInfoScreen(
    userViewModel: UserViewModel = viewModel()
) {
    var bodyInfo by remember { mutableStateOf(BodyInfo()) }
    var isEditing by remember { mutableStateOf(false) }

    // 임시 입력 값들
    var heightInput by remember { mutableStateOf(bodyInfo.height.toString()) }
    var weightInput by remember { mutableStateOf(bodyInfo.weight.toString()) }
    var ageInput by remember { mutableStateOf(bodyInfo.age.toString()) }
    var selectedGender by remember { mutableStateOf(bodyInfo.gender) }
    var selectedActivity by remember { mutableStateOf(bodyInfo.activityLevel) }

    // Observe user state to get userId
    val userState by userViewModel.userState.collectAsState()
    val userProfileState by userViewModel.userProfileState.collectAsState()

    // Load user info on first composition
    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    // Load existing profile data when user info is loaded
    LaunchedEffect(userState) {
        if (userState is UserUiState.Success) {
            val user = (userState as UserUiState.Success).user
            user.profile?.let { profile ->
                bodyInfo = BodyInfo(
                    height = profile.height.toFloat(),
                    weight = profile.weight.toFloat(),
                    age = profile.age,
                    gender = when (profile.gender) {
                        Gender.MALE -> "남성"
                        Gender.FEMALE -> "여성"
                    },
                    activityLevel = when (profile.activityLevel) {
                        ActivityLevel.LOW -> "낮음"
                        ActivityLevel.MEDIUM -> "보통"
                        ActivityLevel.HIGH -> "높음"
                    }
                )
                // Update input fields as well
                heightInput = profile.height.toString()
                weightInput = profile.weight.toString()
                ageInput = profile.age.toString()
                selectedGender = when (profile.gender) {
                    Gender.MALE -> "남성"
                    Gender.FEMALE -> "여성"
                }
                selectedActivity = when (profile.activityLevel) {
                    ActivityLevel.LOW -> "낮음"
                    ActivityLevel.MEDIUM -> "보통"
                    ActivityLevel.HIGH -> "높음"
                }
            }
        }
    }

    // Handle profile creation success
    LaunchedEffect(userProfileState) {
        when (val state = userProfileState) {
            is UserProfileUiState.Success -> {
                // Update local bodyInfo with saved data
                bodyInfo = BodyInfo(
                    height = state.profile.height.toFloat(),
                    weight = state.profile.weight.toFloat(),
                    age = state.profile.age,
                    gender = when (state.profile.gender) {
                        Gender.MALE -> "남성"
                        Gender.FEMALE -> "여성"
                    },
                    activityLevel = when (state.profile.activityLevel) {
                        ActivityLevel.LOW -> "낮음"
                        ActivityLevel.MEDIUM -> "보통"
                        ActivityLevel.HIGH -> "높음"
                    }
                )
                isEditing = false
                // Reload user data to get updated profile
                userViewModel.loadCurrentUser()
                userViewModel.resetUserProfileState()
            }
            is UserProfileUiState.Error -> {
                // Error will be shown via Snackbar below
            }
            else -> {}
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Show error message
    LaunchedEffect(userProfileState) {
        if (userProfileState is UserProfileUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (userProfileState as UserProfileUiState.Error).message
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "신체 정보",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

        if (isEditing) {
            // 입력 모드
            OutlinedTextField(
                value = heightInput,
                onValueChange = { heightInput = it },
                label = { Text("키 (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = weightInput,
                onValueChange = { weightInput = it },
                label = { Text("몸무게 (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = ageInput,
                onValueChange = { ageInput = it },
                label = { Text("나이") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // 성별 선택
            Text(
                text = "성별",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGender == "남성",
                    onClick = { selectedGender = "남성" },
                    label = { Text("남성") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedGender == "여성",
                    onClick = { selectedGender = "여성" },
                    label = { Text("여성") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 활동량 선택
            Text(
                text = "활동량",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedActivity == "낮음",
                    onClick = { selectedActivity = "낮음" },
                    label = { Text("낮음") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedActivity == "보통",
                    onClick = { selectedActivity = "보통" },
                    label = { Text("보통") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedActivity == "높음",
                    onClick = { selectedActivity = "높음" },
                    label = { Text("높음") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 저장 버튼
            Button(
                onClick = {
                    val height = heightInput.toDoubleOrNull()
                    val weight = weightInput.toDoubleOrNull()
                    val age = ageInput.toIntOrNull()

                    if (height != null && weight != null && age != null && age > 0) {
                        // Get user from userState
                        if (userState is UserUiState.Success) {
                            val user = (userState as UserUiState.Success).user

                            // Convert Korean strings to API enums
                            val gender = when (selectedGender) {
                                "남성" -> Gender.MALE
                                "여성" -> Gender.FEMALE
                                else -> Gender.MALE
                            }

                            val activityLevel = when (selectedActivity) {
                                "낮음" -> ActivityLevel.LOW
                                "보통" -> ActivityLevel.MEDIUM
                                "높음" -> ActivityLevel.HIGH
                                else -> ActivityLevel.MEDIUM
                            }

                            // Check if profile already exists
                            if (user.profile != null) {
                                // Update existing profile
                                userViewModel.updateUserProfile(
                                    profileId = user.profile.id,
                                    age = age,
                                    gender = gender,
                                    height = height,
                                    weight = weight,
                                    activityLevel = activityLevel
                                )
                            } else {
                                // Create new profile
                                userViewModel.createUserProfile(
                                    userId = user.id,
                                    age = age,
                                    gender = gender,
                                    height = height,
                                    weight = weight,
                                    activityLevel = activityLevel
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = userProfileState !is UserProfileUiState.Loading
            ) {
                if (userProfileState is UserProfileUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("저장")
                }
            }
        } else {
            // 보기 모드
            if (bodyInfo.height > 0 || bodyInfo.weight > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow("키", "${bodyInfo.height} cm")
                        InfoRow("몸무게", "${bodyInfo.weight} kg")
                        InfoRow("나이", "${bodyInfo.age}세")
                        InfoRow("성별", bodyInfo.gender)
                        InfoRow("활동량", bodyInfo.activityLevel)

                        // Show recommended water intake if profile is available
                        if (userState is UserUiState.Success) {
                            val user = (userState as UserUiState.Success).user
                            user.profile?.let { profile ->
                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                Text(
                                    text = "오늘 마신 물",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "${profile.todayAmount} ml",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Text(
                                    text = "권장 일일 물 섭취량",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "${profile.recommendAmount} ml",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "아직 신체 정보가 입력되지 않았습니다",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Button(
                onClick = {
                    heightInput = if (bodyInfo.height > 0) bodyInfo.height.toString() else ""
                    weightInput = if (bodyInfo.weight > 0) bodyInfo.weight.toString() else ""
                    ageInput = if (bodyInfo.age > 0) bodyInfo.age.toString() else ""
                    selectedGender = bodyInfo.gender
                    selectedActivity = bodyInfo.activityLevel
                    isEditing = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (bodyInfo.height > 0 || bodyInfo.weight > 0) "수정" else "입력하기")
            }
        }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
