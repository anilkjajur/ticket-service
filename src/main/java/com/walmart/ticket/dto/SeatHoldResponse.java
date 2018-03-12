package com.walmart.ticket.dto;


import java.time.Instant;
import java.util.Set;

public class SeatHoldResponse {

    private Long seatHoldId;
    private String customerEmail;
    private Set<SeatDTO> seats;
    private Instant holdingTime;
    private Instant expirationTime;

    public void setSeatHoldId(Long seatHoldId) {
        this.seatHoldId = seatHoldId;
    }

    public Long getSeatHoldId() {
        return seatHoldId;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Set<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(Set<SeatDTO> seats) {
        this.seats = seats;
    }

    public void setHoldingTime(Instant holdingTime) {
        this.holdingTime = holdingTime;
    }

    public Instant getHoldingTime() {
        return holdingTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }
}
