package com.rajnish.dto.file;

import java.time.Instant;

public record FileNode(
        String path,
        Instant modifiedAt,
        String size,
        String type
) {
}
