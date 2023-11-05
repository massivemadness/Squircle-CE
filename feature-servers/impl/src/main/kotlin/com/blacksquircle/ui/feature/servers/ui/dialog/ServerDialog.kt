/*
 * Copyright 2023 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.servers.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.OpenFileContract
import com.blacksquircle.ui.core.extensions.extractFilePath
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.databinding.DialogServerBinding
import com.blacksquircle.ui.feature.servers.ui.mvi.ServerIntent
import com.blacksquircle.ui.feature.servers.ui.viewmodel.ServersViewModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftpes.FTPESFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import com.blacksquircle.ui.uikit.R as UiR

@AndroidEntryPoint
class ServerDialog : DialogFragment() {

    private val viewModel by hiltNavGraphViewModels<ServersViewModel>(R.id.servers_graph)
    private val navArgs by navArgs<ServerDialogArgs>()
    private val openFileContract = OpenFileContract(this) { result ->
        when (result) {
            is ContractResult.Success -> {
                val filePath = context?.extractFilePath(result.uri)
                binding.inputKeyFile.setText(filePath)
            }
            is ContractResult.Canceled -> Unit
        }
    }

    private lateinit var binding: DialogServerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogServerBinding.inflate(layoutInflater)
        configureView()

        return AlertDialog.Builder(requireContext()).apply {
            if (navArgs.data.isNullOrEmpty()) {
                setTitle(R.string.pref_add_server_title)
                setView(binding.root)
                setPositiveButton(UiR.string.common_save) { _, _ ->
                    saveServerData()
                }
                setNegativeButton(android.R.string.cancel, null)
            } else {
                val serverConfig = Gson().fromJson(navArgs.data, ServerConfig::class.java) // FIXME
                fillConfigData(serverConfig)

                setTitle(R.string.pref_edit_server_title)
                setView(binding.root)
                setPositiveButton(UiR.string.common_save) { _, _ ->
                    saveServerData(serverConfig.uuid)
                }
                setNegativeButton(UiR.string.common_delete) { _, _ ->
                    viewModel.obtainEvent(ServerIntent.DeleteServer(serverConfig))
                }
            }
        }.create()
    }

    private fun saveServerData(uuid: String? = null) {
        val serverConfig = ServerConfig(
            uuid = uuid ?: UUID.randomUUID().toString(),
            scheme = when (binding.serverType.selectedItemPosition) {
                SERVER_FTP -> FTPFilesystem.FTP_SCHEME
                SERVER_FTPS -> FTPSFilesystem.FTPS_SCHEME
                SERVER_FTPES -> FTPESFilesystem.FTPES_SCHEME
                SERVER_SFTP -> SFTPFilesystem.SFTP_SCHEME
                else -> throw IllegalArgumentException("Unsupported file scheme")
            },
            name = binding.inputServerName.text.toString(),
            address = binding.inputServerAddress.text.toString(),
            port = binding.inputServerPort.text?.toString()?.toIntOrNull()
                ?: binding.inputServerPort.hint?.toString()?.toIntOrNull()
                ?: HINT_FTP_PORT,
            initialDir = binding.inputInitialDir.text.toString(),
            authMethod = if (
                binding.serverType.selectedItemPosition == SERVER_SFTP &&
                binding.authMethod.selectedItemPosition == AUTH_KEY
            ) {
                AuthMethod.KEY
            } else {
                AuthMethod.PASSWORD
            },
            username = binding.inputUsername.text.toString(),
            password = if (
                binding.authMethod.selectedItemPosition == AUTH_PASSWORD &&
                binding.passwordBehavior.selectedItemPosition == SAVE
            ) {
                binding.inputPassword.text.toString()
            } else {
                null
            },
            privateKey = if (
                binding.serverType.selectedItemPosition == SERVER_SFTP &&
                binding.authMethod.selectedItemPosition == AUTH_KEY
            ) {
                binding.inputKeyFile.text.toString()
            } else {
                null
            },
            passphrase = if (
                binding.serverType.selectedItemPosition == SERVER_SFTP &&
                binding.authMethod.selectedItemPosition == AUTH_KEY &&
                binding.passphraseBehavior.selectedItemPosition == SAVE
            ) {
                binding.inputPassphrase.text.toString()
            } else {
                null
            },
        )

        val isServerNameValid = serverConfig.name.isNotBlank()
        if (!isServerNameValid) {
            binding.serverNameLayout.error = getString(R.string.hint_required)
            binding.serverNameLayout.isErrorEnabled = true
        }

        val isServerAddressValid = serverConfig.address.isNotBlank()
        if (!isServerAddressValid) {
            binding.serverAddressLayout.error = getString(R.string.hint_required)
            binding.serverAddressLayout.isErrorEnabled = true
        }

        if (isServerNameValid && isServerAddressValid) {
            viewModel.obtainEvent(ServerIntent.UpsertServer(serverConfig))
            dismiss()
        }
    }

    private fun configureView() {
        binding.inputServerName.doAfterTextChanged {
            binding.serverNameLayout.isErrorEnabled = false
        }
        binding.inputServerAddress.doAfterTextChanged {
            binding.serverAddressLayout.isErrorEnabled = false
        }
        binding.inputServerAddress.hint = HINT_ADDRESS
        binding.inputServerPort.hint = HINT_FTP_PORT.toString()

        binding.keyFileLayout.setEndIconOnClickListener {
            openFileContract.launch(
                OpenFileContract.OCTET_STREAM,
                OpenFileContract.PEM,
            )
        }
        binding.serverType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    SERVER_FTP,
                    SERVER_FTPS,
                    SERVER_FTPES -> {
                        binding.inputServerPort.hint = HINT_FTP_PORT.toString()
                        if (binding.authMethod.isVisible) {
                            binding.authMethod.isVisible = false
                            binding.passphraseBehavior.setSelection(ASK)
                            passwordAuthLayout()
                        }
                    }
                    SERVER_SFTP -> {
                        binding.inputServerPort.hint = HINT_SFTP_PORT.toString()
                        if (!binding.authMethod.isVisible) {
                            binding.authMethod.isVisible = true

                            if (binding.authMethod.selectedItemPosition == AUTH_PASSWORD) {
                                binding.passphraseBehavior.setSelection(ASK)
                                passwordAuthLayout()
                            } else {
                                binding.passwordBehavior.setSelection(ASK)
                                passphraseAuthLayout()
                            }
                        }
                    }
                }
            }
        }
        binding.authMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    AUTH_PASSWORD -> passwordAuthLayout()
                    AUTH_KEY -> passphraseAuthLayout()
                }
            }
        }
        binding.passwordBehavior.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.hintPassword.isVisible = position == SAVE
                binding.inputPassword.isVisible = position == SAVE
            }
        }
        binding.passphraseBehavior.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.hintPassphrase.isVisible = position == SAVE
                binding.inputPassphrase.isVisible = position == SAVE
            }
        }
    }

    private fun passwordAuthLayout() {
        binding.passwordBehavior.isVisible = true
        if (binding.passwordBehavior.selectedItemPosition == ASK) {
            binding.hintPassword.isVisible = false
            binding.inputPassword.isVisible = false
        } else {
            binding.hintPassword.isVisible = true
            binding.inputPassword.isVisible = true
        }

        // Hide passphrase layout
        binding.passphraseBehavior.isVisible = false
        binding.hintPassphrase.isVisible = false
        binding.inputPassphrase.isVisible = false

        // Key selection is not supported
        binding.hintKeyFile.isVisible = false
        binding.keyFileLayout.isVisible = false
    }

    private fun passphraseAuthLayout() {
        binding.passphraseBehavior.isVisible = true
        if (binding.passphraseBehavior.selectedItemPosition == ASK) {
            binding.hintPassphrase.isVisible = false
            binding.inputPassphrase.isVisible = false
        } else {
            binding.hintPassphrase.isVisible = true
            binding.inputPassphrase.isVisible = true
        }

        // Hide password layout
        binding.passwordBehavior.isVisible = false
        binding.hintPassword.isVisible = false
        binding.inputPassword.isVisible = false

        // Key selection is available
        binding.hintKeyFile.isVisible = true
        binding.keyFileLayout.isVisible = true
    }

    private fun fillConfigData(serverConfig: ServerConfig) {
        binding.inputServerName.setText(serverConfig.name)
        binding.inputServerAddress.setText(serverConfig.address)
        binding.inputServerPort.setText(serverConfig.port.toString())
        binding.inputUsername.setText(serverConfig.username)
        binding.inputPassword.setText(serverConfig.password)
        binding.inputPassphrase.setText(serverConfig.passphrase)
        binding.inputKeyFile.setText(serverConfig.privateKey)
        binding.inputInitialDir.setText(serverConfig.initialDir)

        binding.serverType.setSelection(
            when (serverConfig.scheme) {
                FTPFilesystem.FTP_SCHEME -> SERVER_FTP
                FTPSFilesystem.FTPS_SCHEME -> SERVER_FTPS
                FTPESFilesystem.FTPES_SCHEME -> SERVER_FTPES
                SFTPFilesystem.SFTP_SCHEME -> SERVER_SFTP
                else -> throw IllegalArgumentException("Unsupported file scheme")
            }
        )
        when (serverConfig.authMethod) {
            AuthMethod.PASSWORD -> {
                binding.authMethod.setSelection(AUTH_PASSWORD)
                binding.passwordBehavior.setSelection(
                    if (serverConfig.password == null) ASK else SAVE
                )
            }
            AuthMethod.KEY -> {
                binding.authMethod.setSelection(AUTH_KEY)
                binding.passphraseBehavior.setSelection(
                    if (serverConfig.passphrase == null) ASK else SAVE
                )
            }
        }
    }

    companion object {
        private const val SERVER_FTP = 0
        private const val SERVER_FTPS = 1
        private const val SERVER_FTPES = 2
        private const val SERVER_SFTP = 3

        private const val ASK = 0
        private const val SAVE = 1

        private const val AUTH_PASSWORD = 0
        private const val AUTH_KEY = 1

        private const val HINT_ADDRESS = "192.168.21.101"
        private const val HINT_FTP_PORT = 21
        private const val HINT_SFTP_PORT = 22
    }
}