package com.proa.teiacultural.controller.dto.EventDto;


import java.util.List;

public record EventDto(List<EventFeedItemDto> feedItems, int page, int pageSize, int totalPages, long totalElements) {
}