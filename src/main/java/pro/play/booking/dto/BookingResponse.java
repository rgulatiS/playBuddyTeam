package pro.play.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import pro.play.booking.model.BookingStatus;
import pro.play.booking.model.PaymentStatus;

@Data
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String paymentProviderId;
    private String paymentClientSecret;
}

