package space.mrandika.dicogram.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import space.mrandika.dicogram.prefs.TokenPreferences
import space.mrandika.dicogram.service.DicogramService
import space.mrandika.dicogram.utils.MainDispatcherRule
import space.mrandika.dicogram.utils.dummy.AuthDummy

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class AuthRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var pref: TokenPreferences

    @Mock
    private lateinit var service: DicogramService
    private lateinit var repository: AuthRepository

    private val nameValue = "Fullname"
    private val emailValue = "email@mail.com"
    private val passwordValue = "securepassword"
    private val tokenValue = "Bearer AUTHENTICATION_TOKEN"

    @Before
    fun setup() {
        repository = AuthRepository(service, pref)
    }

    @Test
    fun `User can login`(): Unit = runTest {
        val expectedResponse = AuthDummy.generateDummyLoginResponse()

        `when`(service.login(emailValue, passwordValue)).thenReturn(expectedResponse)

        repository.login(emailValue, passwordValue).collect { response ->
            Assert.assertTrue(response.isSuccess)

            // Assert success response
            response.onSuccess {
                Assert.assertNotNull(it)
                Assert.assertEquals(expectedResponse, it)
            }

            // Assert failure response
            response.onFailure {
                Assert.assertNull(it)
            }
        }
    }

    @Test
    fun `Login failures throws exception`(): Unit = runTest {
        `when`(service.login(emailValue, passwordValue)).then {
            throw Exception()
        }

        repository.login(emailValue, passwordValue).collect { response ->
            Assert.assertTrue(response.isFailure)

            // Assert failure response
            response.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun `User can register`(): Unit = runTest {
        val expectedResponse = AuthDummy.generateDummyRegisterResponse()

        `when`(service.register(nameValue, emailValue, passwordValue)).thenReturn(expectedResponse)

        repository.register(nameValue, emailValue, passwordValue).collect { response ->
            Assert.assertTrue(response.isSuccess)

            // Assert success response
            response.onSuccess {
                Assert.assertNotNull(it)
                Assert.assertEquals(expectedResponse, it)
            }

            // Assert failure response
            response.onFailure {
                Assert.assertNull(it)
            }
        }
    }

    @Test
    fun `Register failures throws exception`(): Unit = runTest {
        `when`(service.register(nameValue, emailValue, passwordValue)).then {
            throw Exception()
        }

        repository.register(nameValue, emailValue, passwordValue).collect { response ->
            Assert.assertTrue(response.isFailure)

            // Assert failure response
            response.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun `Token can be saved`(): Unit = runTest {
        repository.saveAccessToken(tokenValue)

        Mockito.verify(pref).saveAccessToken(tokenValue)
    }

    @Test
    fun `Token is saved`(): Unit = runTest {
        val expectedValue = flowOf(tokenValue)

        `when`(pref.getAccessToken()).thenReturn(expectedValue)

        repository.getAccessToken().collect {
            // Check the token value
            Assert.assertNotNull(it)
            Assert.assertEquals(tokenValue, it)
        }

        Mockito.verify(pref).getAccessToken()
    }
}