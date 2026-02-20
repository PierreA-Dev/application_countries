package com.countries

import com.countries.data.CountriesRepository
import com.countries.domain.Country
import com.countries.ui.CountriesViewModel
import com.countries.ui.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesViewModelTest {

    private val dispatcherTest = StandardTestDispatcher()
    private lateinit var repository: CountriesRepository
    private lateinit var viewModel: CountriesViewModel

    @Before
    fun initialiser() {
        Dispatchers.setMain(dispatcherTest)
        repository = mockk()
    }

    @After
    fun nettoyer() {
        Dispatchers.resetMain()
    }

    // ✅ 1
    @Test
    fun `doit passer en Success quand le repository retourne des pays`() = runTest {

        val paysFictifs = listOf(
            Country(
                code = "FR",
                name = "France",
                emoji = "🇫🇷",
                capital = "Paris",
                currency = "EUR",
                continent = "Europe",
                languages = listOf("Français")
            )
        )

        coEvery { repository.getCountries() } returns paysFictifs

        viewModel = CountriesViewModel(repository)

        viewModel.load()
        dispatcherTest.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is UiState.Success)
    }

    // ❌ 2
    @Test
    fun `doit passer en Error quand le repository lance une exception`() = runTest {

        coEvery { repository.getCountries() } throws RuntimeException("Erreur réseau")

        viewModel = CountriesViewModel(repository)

        viewModel.load()
        dispatcherTest.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is UiState.Error)
    }

    // 3
    @Test
    fun `doit commencer par Loading puis finir en Success`() = runTest {

        val pays = emptyList<Country>()

        coEvery { repository.getCountries() } returns pays

        viewModel = CountriesViewModel(repository)

        viewModel.load()

        // Vérifie qu'on passe par Loading
        assertTrue(viewModel.state.value is UiState.Loading)

        dispatcherTest.scheduler.advanceUntilIdle()

        // Puis Success
        assertTrue(viewModel.state.value is UiState.Success)
    }

    // 4
    @Test
    fun `doit commencer par Loading puis finir en Error en cas d echec`() = runTest {

        coEvery { repository.getCountries() } throws RuntimeException("Boom")

        viewModel = CountriesViewModel(repository)

        viewModel.load()

        assertTrue(viewModel.state.value is UiState.Loading)

        dispatcherTest.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is UiState.Error)
    }

    // 5
    @Test
    fun `doit retourner une liste vide si le repository retourne une liste vide`() = runTest {

        coEvery { repository.getCountries() } returns emptyList()

        viewModel = CountriesViewModel(repository)

        viewModel.load()
        dispatcherTest.scheduler.advanceUntilIdle()

        val etat = viewModel.state.value

        assertTrue(etat is UiState.Success)
        assertEquals(0, (etat as UiState.Success).data.size)
    }
}