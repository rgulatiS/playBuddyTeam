package pro.play.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.play.availability.model.Availability;
import pro.play.availability.model.AvailabilityRule;
import pro.play.availability.repository.AvailabilityRepository;
import pro.play.availability.repository.AvailabilityRuleRepository;
import pro.play.booking.dto.BookingRequest;
import pro.play.booking.dto.BookingResponse;
import pro.play.booking.model.Booking;
import pro.play.booking.model.BookingStatus;
import pro.play.booking.model.PaymentStatus;
import pro.play.booking.repository.BookingRepository;
import pro.play.court.model.Court;
import pro.play.court.repository.CourtRepository;
import pro.play.payment.service.PaymentResult;
import pro.play.payment.service.PaymentService;
import pro.play.user.model.User;
import pro.play.user.repository.UserRepository;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements pro.play.booking.service.BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityRuleRepository availabilityRuleRepository;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest req) {
        // Lock court for update to avoid concurrent bookings
        Court court = courtRepository.findByIdForUpdate(req.getCourtId())
                .orElseThrow(() -> new IllegalArgumentException("Court not found"));

        // Validate user
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check availability slots for date
        List<Availability> slots = availabilityRepository.findByCourtIdAndDate(court.getId(), req.getDate());
        boolean slotOk = false;
        if (!slots.isEmpty()) {
            for (Availability s : slots) {
                if (s.isAvailable() && (!req.getStartTime().isBefore(s.getStartTime()))
                        && (!req.getEndTime().isAfter(s.getEndTime()))) {
                    slotOk = true;
                    break;
                }
            }
            if (!slotOk)
                throw new IllegalArgumentException("Court not available for requested time (by availability slots)");
        } else {
            // Fallback to availability rules
            DayOfWeek dow = req.getDate().getDayOfWeek();
            List<AvailabilityRule> rules = availabilityRuleRepository.findByCourtIdAndDayOfWeek(court.getId(), dow);
            for (AvailabilityRule r : rules) {
                if ((!req.getStartTime().isBefore(r.getStartTime())) && (!req.getEndTime().isAfter(r.getEndTime()))) {
                    slotOk = true;
                    break;
                }
            }
            if (!slotOk)
                throw new IllegalArgumentException("Court not available for requested time (by rules)");
        }

        // Check overlapping bookings
        List<Booking> overlapping = bookingRepository.findOverlapping(court.getId(), req.getDate(), req.getStartTime(),
                req.getEndTime());
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Time slot already booked");
        }

        Booking b = Booking.builder()
                .court(court)
                .user(user)
                .bookedBy(req.getBookedBy())
                .bookedById(req.getBookedById())
                .date(req.getDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(BookingStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(b);

        // Create payment (synchronous here)
        PaymentResult pr = paymentService.createPayment(saved);
        if (pr != null && pr.getProviderId() != null) {
            saved.setPaymentProviderId(pr.getProviderId());
            saved.setPaymentStatus(PaymentStatus.PAID);
            saved.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(saved);
            return new BookingResponse(saved.getId(), saved.getStatus(), saved.getPaymentStatus(),
                    saved.getPaymentProviderId(), pr.getClientSecret());
        } else {
            saved.setPaymentStatus(PaymentStatus.FAILED);
            saved.setStatus(BookingStatus.FAILED);
            bookingRepository.save(saved);
            return new BookingResponse(saved.getId(), saved.getStatus(), saved.getPaymentStatus(), null, null);
        }
    }

    @Override
    @Transactional
    public boolean cancelBooking(Long bookingId, Long userId) {
        Booking b = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        // allow cancellation if userId matches booking.bookedById or booking.user.id
        boolean allowed = (b.getBookedById() != null && b.getBookedById().equals(userId))
                || (b.getUser() != null && b.getUser().getId() != null && b.getUser().getId().equals(userId));
        if (!allowed)
            throw new IllegalArgumentException("Not authorized to cancel this booking");

        b.setStatus(BookingStatus.CANCELLED);
        if (b.getPaymentProviderId() != null && b.getPaymentStatus() == PaymentStatus.PAID) {
            boolean refunded = paymentService.refund(b.getPaymentProviderId());
            b.setPaymentStatus(refunded ? PaymentStatus.REFUNDED : PaymentStatus.FAILED);
        }
        bookingRepository.save(b);
        return true;
    }
}
