package pro.play.payment.service;

import pro.play.booking.model.Booking;

public interface PaymentService {
    /**
     * Create a sandbox payment for the booking and return provider details
     */
    PaymentResult createPayment(Booking booking);

    /**
     * Refund a payment by provider id (if supported). Return true if refund initiated.
     */
    boolean refund(String providerPaymentId);
}
