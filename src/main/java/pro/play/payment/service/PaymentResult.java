package pro.play.payment.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResult {
    private String providerId; // e.g., PaymentIntent id
    private String clientSecret; // client secret for confirming payment (if applicable)
}

