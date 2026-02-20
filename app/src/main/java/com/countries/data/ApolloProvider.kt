package com.countries.data

import com.apollographql.apollo3.ApolloClient

object ApolloProvider {
    val client: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://countries.trevorblades.com/graphql")
        .build()
}
