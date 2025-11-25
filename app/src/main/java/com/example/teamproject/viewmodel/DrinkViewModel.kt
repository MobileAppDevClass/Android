package com.example.teamproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamproject.data.api.DrinkRecordsResponse
import com.example.teamproject.data.repository.DrinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for Drink Records
 */
sealed class DrinkRecordsUiState {
    object Idle : DrinkRecordsUiState()
    object Loading : DrinkRecordsUiState()
    data class Success(val data: DrinkRecordsResponse) : DrinkRecordsUiState()
    data class Error(val message: String) : DrinkRecordsUiState()
}

/**
 * ViewModel for drink record operations
 */
class DrinkViewModel(
    private val repository: DrinkRepository = DrinkRepository()
) : ViewModel() {

    private val _drinkRecordsState = MutableStateFlow<DrinkRecordsUiState>(DrinkRecordsUiState.Idle)
    val drinkRecordsState: StateFlow<DrinkRecordsUiState> = _drinkRecordsState.asStateFlow()

    /**
     * Load drink records with optional filters
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param startDate Start date in ISO format (optional)
     * @param endDate End date in ISO format (optional)
     * @param userId User ID (optional, defaults to current user)
     */
    fun loadDrinkRecords(
        page: Int = 0,
        size: Int = 20,
        startDate: String? = null,
        endDate: String? = null,
        userId: Long? = null
    ) {
        viewModelScope.launch {
            _drinkRecordsState.value = DrinkRecordsUiState.Loading

            val result = repository.getDrinkRecords(
                page = page,
                size = size,
                startDate = startDate,
                endDate = endDate,
                userId = userId
            )

            _drinkRecordsState.value = result.fold(
                onSuccess = { data ->
                    DrinkRecordsUiState.Success(data)
                },
                onFailure = { exception ->
                    DrinkRecordsUiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }

    /**
     * Load today's drink records
     */
    fun loadTodayRecords() {
        // When startDate and endDate are both null, API returns today's records
        loadDrinkRecords(
            page = 0,
            size = 100, // Get all today's records
            startDate = null,
            endDate = null
        )
    }

    /**
     * Reset drink records state to Idle
     */
    fun resetDrinkRecordsState() {
        _drinkRecordsState.value = DrinkRecordsUiState.Idle
    }
}
