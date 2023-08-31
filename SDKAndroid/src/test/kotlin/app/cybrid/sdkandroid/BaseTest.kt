package app.cybrid.sdkandroid

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cybrid.sdkandroid.tools.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class BaseTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    open fun setup() {

        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun teardown() {

        Cybrid.reset()
        unmockkAll()
    }
}