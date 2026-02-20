package com.countries.ui

// Import des composants nécessaires pour Jetpack Compose
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.countries.domain.Country

// Composable principal qui affiche l'écran des pays
@Composable
fun CountriesScreen(
    onCountryClick: (String) -> Unit, // Fonction appelée quand on clique sur un pays
    vm: CountriesViewModel = viewModel()
) {
    // On observe l'état exposé par le ViewModel
    val state by vm.state.collectAsState()

    // On affiche un contenu différent selon l'état
    when (state) {

        // Si chargement, on affiche un indicateur circulaire
        UiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        // Si erreur, on affiche un message + bouton pour réessayer
        is UiState.Error -> {
            val msg = (state as UiState.Error).message
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Erreur: $msg")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { vm.load() }) {
                    Text("Réessayer")
                }
            }
        }

        // Si succès, on affiche la liste des pays
        is UiState.Success<*> -> {
            // On récupère la liste des pays
            val countries = (state as UiState.Success<List<Country>>).data

            // Liste scrollable verticale
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pour chaque pays dans la liste
                items(countries, key = { it.code }) { c ->
                    CountryRow(
                        country = c,
                        onClick = { onCountryClick(c.code) } // Action au clic
                    )
                }
            }
        }
    }
}

// Composable qui représente une ligne (une carte) pour un pays
@Composable
private fun CountryRow(
    country: Country,
    onClick: () -> Unit
) {
    // Carte cliquable
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        // Ligne horizontale contenant emoji + nom
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji du pays
            Text(
                country.emoji,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.width(12.dp))

            // Nom du pays
            Text(
                country.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}