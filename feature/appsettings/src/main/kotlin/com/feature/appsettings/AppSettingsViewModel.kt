package com.feature.appsettings

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.domain.repository.AppSettingsRepository
import com.core.domain.usecase.AddAppSettingsUseCase
import com.core.domain.usecase.ApplyAppSettingsUseCase
import com.core.domain.usecase.RevertAppSettingsUseCase
import com.core.domain.wrapper.PackageManagerWrapper
import com.feature.appsettings.dialog.addsettings.AddSettingsDialogUiState
import com.feature.appsettings.dialog.copypermissioncommand.CopyPermissionCommandUiState
import com.feature.appsettings.navigation.AppSettingsArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appSettingsRepository: AppSettingsRepository,
    private val packageManagerWrapper: PackageManagerWrapper,
    private val applyAppSettingsUseCase: ApplyAppSettingsUseCase,
    private val revertAppSettingsUseCase: RevertAppSettingsUseCase,
    private val addAppSettingsUseCase: AddAppSettingsUseCase
) : ViewModel() {
    private var _snackBar = MutableStateFlow<String?>(null)

    private var _launchAppIntent = MutableStateFlow<Intent?>(null)

    private var _addSettingsDialogUiState =
        MutableStateFlow<AddSettingsDialogUiState>(AddSettingsDialogUiState.HideAddSettingsDialog)

    private var _copyPermissionCommandDialogUiState =
        MutableStateFlow<CopyPermissionCommandUiState>(CopyPermissionCommandUiState.HideCopyPermissionCommandDialog)

    private val appSettingsArgs: AppSettingsArgs = AppSettingsArgs(savedStateHandle)

    val packageName = appSettingsArgs.packageName

    val appName = appSettingsArgs.appName

    val snackBar = _snackBar.asStateFlow()

    val launchAppIntent = _launchAppIntent.asStateFlow()

    val addSettingsDialogUiState = _addSettingsDialogUiState.asStateFlow()

    val copyPermissionCommandDialogUiState = _copyPermissionCommandDialogUiState.asStateFlow()

    private var _dismissDialog = MutableStateFlow(false)

    val appSettingsUiState: StateFlow<AppSettingsUiState> =
        appSettingsRepository.getAppSettingsList(packageName).map { appSettingsList ->
            if (appSettingsList.isNotEmpty()) AppSettingsUiState.Success(appSettingsList)
            else AppSettingsUiState.Empty
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettingsUiState.Loading
        )

    fun onEvent(event: AppSettingsEvent) {
        when (event) {
            is AppSettingsEvent.OnLaunchApp -> {
                viewModelScope.launch {
                    applyAppSettingsUseCase(packageName = packageName,
                                            onEmptyAppSettingsList = { message ->
                                                _snackBar.update { message }
                                            },
                                            onAppSettingsDisabled = { message ->
                                                _snackBar.update { message }
                                            },
                                            onAppSettingsNotSafeToWrite = { message ->
                                                _snackBar.update { message }
                                            },
                                            onApplied = {
                                                val appIntent =
                                                    packageManagerWrapper.getLaunchIntentForPackage(
                                                        packageName
                                                    )
                                                _launchAppIntent.update { appIntent }
                                            },
                                            onSecurityException = {
                                                _copyPermissionCommandDialogUiState.update { CopyPermissionCommandUiState.ShowCopyPermissionCommandDialog }
                                            },
                                            onFailure = { message ->
                                                _snackBar.update { message }
                                            })
                }
            }

            is AppSettingsEvent.OnRevertSettings -> {
                viewModelScope.launch {
                    revertAppSettingsUseCase(packageName = packageName,
                                             onEmptyAppSettingsList = { message ->
                                                 _snackBar.update { message }
                                             },
                                             onAppSettingsDisabled = { message ->
                                                 _snackBar.update { message }
                                             },
                                             onAppSettingsNotSafeToWrite = { message ->
                                                 _snackBar.update { message }
                                             },
                                             onReverted = { message ->
                                                 _snackBar.update { message }
                                             },
                                             onSecurityException = {
                                                 _copyPermissionCommandDialogUiState.update { CopyPermissionCommandUiState.ShowCopyPermissionCommandDialog }
                                             },
                                             onFailure = { message ->
                                                 _snackBar.update { message }
                                             })
                }
            }

            is AppSettingsEvent.OnAppSettingsItemCheckBoxChange -> {
                viewModelScope.launch {
                    val updatedUserAppSettingsItem = event.appSettings.copy(enabled = event.checked)

                    appSettingsRepository.upsertAppSettings(
                        updatedUserAppSettingsItem
                    )
                }
            }

            is AppSettingsEvent.OnDeleteAppSettingsItem -> {
                viewModelScope.launch {
                    appSettingsRepository.deleteAppSettings(event.appSettings)
                }
            }

            is AppSettingsEvent.AddSettings -> {
                viewModelScope.launch {
                    addAppSettingsUseCase(event.appSettings)

                    _dismissDialog.update { true }
                }
            }

            is AppSettingsEvent.ShowAddSettingsDialog -> {
                _addSettingsDialogUiState.update { AddSettingsDialogUiState.ShowAddSettingsDialog }
            }

            AppSettingsEvent.HideAddSettingsDialog -> {
                _addSettingsDialogUiState.update { AddSettingsDialogUiState.HideAddSettingsDialog }
            }

            AppSettingsEvent.HideCopyPermissionCommandDialog -> {
                _copyPermissionCommandDialogUiState.update { CopyPermissionCommandUiState.HideCopyPermissionCommandDialog }
            }

            AppSettingsEvent.ShowCopyPermissionCommandDialog -> {
                _copyPermissionCommandDialogUiState.update { CopyPermissionCommandUiState.ShowCopyPermissionCommandDialog }
            }
        }
    }

    fun clearSnackBar() {
        _snackBar.update { null }
    }

    fun clearLaunchAppIntent() {
        _launchAppIntent.update { null }
    }
}