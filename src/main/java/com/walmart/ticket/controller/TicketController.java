package com.walmart.ticket.controller;

import com.walmart.ticket.assembler.SeatHoldAssembler;
import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.dto.SeatHoldRequest;
import com.walmart.ticket.dto.SeatHoldResponse;
import com.walmart.ticket.dto.SeatReserveRequest;
import com.walmart.ticket.service.TicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping(value = "/v1/venue/seats")
    public int allAvailableSeats() {
        return ticketService.numSeatsAvailable();
    }

    @PostMapping(value = "/v1/venue/seats/hold")
    public SeatHoldResponse holdSeats(@RequestBody SeatHoldRequest seatHoldRequest) {

        SeatHold seatHold = ticketService.findAndHoldSeats(seatHoldRequest.getNumSeats(),
                seatHoldRequest.getCustomerEmail());

        return new SeatHoldAssembler().mapToResponse(seatHold);
    }

    @PostMapping(value = "/v1/venue/seats/reserve")
    public String reserveSeats(@RequestBody SeatReserveRequest seatReserveRequest) {

        return ticketService.reserveSeats(seatReserveRequest.getSeatHoldId(),
                seatReserveRequest.getCustomerEmail());

    }
}
