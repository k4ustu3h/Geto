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
import com.android.geto.core.model.AppSettings
import javax.inject.Inject

class AddAppSettingsUseCase @Inject constructor(
    private val secureSettingsRepository: SecureSettingsRepository,
    private val appSettingsRepository: AppSettingsRepository
) {
    suspend operator fun invoke(appSettings: AppSettings) {
        val safeToWrite =
            appSettings.key in secureSettingsRepository.getSecureSettings(appSettings.settingsType)
                .map { it.name }

        appSettingsRepository.upsertAppSettings(appSettings.copy(safeToWrite = safeToWrite))
    }
}