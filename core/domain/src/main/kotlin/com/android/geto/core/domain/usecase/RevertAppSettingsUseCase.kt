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

package com.android.geto.core.domain.usecase

import com.android.geto.core.domain.repository.AppSettingsRepository
import com.android.geto.core.domain.repository.SecureSettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RevertAppSettingsUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val secureSettingsRepository: SecureSettingsRepository
) {

    suspend operator fun invoke(
        packageName: String,
        onEmptyAppSettingsList: (String) -> Unit,
        onAppSettingsDisabled: (String) -> Unit,
        onAppSettingsNotSafeToWrite: (String) -> Unit,
        onReverted: (String) -> Unit,
        onSecurityException: (String) -> Unit,
        onFailure: (String?) -> Unit
    ) {

        val appSettingsList = appSettingsRepository.getAppSettingsList(packageName).first()

        if (appSettingsList.isEmpty()) {

            onEmptyAppSettingsList("No settings found")

            return
        }

        if (appSettingsList.any { !it.enabled }) {

            onAppSettingsDisabled("Please enable atleast one setting")

            return
        }

        if (appSettingsList.any { !it.safeToWrite }) {

            onAppSettingsNotSafeToWrite("Reverting settings that don't exist is not allowed. Please remove items highlighted as red")

            return
        }

        secureSettingsRepository.revertSecureSettings(appSettingsList).onSuccess { reverted ->
            if (reverted) {
                onReverted("Settings reverted")
            } else {
                onFailure("Database failure")
            }
        }.onFailure { t ->
            if (t is SecurityException) {
                onSecurityException("Permission not granted")
            } else {
                onFailure(t.localizedMessage)
            }
        }
    }
}