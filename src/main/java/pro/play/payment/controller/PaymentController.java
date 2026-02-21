package pro.play.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.booking.model.Booking;
import pro.play.booking.repository.BookingRepository;
import pro.play.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;

    @PostMapping
    public ResponseEntity<String> createPayment(@RequestParam Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return ResponseEntity.notFound().build();
        String providerId = paymentService.createPayment(booking);
        return ResponseEntity.ok(providerId);
    }
}

