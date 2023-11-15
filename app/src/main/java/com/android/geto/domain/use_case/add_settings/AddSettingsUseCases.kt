package com.android.geto.domain.use_case.add_settings

import com.android.geto.domain.use_case.ValidatePackageName

data class AddSettingsUseCases(
    val validateLabel: ValidateLabel,
    val validateKey: ValidateKey,
    val validateValueOnLaunch: ValidateValueOnLaunch,
    val validateValueOnRevert: ValidateValueOnRevert,
    val validatePackageName: ValidatePackageName
)
