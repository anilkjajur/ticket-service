package com.walmart.ticket.dto;


public class SeatReserveDTO {
    private Long seatHoldId;
    private String customerEmail;

    public Long getSeatHoldId() {
        return seatHoldId;
    }

    public void setSeatHoldId(Long seatHoldId) {
        this.seatHoldId = seatHoldId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
