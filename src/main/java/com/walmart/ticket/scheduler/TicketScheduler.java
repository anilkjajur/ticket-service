package com.walmart.ticket.scheduler;

import com.walmart.ticket.domain.Seat;
import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.repository.SeatHoldRepository;
import com.walmart.ticket.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TicketScheduler {

    @Autowired
    SeatHoldRepository seatHoldRepository;

    @Autowired
    SeatRepository seatRepository;

    /**
     *  Execute the ticket scheduler task for every 5 seconds
     */
    @Scheduled(fixedRate = 5000)
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void removeExpiredSeatsOnHold() {

        Set<SeatHold> expiredHolds = seatHoldRepository.findAll().stream()
                .filter(SeatHold::isOnHoldExpired)
                .collect(Collectors.toSet());

        expiredHolds.forEach(this::removeSeats);

        if (!expiredHolds.isEmpty()) {
            seatHoldRepository.deleteAll(expiredHolds);
        }
    }

    private void removeSeats(SeatHold seatHold) {
        Set<Seat> seats = seatHold.getSeats();
        seatHold.removeSeats();

        seatRepository.deleteAll(seats);
    }
}
