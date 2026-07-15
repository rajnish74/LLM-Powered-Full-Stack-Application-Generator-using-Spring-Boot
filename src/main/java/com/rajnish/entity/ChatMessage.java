package com.rajnish.entity;

import com.rajnish.common.enums.MessageRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ChatMessage {

    Long id;
    ChatSession chatSession;

    String content;
    MessageRole role;
    String toolCalls;

    Integer tokensUsed;

    Instant createdAt;
}
