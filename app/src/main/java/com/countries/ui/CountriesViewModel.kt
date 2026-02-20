package com.countries.ui

// Import du ViewModel Android
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// Import du repository
import com.countries.data.CountriesRepository
import com.countries.domain.Country

// Import des outils Flow pour gérer l'état
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Import des coroutines
import kotlinx.coroutines.launch

// ViewModel qui gère les données de l'écran des pays
class CountriesViewModel(
    // Repository utilisé pour récupérer les pays
    private val repo: CountriesRepository = CountriesRepository()
) : ViewModel() {

    // État interne modifiable
    private val _state = MutableStateFlow<UiState<List<Country>>>(UiState.Loading)

    // État exposé en lecture seule pour l'UI
    val state: StateFlow<UiState<List<Country>>> = _state.asStateFlow()

    // Bloc exécuté à la création du ViewModel
    init {
        load() // On charge automatiquement les données
    }

    // Fonction pour charger la liste des pays
    fun load() {

        // On met l'état en "Loading" avant de commencer
        _state.value = UiState.Loading

        // On lance une coroutine liée au cycle de vie du ViewModel
        viewModelScope.launch {

            // runCatching permet de gérer succès/erreur proprement
            runCatching { repo.getCountries() }

                // Si succès → on met l'état à Success avec les données
                .onSuccess {
                    _state.value = UiState.Success(it)
                }

                // Si erreur → on met l'état à Error avec le message
                .onFailure {
                    _state.value = UiState.Error(
                        it.message ?: "Erreur inconnue"
                    )
                }
        }
    }
}