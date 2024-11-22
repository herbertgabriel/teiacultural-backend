package com.proa.teiacultural.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tb_event")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "event_id")
    private long eventId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    private String location;
    private String category;
    private String imageUrl;

    @Column(nullable = false)
    private String date;

    private String eventWebSiteUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
