package com.walmart.ticket.service;

import com.walmart.ticket.domain.*;
import com.walmart.ticket.repository.CustomerRepository;
import com.walmart.ticket.repository.SeatHoldRepository;
import com.walmart.ticket.repository.SeatRepository;
import com.walmart.ticket.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
public class TicketServiceImpl implements TicketService {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private CustomerRepository customerRespository;

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public int numSeatsAvailable() {
        return getVenueByID()
                .getAvailableSeats();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        Venue venue = getVenueByID();

        int availableSeats = venue.getAvailableSeats();

        if (availableSeats <= 0 || availableSeats < numSeats) {
            throw new IllegalStateException("Not enough seats are makeAvailable for holding seats: " + numSeats);
        }

        Customer customer = getOrCreateCustomerForCustomerEmail(customerEmail);

        SeatHold seatHold = SeatHold.newInstance(customer, venue);
        createSeatsToHold(numSeats, venue, seatHold);
        seatHoldRepository.save(seatHold);

        return seatHold;
    }

    private Set<Seat> createSeatsToHold(int holdSeats, Venue venue, SeatHold seatHold) {
        int availableSeats = venue.getAvailableSeats();

        if (availableSeats < holdSeats) {
            throw new IllegalStateException("Not enough seats are makeAvailable to hold seats " + holdSeats);
        }

        Set<Seat> seats = IntStream.range(0, holdSeats)
                .mapToObj(i -> createSeatsToHold(venue, seatHold))
                .collect(Collectors.toSet());

        seatHold.addSeats(seats);
        venue.addSeats(seats);
        return seats;
    }

    private Seat createSeatsToHold(Venue venue, SeatHold seatHold) {
        return Seat.newInstance()
                .seatHold(seatHold)
                .venue(venue)
                .status(Status.ON_HOLD)
                .build();
    }

    private Customer getOrCreateCustomerForCustomerEmail(String customerEmail) {
        Customer customer = customerRespository.findByCustomerEmail(customerEmail)
                .orElse(new Customer(customerEmail));

        customerRespository.save(customer);
        return customer;
    }

    private Venue getVenueByID() {
        return venueRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Venue is not found"));
    }

    @Override
    public String reserveSeats(String seatHoldId, String customerEmail) {

        SeatHold seatHold = seatHoldRepository.findById(Long.valueOf(seatHoldId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid seatHoldId: " + seatHoldId));

        seatHold.validateCustomerEmail(customerEmail);
        seatHold.validateBooking();
        seatHold.addBooking();
        seatHold.makeReservedSeats();

        seatHoldRepository.save(seatHold);

        return seatHold.getBookingCode()
                .orElseThrow(() -> new IllegalStateException("Unable to find booking code"));
    }
}
