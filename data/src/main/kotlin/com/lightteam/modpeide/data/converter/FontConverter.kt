package com.lightteam.modpeide.data.converter

import com.lightteam.modpeide.data.feature.font.FontModel
import com.lightteam.modpeide.data.model.entity.FontEntity

object FontConverter {

    fun toModel(entity: FontEntity): FontModel {
        return FontModel(
            fontName = entity.fontName,
            fontPath = entity.fontPath,
            supportLigatures = entity.supportLigatures,
            isExternal = entity.isExternal,
            isPaid = entity.isPaid
        )
    }
}