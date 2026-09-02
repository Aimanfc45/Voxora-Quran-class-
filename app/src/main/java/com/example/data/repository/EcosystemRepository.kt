package com.example.data.repository

import com.example.data.mock.EcosystemData
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EcosystemRepository {

    // -------------------------------------------------------------
    // 1. DHIKR & DIGITAL TASBIH STATE
    // -------------------------------------------------------------
    private val _dhikrItems = MutableStateFlow(EcosystemData.dhikrList)
    val dhikrItems: StateFlow<List<DhikrItem>> = _dhikrItems.asStateFlow()

    private val _activeDhikr = MutableStateFlow(EcosystemData.dhikrList.first())
    val activeDhikr: StateFlow<DhikrItem> = _activeDhikr.asStateFlow()

    private val _todayTotalDhikrCount = MutableStateFlow(142)
    val todayTotalDhikrCount: StateFlow<Int> = _todayTotalDhikrCount.asStateFlow()

    fun selectDhikr(dhikr: DhikrItem) {
        _activeDhikr.value = dhikr
    }

    fun incrementDhikrCount() {
        val current = _activeDhikr.value
        val newCount = current.currentCount + 1
        val isDone = newCount >= current.targetCount

        _activeDhikr.update { it.copy(currentCount = newCount, isCompleted = isDone) }
        _dhikrItems.update { list ->
            list.map { if (it.id == current.id) it.copy(currentCount = newCount, isCompleted = isDone) else it }
        }
        _todayTotalDhikrCount.update { it + 1 }
    }

    fun resetActiveDhikrCount() {
        val current = _activeDhikr.value
        _activeDhikr.update { it.copy(currentCount = 0, isCompleted = false) }
        _dhikrItems.update { list ->
            list.map { if (it.id == current.id) it.copy(currentCount = 0, isCompleted = false) else it }
        }
    }

    fun setDhikrTarget(target: Int) {
        _activeDhikr.update { it.copy(targetCount = target) }
    }

    // -------------------------------------------------------------
    // 2. DUA LIBRARY STATE
    // -------------------------------------------------------------
    private val _duas = MutableStateFlow(EcosystemData.duaList)
    val duas: StateFlow<List<DuaItem>> = _duas.asStateFlow()

    private val _selectedDuaCategory = MutableStateFlow<DuaCategory?>(null)
    val selectedDuaCategory: StateFlow<DuaCategory?> = _selectedDuaCategory.asStateFlow()

    private val _duaSearchQuery = MutableStateFlow("")
    val duaSearchQuery: StateFlow<String> = _duaSearchQuery.asStateFlow()

    fun selectDuaCategory(category: DuaCategory?) {
        _selectedDuaCategory.value = category
    }

    fun setDuaSearchQuery(query: String) {
        _duaSearchQuery.value = query
    }

    fun toggleBookmarkDua(duaId: String) {
        _duas.update { list ->
            list.map { if (it.id == duaId) it.copy(isBookmarked = !it.isBookmarked) else it }
        }
    }

    // -------------------------------------------------------------
    // 3. RAMADAN MODE STATE
    // -------------------------------------------------------------
    private val _ramadanStats = MutableStateFlow(
        RamadanStats(
            daysCompleted = 12,
            totalDays = 30,
            pagesRecitedToday = 14,
            targetPagesPerDay = 20,
            tarawihCount = 11,
            charityDaysCount = 9
        )
    )
    val ramadanStats: StateFlow<RamadanStats> = _ramadanStats.asStateFlow()

    fun recordFastingDay() {
        _ramadanStats.update { it.copy(daysCompleted = (it.daysCompleted + 1).coerceAtMost(30)) }
    }

    fun incrementTarawih() {
        _ramadanStats.update { it.copy(tarawihCount = it.tarawihCount + 1) }
    }

    fun incrementKhatamPages(pages: Int = 1) {
        _ramadanStats.update { it.copy(pagesRecitedToday = it.pagesRecitedToday + pages) }
    }

    fun recordCharityDay() {
        _ramadanStats.update { it.copy(charityDaysCount = it.charityDaysCount + 1) }
    }

    // -------------------------------------------------------------
    // 4. HAJJ & UMRAH STATE
    // -------------------------------------------------------------
    private val _umrahSteps = MutableStateFlow(EcosystemData.umrahSteps)
    val umrahSteps: StateFlow<List<PilgrimageStep>> = _umrahSteps.asStateFlow()

    private val _hajjSteps = MutableStateFlow(EcosystemData.hajjDays)
    val hajjSteps: StateFlow<List<PilgrimageStep>> = _hajjSteps.asStateFlow()

    private val _selectedPilgrimageType = MutableStateFlow(PilgrimageType.UMRAH)
    val selectedPilgrimageType: StateFlow<PilgrimageType> = _selectedPilgrimageType.asStateFlow()

    fun selectPilgrimageType(type: PilgrimageType) {
        _selectedPilgrimageType.value = type
    }

    fun toggleStepCompleted(isUmrah: Boolean, stepNumber: Int) {
        if (isUmrah) {
            _umrahSteps.update { list ->
                list.map { if (it.stepNumber == stepNumber) it.copy(isCompleted = !it.isCompleted) else it }
            }
        } else {
            _hajjSteps.update { list ->
                list.map { if (it.stepNumber == stepNumber) it.copy(isCompleted = !it.isCompleted) else it }
            }
        }
    }

    fun incrementStepCounter(isUmrah: Boolean, stepNumber: Int) {
        if (isUmrah) {
            _umrahSteps.update { list ->
                list.map {
                    if (it.stepNumber == stepNumber && it.hasCounter) {
                        val next = (it.counterCurrent + 1).coerceAtMost(it.counterTarget)
                        it.copy(counterCurrent = next, isCompleted = next >= it.counterTarget)
                    } else it
                }
            }
        } else {
            _hajjSteps.update { list ->
                list.map {
                    if (it.stepNumber == stepNumber && it.hasCounter) {
                        val next = (it.counterCurrent + 1).coerceAtMost(it.counterTarget)
                        it.copy(counterCurrent = next, isCompleted = next >= it.counterTarget)
                    } else it
                }
            }
        }
    }

    fun resetStepCounter(isUmrah: Boolean, stepNumber: Int) {
        if (isUmrah) {
            _umrahSteps.update { list ->
                list.map {
                    if (it.stepNumber == stepNumber) it.copy(counterCurrent = 0, isCompleted = false) else it
                }
            }
        } else {
            _hajjSteps.update { list ->
                list.map {
                    if (it.stepNumber == stepNumber) it.copy(counterCurrent = 0, isCompleted = false) else it
                }
            }
        }
    }

    // -------------------------------------------------------------
    // 5. MASJID DISCOVERY STATE
    // -------------------------------------------------------------
    private val _masjids = MutableStateFlow(EcosystemData.masjidList)
    val masjids: StateFlow<List<MasjidItem>> = _masjids.asStateFlow()

    private val _masjidSearchQuery = MutableStateFlow("")
    val masjidSearchQuery: StateFlow<String> = _masjidSearchQuery.asStateFlow()

    fun setMasjidSearchQuery(query: String) {
        _masjidSearchQuery.value = query
    }

    fun toggleFavoriteMasjid(masjidId: String) {
        _masjids.update { list ->
            list.map { if (it.id == masjidId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    // -------------------------------------------------------------
    // 6. ISLAMIC CALENDAR & EVENTS
    // -------------------------------------------------------------
    val islamicMonths: List<HijriMonthData> = EcosystemData.islamicMonths
    val keyEvents: List<IslamicEvent> = EcosystemData.keyIslamicEvents
}
