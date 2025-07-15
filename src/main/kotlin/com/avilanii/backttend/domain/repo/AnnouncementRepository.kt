package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.Announcement

interface AnnouncementRepository {
    suspend fun getAnnouncements(eventId: Int): List<Announcement>
    suspend fun postAnnouncement(eventId: Int, title: String, description: String): Announcement
}