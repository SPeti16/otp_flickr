package com.test.otp

import android.widget.TextView
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.test.otp.network.FlickrApiService
import com.test.otp.ui.FlickApp
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import retrofit2.Retrofit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.test.otp", appContext.packageName)
    }
}

@RunWith(AndroidJUnit4::class)
class ApiServiceTest {

    private lateinit var apiService: FlickrApiService
    private val json = Json { ignoreUnknownKeys = true }
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.url_base))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        apiService = retrofit.create(FlickrApiService::class.java)
    }

    @Test
    fun testGetOffers() = runBlocking {
        try {
            val photos = apiService.searchPhotos(
                apiKey = BuildConfig.API_KEY,
                text = context.getString(R.string.default_search),
                page = 1
            )
            assertTrue("Photo is not empty", photos.photos.photo.isNotEmpty())
            assertNotNull(photos.photos.photo[0].id)
        } catch (e: Exception) {
            fail("Failed to load photo: ${e.message}")
        }
    }

    @Test
    fun testGetDetail() = runBlocking {
        try {
            val photos = apiService.searchPhotos(
                apiKey = BuildConfig.API_KEY,
                text = context.getString(R.string.default_search),
                page = 1
            )
            val info = apiService.getInfo(apiKey = BuildConfig.API_KEY, id = photos.photos.photo[0].id)
            assertNotNull("Info should not be null", info)
            assertEquals("Expected photo id", photos.photos.photo[0].id, info.photo.id)
        } catch (e: Exception) {
            fail("Failed to load info: ${e.message}")
        }
    }


}

@RunWith(AndroidJUnit4::class)
class ComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test() {

        composeTestRule.onNodeWithTag("dark_light_mode").assertExists().performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("dark_light_mode").performClick()

        composeTestRule.waitForIdle()
    }
}