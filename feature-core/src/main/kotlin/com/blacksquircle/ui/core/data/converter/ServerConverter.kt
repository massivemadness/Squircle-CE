package com.blacksquircle.ui.core.data.converter

import com.blacksquircle.ui.core.data.storage.database.entity.server.ServerEntity
import com.blacksquircle.ui.filesystem.base.model.ServerModel

object ServerConverter {

    fun toModel(serverEntity: ServerEntity): ServerModel {
        return ServerModel(
            uuid = serverEntity.uuid,
            scheme = serverEntity.scheme,
            name = serverEntity.name,
            address = serverEntity.address,
            port = serverEntity.port,
            username = serverEntity.username,
            password = serverEntity.password,
        )
    }

    fun toEntity(serverModel: ServerModel): ServerEntity {
        return ServerEntity(
            uuid = serverModel.uuid,
            scheme = serverModel.scheme,
            name = serverModel.name,
            address = serverModel.address,
            port = serverModel.port,
            username = serverModel.username,
            password = serverModel.password,
        )
    }
}