package com.walmart.ticket.dto;


import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class SeatHoldDTO {

    private Long seatHoldId;
    private String customerEmail;
    private int numberOfSeats;
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

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
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

    public void addSeat(SeatDTO seatDTO) {
        if (seats == null) {
            seats = new HashSet<>();
        }
        seats.add(seatDTO);
    }
}
