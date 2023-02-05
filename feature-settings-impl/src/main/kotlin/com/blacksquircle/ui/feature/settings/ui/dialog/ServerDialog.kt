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

package com.blacksquircle.ui.feature.settings.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.databinding.DialogServerBinding
import com.blacksquircle.ui.feature.settings.ui.viewmodel.SettingsViewModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerModel
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftpes.FTPESFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ServerDialog : DialogFragment() {

    private val viewModel by hiltNavGraphViewModels<SettingsViewModel>(R.id.settings_graph)
    private val navArgs by navArgs<ServerDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            customView(R.layout.dialog_server)

            val binding = DialogServerBinding.bind(getCustomView())
            if (navArgs.data.isNullOrEmpty()) {
                title(R.string.pref_add_server_title)
                positiveButton(R.string.action_save) {
                    val serverModel = ServerModel(
                        uuid = UUID.randomUUID().toString(),
                        scheme = when (binding.serverType.selectedItemPosition) {
                            0 -> FTPFilesystem.FTP_SCHEME
                            1 -> FTPSFilesystem.FTPS_SCHEME
                            2 -> FTPESFilesystem.FTPES_SCHEME
                            3 -> SFTPFilesystem.SFTP_SCHEME
                            else -> throw IllegalArgumentException("Unsupported scheme type")
                        },
                        name = binding.inputServerName.text.toString(),
                        address = binding.inputServerAddress.text.toString(),
                        port = binding.inputServerPort.text?.toString()?.toIntOrNull() ?: DEFAULT_FTP_PORT,
                        authMethod = AuthMethod.PASSWORD,
                        username = binding.inputUsername.text.toString(),
                        password = binding.inputPassword.text.toString(),
                        privateKey = "",
                        passphrase = "",
                    )
                    viewModel.upsertServer(serverModel)
                }
                negativeButton(android.R.string.cancel)
            } else {
                title(R.string.pref_edit_server_title)

                val serverModel = Gson().fromJson(navArgs.data, ServerModel::class.java)
                binding.inputServerName.setText(serverModel.name)
                binding.inputServerAddress.setText(serverModel.address)
                binding.inputServerPort.setText(serverModel.port.toString())
                binding.inputUsername.setText(serverModel.username)
                binding.inputPassword.setText(serverModel.password)

                val scheme = when (serverModel.scheme) {
                    FTPFilesystem.FTP_SCHEME -> 0
                    FTPSFilesystem.FTPS_SCHEME -> 1
                    FTPESFilesystem.FTPES_SCHEME -> 2
                    SFTPFilesystem.SFTP_SCHEME -> 3
                    else -> throw IllegalArgumentException("Unsupported scheme type")
                }
                binding.serverType.setSelection(scheme)

                positiveButton(R.string.action_save) {
                    val changedModel = ServerModel(
                        uuid = serverModel.uuid,
                        scheme = when (binding.serverType.selectedItemPosition) {
                            0 -> FTPFilesystem.FTP_SCHEME
                            1 -> FTPSFilesystem.FTPS_SCHEME
                            2 -> FTPESFilesystem.FTPES_SCHEME
                            3 -> SFTPFilesystem.SFTP_SCHEME
                            else -> throw IllegalArgumentException("Unsupported scheme type")
                        },
                        name = binding.inputServerName.text.toString(),
                        address = binding.inputServerAddress.text.toString(),
                        port = binding.inputServerPort.text?.toString()?.toIntOrNull() ?: DEFAULT_FTP_PORT,
                        authMethod = AuthMethod.PASSWORD,
                        username = binding.inputUsername.text.toString(),
                        password = binding.inputPassword.text.toString(),
                        privateKey = "",
                        passphrase = "",
                    )
                    viewModel.upsertServer(changedModel)
                }
                negativeButton(R.string.action_delete) {
                    viewModel.deleteServer(serverModel)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_FTP_PORT = 21
    }
}