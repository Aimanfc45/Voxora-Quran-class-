package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.mock.MockQuranData
import com.example.data.model.ClassType
import com.example.data.repository.VoxoraRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Voxora Quran", appName)
    }

    @Test
    fun `verify repository initial state and bookmarking`() {
        val repository = VoxoraRepository()
        assertNotNull(repository.userProfile.value)
        assertEquals("Ahmed Al-Farsi", repository.userProfile.value.name)

        val surah = MockQuranData.surahList.first()
        val verse = surah.verses.first()

        val initialCount = repository.bookmarks.value.size
        val added = repository.toggleBookmark(surah, verse)
        assertTrue(added)
        assertEquals(initialCount + 1, repository.bookmarks.value.size)
        assertTrue(repository.isVerseBookmarked(surah.number, verse.verseNumber))

        // Toggle again to remove
        val removed = repository.toggleBookmark(surah, verse)
        assertFalse(removed)
        assertFalse(repository.isVerseBookmarked(surah.number, verse.verseNumber))
    }

    @Test
    fun `verify live classroom controls state changes`() {
        val repository = VoxoraRepository()
        val initialMic = repository.isMyMicMuted.value
        repository.toggleMyMic()
        assertEquals(!initialMic, repository.isMyMicMuted.value)

        assertFalse(repository.isMyHandRaised.value)
        repository.toggleRaiseHand()
        assertTrue(repository.isMyHandRaised.value)
    }

    @Test
    fun `verify community post interaction and commenting`() {
        val repository = VoxoraRepository()
        val initialPostCount = repository.posts.value.size
        repository.createPost("Test recitation post", "Surah Al-Fatihah", "Global Quran Learners")
        assertEquals(initialPostCount + 1, repository.posts.value.size)

        val newPost = repository.posts.value.first()
        assertEquals("Test recitation post", newPost.content)

        repository.addCommentToPost(newPost.id, "MashaAllah brother!")
        val updatedPost = repository.posts.value.find { it.id == newPost.id }
        assertNotNull(updatedPost)
        assertEquals(1, updatedPost?.commentsCount)
    }
}
