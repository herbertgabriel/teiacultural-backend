package com.proa.teiacultural.controller;

import com.proa.teiacultural.controller.dto.EventDto.EventDto;
import com.proa.teiacultural.controller.dto.EventDto.EventFeedItemDto;
import com.proa.teiacultural.entities.Event;
import com.proa.teiacultural.repository.EventRepository;
import com.proa.teiacultural.repository.UserRepository;
import com.proa.teiacultural.services.StoreFileService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class EventController {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final StoreFileService storeFileService;

    public EventController(UserRepository userRepository, EventRepository eventRepository, StoreFileService storeFileService) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.storeFileService = storeFileService;
    }

    @Transactional
    @PatchMapping(value = "/event", consumes = {"multipart/form-data"})
    public ResponseEntity<Void> patchEvent(@RequestParam(value = "title", required = false) String title,
                                           @RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "location", required = false) String location,
                                           @RequestParam(value = "category", required = false) String category,
                                           @RequestParam(value = "imageUrl", required = false) MultipartFile image,
                                           @RequestParam(value = "date", required = false) String date,
                                           @RequestParam(value = "eventWebSiteUrl", required = false) String eventWebSiteUrl,
                                           JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName())).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("premium"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have PREMIUM role");
        }

        var event = new Event();
        event.setUser(user);
        event.setTitle(title);
        event.setDescription(description);
        event.setLocation(location);
        event.setCategory(category);
        event.setDate(date);
        event.setEventWebSiteUrl(eventWebSiteUrl);

        if (image != null && !image.isEmpty() && isValidImageType(image.getContentType())) {
            event.setImageUrl(storeFileService.uploadFile(image, user.getUsername() + "/events", UUID.randomUUID().toString()));
        }

        eventRepository.save(event);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/events")
    public ResponseEntity<EventDto> getEvents(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        var eventsPage = eventRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "date"));
        var eventItems = eventsPage.getContent().stream().map(event -> new EventFeedItemDto(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getCategory(),
                event.getImageUrl(),
                event.getDate(),
                event.getEventWebSiteUrl(),
                event.getUser().getUsername()
        )).collect(Collectors.toList());

        var dto = new EventDto(
                eventItems,
                page,
                pageSize,
                eventsPage.getTotalPages(),
                eventsPage.getTotalElements()
        );

        return ResponseEntity.ok(dto);
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/png") || contentType.equals("image/jpeg") || contentType.equals("image/svg+xml");
    }

    @DeleteMapping("/event/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable long id, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName())).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        var event = eventRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (!event.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        eventRepository.delete(event);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PutMapping("/events/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable Long id,
                                            @RequestParam(value = "title", required = false) String title,
                                            @RequestParam(value = "description", required = false) String description,
                                            @RequestParam(value = "location", required = false) String location,
                                            @RequestParam(value = "category", required = false) String category,
                                            @RequestParam(value = "imageUrl", required = false) MultipartFile image,
                                            @RequestParam(value = "date", required = false) String date,
                                            @RequestParam(value = "eventWebSiteUrl", required = false) String eventWebSiteUrl,
                                            JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName())).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        var event = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!event.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the owner of the event");
        }

        event.setTitle(title);
        event.setDescription(description);
        event.setLocation(location);
        event.setCategory(category);
        event.setDate(date);
        event.setEventWebSiteUrl(eventWebSiteUrl);

        if (image != null && !image.isEmpty() && isValidImageType(image.getContentType())) {
            // Delete old image from S3
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                storeFileService.deleteFile(event.getImageUrl());
            }
            // Upload new image to S3
            event.setImageUrl(storeFileService.uploadFile(image, user.getUsername() + "/events", UUID.randomUUID().toString()));
        }

        eventRepository.save(event);
        return ResponseEntity.ok().build();
    }

}