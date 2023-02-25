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
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.databinding.DialogServerBinding
import com.blacksquircle.ui.feature.servers.ui.viewmodel.ServerIntent
import com.blacksquircle.ui.feature.servers.ui.viewmodel.ServersViewModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftpes.FTPESFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import com.blacksquircle.ui.uikit.R as UiR

@AndroidEntryPoint
class ServerDialog : DialogFragment() {

    private val viewModel by hiltNavGraphViewModels<ServersViewModel>(R.id.servers_graph)
    private val navArgs by navArgs<ServerDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            customView(R.layout.dialog_server)

            val binding = DialogServerBinding.bind(getCustomView())
            if (navArgs.data.isNullOrEmpty()) {
                title(R.string.pref_add_server_title)
                positiveButton(UiR.string.common_save) {
                    val serverConfig = ServerConfig(
                        uuid = UUID.randomUUID().toString(),
                        scheme = when (binding.serverType.selectedItemPosition) {
                            0 -> FTPFilesystem.FTP_SCHEME
                            1 -> FTPSFilesystem.FTPS_SCHEME
                            2 -> FTPESFilesystem.FTPES_SCHEME
                            3 -> SFTPFilesystem.SFTP_SCHEME
                            else -> throw IllegalArgumentException("Unsupported file scheme")
                        },
                        name = binding.inputServerName.text.toString(),
                        address = binding.inputServerAddress.text.toString(),
                        port = binding.inputServerPort.text?.toString()?.toIntOrNull() ?: DEFAULT_FTP_PORT,
                        initialDir = binding.inputInitialDir.text.toString(),
                        authMethod = AuthMethod.PASSWORD,
                        username = binding.inputUsername.text.toString(),
                        password = binding.inputPassword.text.toString(),
                        privateKey = "",
                        passphrase = "",
                    )
                    viewModel.obtainEvent(ServerIntent.UpsertServer(serverConfig))
                }
                negativeButton(android.R.string.cancel)
            } else {
                title(R.string.pref_edit_server_title)

                val serverConfig = Gson().fromJson(navArgs.data, ServerConfig::class.java)
                binding.inputServerName.setText(serverConfig.name)
                binding.inputServerAddress.setText(serverConfig.address)
                binding.inputServerPort.setText(serverConfig.port.toString())
                binding.inputInitialDir.setText(serverConfig.initialDir)
                binding.inputUsername.setText(serverConfig.username)
                binding.inputPassword.setText(serverConfig.password)

                val scheme = when (serverConfig.scheme) {
                    FTPFilesystem.FTP_SCHEME -> 0
                    FTPSFilesystem.FTPS_SCHEME -> 1
                    FTPESFilesystem.FTPES_SCHEME -> 2
                    SFTPFilesystem.SFTP_SCHEME -> 3
                    else -> throw IllegalArgumentException("Unsupported file scheme")
                }
                binding.serverType.setSelection(scheme)

                positiveButton(UiR.string.common_save) {
                    val changedModel = ServerConfig(
                        uuid = serverConfig.uuid,
                        scheme = when (binding.serverType.selectedItemPosition) {
                            0 -> FTPFilesystem.FTP_SCHEME
                            1 -> FTPSFilesystem.FTPS_SCHEME
                            2 -> FTPESFilesystem.FTPES_SCHEME
                            3 -> SFTPFilesystem.SFTP_SCHEME
                            else -> throw IllegalArgumentException("Unsupported file scheme")
                        },
                        name = binding.inputServerName.text.toString(),
                        address = binding.inputServerAddress.text.toString(),
                        port = binding.inputServerPort.text?.toString()?.toIntOrNull() ?: DEFAULT_FTP_PORT,
                        initialDir = binding.inputInitialDir.text.toString(),
                        authMethod = AuthMethod.PASSWORD,
                        username = binding.inputUsername.text.toString(),
                        password = binding.inputPassword.text.toString(),
                        privateKey = "",
                        passphrase = "",
                    )
                    viewModel.obtainEvent(ServerIntent.UpsertServer(changedModel))
                }
                negativeButton(UiR.string.common_delete) {
                    viewModel.obtainEvent(ServerIntent.DeleteServer(serverConfig))
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_FTP_PORT = 21
    }
}