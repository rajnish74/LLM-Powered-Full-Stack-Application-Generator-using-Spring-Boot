package com.rajnish.entity;

import com.rajnish.common.enums.PreviewStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Preview {

    Long id;

    Project project;

    PreviewStatus status;


    String namespace;
    String podName;
    String previewUrl;

    Instant startedAt;
    Instant terminatedAt;
    Instant createdAt;
}
