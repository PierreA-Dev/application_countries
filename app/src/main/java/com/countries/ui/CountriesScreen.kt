package com.countries.ui

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

@Composable
fun CountriesScreen(
    onCountryClick: (String) -> Unit,
    vm: CountriesViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    when (state) {
        UiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

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
                Button(onClick = { vm.load() }) { Text("Réessayer") }
            }
        }

        is UiState.Success<*> -> {
            val countries = (state as UiState.Success<List<Country>>).data
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(countries, key = { it.code }) { c ->
                    CountryRow(
                        country = c,
                        onClick = { onCountryClick(c.code) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryRow(
    country: Country,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(country.emoji, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.width(12.dp))
            Text(country.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}