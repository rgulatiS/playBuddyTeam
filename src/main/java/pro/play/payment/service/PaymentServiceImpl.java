package pro.play.payment.service;

import org.springframework.stereotype.Service;
import pro.play.booking.model.Booking;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String createPayment(Booking booking) {
        // Stubbed: integrate Stripe SDK later. Return a fake sandbox payment id.
        return "sandbox_payment_" + (booking != null && booking.getId() != null ? booking.getId() : 0);
    }
}
