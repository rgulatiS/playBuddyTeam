package pro.play.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.play.booking.dto.BookingRequest;
import pro.play.booking.dto.BookingResponse;
import pro.play.booking.dto.CancelRequest;
import pro.play.booking.dto.WalkInRequest;
import pro.play.booking.model.Booking;
import pro.play.booking.model.BookingStatus;
import pro.play.booking.repository.BookingRepository;
import pro.play.booking.service.BookingService;
import pro.play.user.model.User;
import pro.play.user.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Booking>> list() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody Booking b) {
        // NOTE: real implementation should check availability and create payment
        b.setStatus(BookingStatus.PENDING);
        return ResponseEntity.ok(bookingRepository.save(b));
    }

    @PostMapping("/create")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest req) {
        BookingResponse resp = bookingService.createBooking(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/walkin")
    public ResponseEntity<BookingResponse> walkIn(@Valid @RequestBody WalkInRequest req) {
        // find or create user by mobile or email
        User user = null;
        if (req.getCustomerEmail() != null && !req.getCustomerEmail().isBlank()) {
            user = userRepository.findByEmail(req.getCustomerEmail()).orElse(null);
        }
        if (user == null && req.getCustomerMobile() != null && !req.getCustomerMobile().isBlank()) {
            user = userRepository.findByMobileNumber(req.getCustomerMobile()).orElse(null);
        }
        if (user == null) {
            User u = User.builder()
                    .name(req.getCustomerName())
                    .email(req.getCustomerEmail())
                    .mobileNumber(req.getCustomerMobile())
                    .role(pro.play.user.model.Role.USER)
                    .build();
            user = userRepository.save(u);
        }

        BookingRequest br = new BookingRequest();
        br.setCourtId(req.getCourtId());
        br.setUserId(user.getId());
        br.setBookedBy(req.getBookedBy());
        br.setBookedById(req.getBookedById());
        br.setDate(req.getDate());
        br.setStartTime(req.getStartTime());
        br.setEndTime(req.getEndTime());
        br.setAmountCents(req.getAmountCents());

        BookingResponse resp = bookingService.createBooking(br);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancel(@Valid @RequestBody CancelRequest req) {
        boolean ok = bookingService.cancelBooking(req.getBookingId(), req.getUserId());
        if (ok) return ResponseEntity.noContent().build();
        return ResponseEntity.status(403).build();
    }
}
