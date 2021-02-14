package com.brackeys.ui.domain.repository.fonts

import com.brackeys.ui.domain.model.font.FontModel

interface FontsRepository {
    suspend fun fetchFonts(searchQuery: String): List<FontModel>
    suspend fun createFont(fontModel: FontModel)
    suspend fun removeFont(fontModel: FontModel)
    suspend fun selectFont(fontModel: FontModel)
}