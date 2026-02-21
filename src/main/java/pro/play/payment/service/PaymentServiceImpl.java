package pro.play.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.play.booking.model.Booking;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${app.stripe.secret:}")
    private String stripeSecret;

    @Override
    public PaymentResult createPayment(Booking booking) {
        // No external Stripe SDK available in this environment — return a sandbox payment id.
        String id = "sandbox_payment_" + (booking != null && booking.getId() != null ? booking.getId() : System.currentTimeMillis());
        return new PaymentResult(id, null);
    }

    @Override
    public boolean refund(String providerPaymentId) {
        // Sandbox: pretend refund succeeded
        return true;
    }
}
