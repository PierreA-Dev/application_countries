package com.countries.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.countries.data.CountriesRepository
import com.countries.domain.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CountriesViewModel(
    private val repo: CountriesRepository = CountriesRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Country>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Country>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            runCatching { repo.getCountries() }
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Erreur inconnue") }
        }
    }
}
