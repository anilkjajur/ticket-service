package com.walmart.ticket.dto;


public class SeatAvailabilityDTO {

    private int availableSeats;

    public SeatAvailabilityDTO(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
