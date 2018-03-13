package com.walmart.ticket.dto;


public class SeatReserveResponse {
    private String bookingCode;

    public SeatReserveResponse(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
}
