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

package com.android.geto.core.data.repository

import com.android.geto.core.model.AppSetting
import com.android.geto.core.model.SecureSetting
import com.android.geto.core.model.SettingType
import com.android.geto.core.securesettings.SecureSettingsPermissionWrapper
import javax.inject.Inject

class DefaultSecureSettingsRepository @Inject constructor(
    private val secureSettingsPermissionWrapper: SecureSettingsPermissionWrapper
) : SecureSettingsRepository {
    override suspend fun applySecureSettings(appSettingList: List<AppSetting>): Boolean {
        return appSettingList.all { appSettings ->
            secureSettingsPermissionWrapper.canWriteSecureSettings(
                appSetting = appSettings,
                valueSelector = { appSettings.valueOnLaunch })
        }
    }

    override suspend fun revertSecureSettings(appSettingList: List<AppSetting>): Boolean {
        return appSettingList.all { appSettings ->
            secureSettingsPermissionWrapper.canWriteSecureSettings(
                appSetting = appSettings,
                valueSelector = { appSettings.valueOnRevert })
        }
    }

    override suspend fun getSecureSettings(settingType: SettingType): List<SecureSetting> {
        return secureSettingsPermissionWrapper.getSecureSettings(settingType)
    }
}