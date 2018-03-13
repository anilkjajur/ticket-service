package com.walmart.ticket.controller;

import com.walmart.ticket.assembler.SeatHoldAssembler;
import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.dto.SeatHoldRequest;
import com.walmart.ticket.dto.SeatHoldResponse;
import com.walmart.ticket.dto.SeatReserveRequest;
import com.walmart.ticket.dto.SeatReserveResponse;
import com.walmart.ticket.service.TicketService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api")
@Api(value="ticket-service", description="Operations pertaining to ticket reservation for the Venue")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @ApiOperation(value = "Get all available seats", response = Integer.class)
    @GetMapping(value = "/v1/venue/seats", produces = "application/json")
    public int allAvailableSeats() {
        return ticketService.numSeatsAvailable();
    }

    @ApiOperation(value = "Hold seats for the customer using customer email address", response = SeatHoldResponse.class)
    @PostMapping(value = "/v1/venue/seats/hold", produces = "application/json")
    public SeatHoldResponse holdSeats(@RequestBody SeatHoldRequest seatHoldRequest) {

        SeatHold seatHold = ticketService.findAndHoldSeats(seatHoldRequest.getNumSeats(),
                seatHoldRequest.getCustomerEmail());

        return new SeatHoldAssembler().mapToResponse(seatHold);
    }

    @ApiOperation(value = "Reserve seats for the customer using customer email address", response = SeatReserveResponse.class)
    @PostMapping(value = "/v1/venue/seats/reserve", produces = "application/json")
    public SeatReserveResponse reserveSeats(@RequestBody SeatReserveRequest seatReserveRequest) {

        String bookingCode = ticketService.reserveSeats(seatReserveRequest.getSeatHoldId(),
                seatReserveRequest.getCustomerEmail());

        return new SeatReserveResponse(bookingCode);
    }
}
