package com.countries.ui

// Import des composants Compose
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Modèle de données Country
import com.countries.domain.Country

/**
 * CountryDetailScreen
 * Écran qui affiche les détails d’un pays à partir de son code
 */
@Composable
fun CountryDetailScreen(
    countryCode: String, // Code du pays reçu (navigation)
    vm: CountriesViewModel = viewModel() // ViewModel partagé
) {

    // On observe l'état exposé par le ViewModel
    val state by vm.state.collectAsState()

    // Effet déclenché quand le countryCode change
    // Vide car on utilise les données déjà chargées)
    LaunchedEffect(countryCode) {
    }

    // On essaie de récupérer le pays depuis la liste déjà chargée (cache)
    val cachedCountry: Country? = when (state) {

        // Si les données sont chargées
        is UiState.Success<*> -> {
            val list = (state as UiState.Success<List<Country>>).data

            // On cherche le pays correspondant au code
            list.find { it.code.equals(countryCode, ignoreCase = true) }
        }

        // Sinon on retourne null
        else -> null
    }

    // Surface = conteneur principal de l'écran
    Surface(modifier = Modifier.fillMaxSize()) {

        when {

            // Si on a trouvé le pays → afficher son contenu
            cachedCountry != null ->
                CountryDetailContent(country = cachedCountry)

            // ⏳ Si les données sont en cours de chargement
            state is UiState.Loading ->
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            // Si erreur
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

                    // Bouton pour relancer le chargement
                    Button(onClick = { vm.load() }) {
                        Text("Réessayer")
                    }
                }
            }

            // Cas où aucun pays trouvé
            else ->
                Box(
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

/**
 * Affiche le contenu détaillé d’un pays
 */
@Composable
private fun CountryDetailContent(country: Country) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Ligne contenant le drapeau et le nom du pays
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Emoji du pays
            Text(
                text = country.emoji,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Nom du pays
            Text(
                text = country.name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis // Coupe le texte trop long
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Informations détaillées
        InfoRow(label = "Code", value = country.code)
        InfoRow(label = "Capitale", value = country.capital ?: "—")
        InfoRow(label = "Continent", value = country.continent ?: "—")
        InfoRow(label = "Monnaie", value = country.currency ?: "—")

        // Jointure des langues en une seule chaîne
        InfoRow(
            label = "Langue(s)",
            value = country.languages.joinToString().ifEmpty { "—" }
        )
    }
}

/**
 * Afficher un label + une valeur
 */
@Composable
private fun InfoRow(label: String, value: String) {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        // Titre de la donnée
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )

        // Valeur correspondante
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}