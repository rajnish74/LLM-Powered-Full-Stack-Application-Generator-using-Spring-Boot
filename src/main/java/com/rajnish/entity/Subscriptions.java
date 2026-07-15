package com.rajnish.entity;

import com.rajnish.common.enums.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Subscriptions {

    Long id;
    User user;

    Plan plan;

    String stripeCustomerId;
    String stripeSubscriptionId;

    SubscriptionStatus status;

    Instant currentPeriodStart;
    Instant currentPeriodEnd;
    Boolean cancelPeriodEnd = false;

    Instant createdAt;
    Instant updatedAt;

}
