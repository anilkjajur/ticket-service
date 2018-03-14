package com.walmart.ticket.exception;


public class SeatHoldNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -60724728268211769L;
    private final Long id;

    public SeatHoldNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
