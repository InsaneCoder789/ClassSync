package com.rochiee.classsync.data.repository

import com.rochiee.classsync.data.remote.gmail.GmailMessageDto
import com.rochiee.classsync.data.remote.gmail.GmailRemoteDataSource
import com.rochiee.classsync.domain.repository.GmailRepository

class GmailRepositoryImpl(
    private val remoteDataSource: GmailRemoteDataSource
) : GmailRepository {
    override suspend fun fetchRecentAcademicMessages(): List<GmailMessageDto> {
        return remoteDataSource.fetchRecentAcademicMessages()
    }
}