package com.walmart.ticket.dto;


public class SeatBookingDTO {
    private String bookingCode;

    public SeatBookingDTO(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
}
