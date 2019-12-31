package com.codingblocks.cbonlineapp.mycourse.player

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.NotesDao
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class VideoPlayerRepository(
    private val doubtsDao: DoubtsDao,
    private val notesDao: NotesDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val contentDao: ContentDao
) {
    suspend fun fetchCourseNotes(attemptId: String) = safeApiCall { Clients.onlineV2JsonApi.getNotesByAttemptId(attemptId) }

    suspend fun insertNotes(notes: List<Note>) {
        notes.forEach {
            val contentTitle = GlobalScope.async { contentDao.getContentTitle(it.content?.id ?: "") }
            val model = NotesModel(
                it.id,
                it.duration,
                it.text,
                it.content?.id ?: "",
                it.runAttempt?.id ?: "",
                it.createdAt,
                it.deletedAt,
                contentTitle.await()
            )
            notesDao.insert(model)
        }
    }

    fun getNotes(attemptId: String) = notesDao.getNotes(attemptId)
}