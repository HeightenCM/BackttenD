package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.Announcement
import com.avilanii.backttend.infrastructure.repo.EventAnnouncementRepositoryImpl

class AnnouncementService(
    private val announcementRepository: EventAnnouncementRepositoryImpl
) {
    suspend fun getAnnouncements(eventId: Int): List<Announcement>{
        return announcementRepository.getAnnouncements(eventId)
    }
    suspend fun postAnnouncement(eventId: Int, title: String, description: String): Announcement{
        return announcementRepository.postAnnouncement(eventId, title, description)
    }
}