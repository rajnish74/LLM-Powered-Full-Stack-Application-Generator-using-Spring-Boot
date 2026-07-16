package com.rajnish.service;

import com.rajnish.dto.subscriptions.CheckoutRequest;
import com.rajnish.dto.subscriptions.CheckoutResponse;
import com.rajnish.dto.subscriptions.PortalResponse;
import com.rajnish.dto.subscriptions.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId);

    PortalResponse openCustomerPortal(Long userId);
}
