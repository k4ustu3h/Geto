/*
 *
 *   Copyright 2023 Einstein Blanco
 *
 *   Licensed under the GNU General Public License v3.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.gnu.org/licenses/gpl-3.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.android.geto

import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.android.geto.feature.securesettingslist.navigation.SECURE_SETTINGS_LIST_NAVIGATION_ROUTE
import com.android.geto.navigation.GetoNavHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.activity.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            GetoNavHost(navController = navController)

        }
    }

    @Test
    fun navHost_verifyStartDestination() {
        composeTestRule.onNodeWithTag("applist").assertIsDisplayed()
    }

    @Test
    fun navigateToSecureSettingsScreen_whenSettingsIconIsClicked() {
        composeTestRule.onNodeWithContentDescription(label = "Secure settings icon").performClick()

        val route = navController.currentDestination?.route

        assertEquals(expected = SECURE_SETTINGS_LIST_NAVIGATION_ROUTE, actual = route)
    }
}