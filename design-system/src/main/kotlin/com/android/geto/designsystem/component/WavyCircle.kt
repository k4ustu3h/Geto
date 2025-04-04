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
package com.android.geto.designsystem.component

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun AnimatedWavyCircle(
    modifier: Modifier = Modifier,
    active: Boolean,
    colors: WavyCircleColors = WavyCircleDefaults.colors(),
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 10000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "",
    )

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { offset ->
                    val radius = minOf(size.width, size.height) / 3
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val distanceFromCenter =
                        (offset.x - centerX).pow(2) + (offset.y - centerY).pow(2)

                    if (distanceFromCenter <= radius * radius) {
                        onClick()
                    }
                },
            )
        },
    ) {
        val radius = size.minDimension / 3
        val centerX = size.width / 2
        val centerY = size.height / 2
        val waveFrequency = 12
        val waveAmplitude = radius * 0.05f

        val path = Path().apply {
            moveTo(centerX + radius, centerY)

            for (i in 0..360 step 1) {
                val angleRad = i * (PI / 180)
                val wave = waveAmplitude * sin(waveFrequency * angleRad)
                val x = centerX + (radius + wave) * cos(angleRad)
                val y = centerY + (radius + wave) * sin(angleRad)
                lineTo(x.toFloat(), y.toFloat())
            }
            close()
        }

        withTransform(
            {
                scale(scaleX = scale, scaleY = scale)
                rotate(degrees = rotation)
            },
        ) {
            drawPath(
                path = path,
                color = if (active) colors.activeColor else colors.inActiveColor,
                style = Fill,
            )
        }
    }
}

object WavyCircleDefaults {
    @Composable
    fun colors(
        activeColor: Color = MaterialTheme.colorScheme.inversePrimary,
        inActiveColor: Color = MaterialTheme.colorScheme.error,
    ): WavyCircleColors {
        return WavyCircleColors(activeColor = activeColor, inActiveColor = inActiveColor)
    }
}

class WavyCircleColors internal constructor(
    val activeColor: Color,
    val inActiveColor: Color,
)
