package com.countries.data

import com.apollographql.apollo3.exception.ApolloException
import com.countries.domain.Country
import com.countries.graphql.CountriesQuery

class CountriesRepository {

    suspend fun getCountries(): List<Country> {
        try {
            val response = ApolloProvider.client
                .query(CountriesQuery())
                .execute()

            val countries = response.data?.countries
                ?: throw IllegalStateException("Réponse vide")

            return countries.map { country ->
                Country(
                    code = country.code,
                    name = country.name,
                    emoji = country.emoji,
                    capital = country.capital,
                    currency = country.currency,
                    continent = country.continent.name ?: "—",
                    languages = country.languages.mapNotNull { it.name }
                )
            }

        } catch (e: ApolloException) {
            throw RuntimeException(
                "Erreur réseau GraphQL: ${e.message}",
                e
            )
        }
    }
}