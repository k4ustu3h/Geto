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
package com.android.geto.feature.shizuku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.geto.core.domain.framework.ShizukuWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShizukuViewModel @Inject constructor(private val shizukuWrapper: ShizukuWrapper) :
    ViewModel() {

    val shizukuStatus = shizukuWrapper.shizukuStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun onEvent(event: ShizukuEvent) {
        when (event) {
            ShizukuEvent.CheckShizukuShizuku -> {
                shizukuWrapper.checkShizukuPermission()
            }

            ShizukuEvent.OnCreate -> {
                shizukuWrapper.onCreate()
            }

            ShizukuEvent.OnDestroy -> {
                shizukuWrapper.onDestroy()
            }
        }
    }
}
