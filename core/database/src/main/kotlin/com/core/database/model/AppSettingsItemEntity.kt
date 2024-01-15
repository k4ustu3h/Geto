package com.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.core.model.AppSettings
import com.core.model.SettingsType

@Entity
data class AppSettingsItemEntity(
    @PrimaryKey val id: Int? = null,
    val enabled: Boolean,
    val settingsType: SettingsType,
    val packageName: String,
    val label: String,
    val key: String,
    val valueOnLaunch: String,
    val valueOnRevert: String,
    @ColumnInfo(name = "safeToWrite", defaultValue = "0") val safeToWrite: Boolean
)

fun AppSettings.asExternalModel(): AppSettingsItemEntity {
    return AppSettingsItemEntity(
        id = id,
        enabled = enabled,
        settingsType = settingsType,
        packageName = packageName,
        label = label,
        key = key,
        valueOnLaunch = valueOnLaunch,
        valueOnRevert = valueOnRevert,
        safeToWrite = safeToWrite
    )
}
