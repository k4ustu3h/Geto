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
package com.android.geto.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.android.geto.designsystem.component.AnimatedWavyCircle
import com.android.geto.roborazzi.DefaultRoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "480dpi")
class WavyCircleScreenshotTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun animatedWavyCircle_animation() {
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.setContent {
            Surface {
                AnimatedWavyCircle(
                    modifier = Modifier.fillMaxSize(),
                    active = true,
                    onClick = {},
                )
            }
        }
        // Try multiple frames of the animation; some arbitrary, some synchronized with duration.
        listOf(0L, 250L, 500L).forEach { deltaTime ->
            composeTestRule.mainClock.advanceTimeBy(deltaTime)

            composeTestRule.onRoot().captureRoboImage(
                "src/test/screenshots/WavyCircle/AnimatedWavyCircle_animation_$deltaTime.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
        }
    }
}
