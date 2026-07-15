package com.rajnish.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageLog {

    Long id;
    User user;
    Project project;

    String action;
    Integer tokensUsed;
    Integer durationMs;

    String metadata; //JSON of{model used, prompt used}
    Instant createdAt;
}
