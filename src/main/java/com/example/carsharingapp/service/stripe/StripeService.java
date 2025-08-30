package com.example.carsharingapp.service.stripe;

import com.example.carsharingapp.model.rental.Rental;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;

public interface StripeService {
    Session createRentalPaymentSession(Rental rental, BigDecimal amount);
    String checkPaymentStatus(String sessionId);
}

