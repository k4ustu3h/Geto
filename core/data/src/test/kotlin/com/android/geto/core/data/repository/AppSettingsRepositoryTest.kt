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

import com.android.geto.core.data.testdoubles.TestAppSettingsDao
import com.android.geto.core.database.model.asEntity
import com.android.geto.core.model.AppSetting
import com.android.geto.core.model.SettingType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppSettingsRepositoryTest {

    private lateinit var appSettingsDao: TestAppSettingsDao

    private lateinit var subject: AppSettingsRepository

    @Before
    fun setup() {
        appSettingsDao = TestAppSettingsDao()

        subject = DefaultAppSettingsRepository(appSettingsDao)
    }

    @Test
    fun appSettingsRepository_upsertAppSetting_delegates_to_dao() = runTest {
        val appSetting = AppSetting(
            id = 0,
            enabled = true,
            settingType = SettingType.SECURE,
            packageName = "com.android.geto",
            label = "Geto",
            key = "Geto",
            valueOnLaunch = "0",
            valueOnRevert = "1"
        )

        subject.upsertAppSetting(appSetting)

        assertTrue {
            appSetting.asEntity() in appSettingsDao.getAppSettingsByPackageName("com.android.geto")
                .first()
        }
    }

    @Test
    fun appSettingsRepository_deleteAppSetting_delegates_to_dao() = runTest {
        val appSetting = AppSetting(
            id = 0,
            enabled = true,
            settingType = SettingType.SECURE,
            packageName = "com.android.geto",
            label = "Geto",
            key = "Geto",
            valueOnLaunch = "0",
            valueOnRevert = "1"
        )

        subject.upsertAppSetting(appSetting)

        subject.deleteAppSetting(appSetting)

        assertFalse {
            appSetting.asEntity() in appSettingsDao.getAppSettingsByPackageName("com.android.geto")
                .first()
        }
    }

    @Test
    fun appSettingsRepository_deleteAppSettingsByPackageName_delegates_to_dao() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { subject.appSettings.collect() }

        val oldAppSettings = List(10) { _ ->
            AppSetting(
                enabled = false, settingType = SettingType.GLOBAL,
                packageName = "com.android.geto",
                label = "Geto", key = "Geto",
                valueOnLaunch = "0",
                valueOnRevert = "1"
            )
        }

        val newAppSettings = List(10) { _ ->
            AppSetting(
                enabled = false, settingType = SettingType.GLOBAL,
                packageName = "com.android.sample",
                label = "Sample", key = "Sample",
                valueOnLaunch = "0",
                valueOnRevert = "1"
            )
        }

        oldAppSettings.forEach { appSettings ->
            subject.upsertAppSetting(appSettings)
        }

        newAppSettings.forEach { appSettings ->
            subject.upsertAppSetting(appSettings)
        }

        subject.deleteAppSettingsByPackageName(packageNames = listOf("com.android.geto"))

        assertFalse {
            subject.appSettings.first().containsAll(oldAppSettings)
        }

        assertTrue {
            subject.appSettings.first().containsAll(newAppSettings)
        }

        collectJob.cancel()
    }

    @Test
    fun appSettingsRepository_getAppSettingsByPackageName() = runTest {
        val appSetting = AppSetting(
            id = 0,
            enabled = true,
            settingType = SettingType.SECURE,
            packageName = "com.android.geto",
            label = "Geto",
            key = "Geto",
            valueOnLaunch = "0",
            valueOnRevert = "1"
        )

        subject.upsertAppSetting(appSetting)

        val result = subject.getAppSettingsByPackageName("com.android.geto").first()

        assertTrue(result.isNotEmpty())
    }
}