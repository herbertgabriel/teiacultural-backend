package com.proa.teiacultural.controller.dto.EventDto;

public record EventFeedItemDto(
        long eventId,
        String title,
        String description,
        String location,
        String category,
        String imageUrl,
        String date,
        String eventWebSiteUrl,
        String username
) {}