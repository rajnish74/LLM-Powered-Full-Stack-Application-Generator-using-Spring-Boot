package com.rajnish.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ChatSession {

    Project project;
    User user;

    String title;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}
