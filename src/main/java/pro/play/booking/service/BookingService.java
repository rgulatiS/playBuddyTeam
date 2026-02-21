package pro.play.booking.service;

import pro.play.booking.dto.BookingRequest;
import pro.play.booking.dto.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(BookingRequest req);
    boolean cancelBooking(Long bookingId, Long userId);
}

