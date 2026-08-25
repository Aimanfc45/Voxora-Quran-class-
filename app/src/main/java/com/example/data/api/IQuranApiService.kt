package com.example.data.api

import com.example.data.model.JuzInfo
import com.example.data.model.Surah
import com.example.data.model.Verse

/**
 * Interface prepared for future integration with Al-Quran Cloud / Tanzil REST API.
 */
interface IQuranApiService {
    suspend fun getSurahList(): Result<List<Surah>>
    suspend fun getSurahDetail(surahNumber: Int): Result<Surah>
    suspend fun getJuzList(): Result<List<JuzInfo>>
    suspend fun searchVerses(query: String): Result<List<Verse>>
}

/**
 * Mock implementation of IQuranApiService for offline prototyping.
 */
class MockQuranApiService : IQuranApiService {
    override suspend fun getSurahList(): Result<List<Surah>> =
        Result.success(com.example.data.mock.MockQuranData.surahList)

    override suspend fun getSurahDetail(surahNumber: Int): Result<Surah> {
        val found = com.example.data.mock.MockQuranData.surahList.find { it.number == surahNumber }
            ?: com.example.data.mock.MockQuranData.surahList.first()
        return Result.success(found)
    }

    override suspend fun getJuzList(): Result<List<JuzInfo>> =
        Result.success(com.example.data.mock.MockQuranData.juzList)

    override suspend fun searchVerses(query: String): Result<List<Verse>> {
        val allVerses = com.example.data.mock.MockQuranData.surahList.flatMap { it.verses }
        return Result.success(allVerses.filter {
            it.textArabic.contains(query) ||
                    it.translationEnglish.contains(query, ignoreCase = true) ||
                    it.transliteration.contains(query, ignoreCase = true)
        })
    }
}
