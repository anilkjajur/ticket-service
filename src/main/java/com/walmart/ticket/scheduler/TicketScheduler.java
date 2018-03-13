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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TicketScheduler {

    @Autowired
    SeatHoldRepository seatHoldRepository;

    @Autowired
    SeatRepository seatRepository;

    /**
     *  Execute the ticket scheduler task for every 30 seconds
     */
    @Scheduled(fixedRate = 30000)
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void removeExpiredSeatsOnHold() {
        List<SeatHold> holds = seatHoldRepository.findAll();

        Set<SeatHold> expiredHolds = holds.stream()
                .filter(SeatHold::isOnHoldExpired)
                .collect(Collectors.toSet());

        expiredHolds.forEach(this::removeSeats);

        seatHoldRepository.deleteAll(expiredHolds);
    }

    private void removeSeats(SeatHold seatHold) {
        Set<Seat> seats = seatHold.getSeats();
        seatHold.removeSeats();

        seatRepository.deleteAll(seats);
    }
}
