package com.walmart.ticket.domain;

import com.walmart.ticket.exception.SeatHoldNotFoundException;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Entity
@Table(name = "SEAT_HOLD")
public class SeatHold extends BaseEntity implements Serializable {

    private static final long EXPIRATION_SECONDS = 60L;

    @Column(name = "HOLDING_TIME")
    private Instant holdingTime;

    @Column(name = "NUMBER_OF_SEATS", nullable = false)
    private int numberOfSeats;

    @OneToMany(mappedBy = "seatHold", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seat> seats;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private SeatHold() {
        super();
    }

    @PrePersist
    private void preCreate() {
        this.holdingTime = Instant.now();
    }

    @PostLoad
    private void postLoad() {
        if (seats == null) {
            seats = new HashSet<>();
        }
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public Instant getHoldingTime() {
        return holdingTime;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setHoldingTime(Instant holdingTime) {
        this.holdingTime = holdingTime;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Optional<Booking> getBooking() {
        return Optional.ofNullable(booking);
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public static SeatHold newInstance(Customer customer, Venue venue, int holdSeats) {
        SeatHold domain = new SeatHold();
        domain.venue = venue;
        domain.customer = customer;
        domain.numberOfSeats = holdSeats;
        domain.seats = createSeatsToHold(holdSeats, venue, domain);
        return domain;
    }

    private static Set<Seat> createSeatsToHold(int holdSeats, Venue venue, SeatHold seatHold) {
        int availableSeats = venue.getAvailableSeats();

        if (availableSeats < holdSeats) {
            throw new IllegalArgumentException("Not enough seats are makeAvailable to hold seats " + holdSeats);
        }

        Set<Seat> seats = IntStream.range(0, holdSeats)
                .mapToObj(i -> createSeatsToHold(venue, seatHold))
                .collect(Collectors.toSet());

        seatHold.addSeats(seats);
        venue.addSeats(seats);
        return seats;
    }

    private static Seat createSeatsToHold(Venue venue, SeatHold seatHold) {
        return Seat.newInstance()
                .seatHold(seatHold)
                .venue(venue)
                .status(Status.ON_HOLD)
                .build();
    }


    public boolean isOnHoldExpired() {
        Instant now = Instant.now().minusSeconds(EXPIRATION_SECONDS);
        return hasSeatsOnHold() && now.isAfter(holdingTime);
    }

    private boolean hasSeatsOnHold() {
        return this.seats.stream().anyMatch(Seat::isOnHold);
    }

    public String getCustomerEmail() {
        return customer.getCustomerEmail();
    }

    public void addSeats(Set<Seat> seats) {
        if (seats == null) {
            this.seats = new HashSet<>();
        }
        else {
            this.seats = seats;
        }
    }

    public Instant getExpirationTime() {
        return holdingTime.plusSeconds(EXPIRATION_SECONDS);
    }

    public void validateCustomerEmail(String customerEmail) {
        if (!customer.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
            throw new SeatHoldNotFoundException(getId(), "Customer email is not valid for seatHoldId");
        }
    }

    public void validateBooking() {
        if (getBooking().isPresent()) {
            throw new IllegalArgumentException("Seat hold id already booked by the customer");
        }
    }

    public void addBooking() {
        this.booking = Booking.newInstance(this);
    }

    public void makeReservedSeats() {
        getSeats().forEach(Seat::makeReserved);
    }

    public Optional<String> getBookingCode() {
        return getBooking().map(Booking::getBookingCode);
    }

    public void removeSeats() {
        venue.removeSeats(seats);
        seats.clear();
    }
}
