package pro.play.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.booking.model.Booking;
import pro.play.booking.repository.BookingRepository;
import pro.play.payment.service.PaymentResult;
import pro.play.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;

    @PostMapping
    public ResponseEntity<PaymentResult> createPayment(@RequestParam Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return ResponseEntity.notFound().build();
        PaymentResult result = paymentService.createPayment(booking);
        if (result != null && result.getProviderId() != null) {
            booking.setPaymentProviderId(result.getProviderId());
            booking.setPaymentStatus(pro.play.booking.model.PaymentStatus.PAID);
            booking.setStatus(pro.play.booking.model.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }
        return ResponseEntity.ok(result);
    }
}
