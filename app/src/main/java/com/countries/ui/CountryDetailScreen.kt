package com.countries.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.countries.domain.Country
import kotlinx.coroutines.flow.StateFlow

/**
 * CountryDetailScreen - affiche les informations d'un pays identifié
 */
@Composable
fun CountryDetailScreen(
    countryCode: String,
    vm: CountriesViewModel = viewModel()
) {
    // Expose le state du ViewModel
    val state by vm.state.collectAsState()

    // Lancer une charge du détail uniquement si nécessaire.
    LaunchedEffect(countryCode) {
    }

    // Récupérer le pays depuis le cache
    val cachedCountry: Country? = when (state) {
        is UiState.Success<*> -> {
            val list = (state as UiState.Success<List<Country>>).data
            list.find { it.code.equals(countryCode, ignoreCase = true) }
        }
        else -> null
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            // Récupérer le pays depuis le cache si disponible
            cachedCountry != null -> CountryDetailContent(country = cachedCountry)

            // Spinner Chargement
            state is UiState.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state is UiState.Error -> {
                val msg = (state as UiState.Error).message
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Erreur: $msg")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { vm.load() }) {
                        Text("Réessayer")
                    }
                }
            }

            else -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Détails introuvables pour le pays : $countryCode")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        vm.load()
                    }) {
                        Text("Réessayer")
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryDetailContent(country: Country) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Drapeau + Nom
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = country.emoji,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = country.name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Détails
        InfoRow(label = "Code", value = country.code)
        InfoRow(label = "Capitale", value = country.capital ?: "—")
        InfoRow(label = "Continent", value = country.continent ?: "—")
        InfoRow(label = "Monnaie", value = country.currency ?: "—")
        InfoRow(label = "Langue(s)",     value = country.languages.joinToString().ifEmpty { "—" })
        // Ajoute d'autres champs selon ton modèle (population, area, etc.)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}