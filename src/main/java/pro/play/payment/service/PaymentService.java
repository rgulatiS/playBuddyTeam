package pro.play.payment.service;

import pro.play.booking.model.Booking;

public interface PaymentService {
    /**
     * Create a sandbox payment for the booking and return a provider id or URL
     */
    String createPayment(Booking booking);
}

