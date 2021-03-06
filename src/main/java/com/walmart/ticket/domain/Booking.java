package com.walmart.ticket.domain;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "BOOKING")
public class Booking extends BaseEntity {

    @OneToOne(mappedBy = "booking")
    private SeatHold seatHold;

    @Column(name = "NUMBER_OF_SEATS", nullable = false)
    private Integer numberOfSeats;

    @Digits(integer=8, fraction=2)
    @Column(name = "BOOKING_PRICE", precision = 8, scale = 2)
    private BigDecimal bookingPrice;

    @Column(name = "BOOKING_CODE", length = 500, nullable = false, updatable = false)
    private String bookingCode;

    @Column(name = "BOOKING_TIME", nullable = false)
    private Instant bookingTime;

    private Booking() {
        super();
    }

    @PrePersist
    private void preCreate() {
        this.bookingCode = UUID.randomUUID().toString();
        this.bookingTime = Instant.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.bookingTime = Instant.now();
    }

    public SeatHold getSeatHold() {
        return seatHold;
    }

    public void setSeatHold(SeatHold seatHold) {
        this.seatHold = seatHold;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public BigDecimal getBookingPrice() {
        return bookingPrice;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public Instant getBookingTime() {
        return bookingTime;
    }

    public static Booking newInstance(SeatHold seatHold) {
        Booking domain = new Booking();
        domain.seatHold = Objects.requireNonNull(seatHold, "Booking seats on hold is required");
        domain.numberOfSeats = seatHold.getNumberOfSeats();
        domain.bookingPrice = seatHold.getSeats().stream().map(Seat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        domain.bookingCode = UUID.randomUUID().toString();
        return domain;
    }

}
