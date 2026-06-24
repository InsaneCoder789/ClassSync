package com.rochiee.classsync.domain.repository

import com.rochiee.classsync.data.remote.gmail.GmailMessageDto

interface GmailRepository {
    suspend fun fetchRecentAcademicMessages(): List<GmailMessageDto>
}