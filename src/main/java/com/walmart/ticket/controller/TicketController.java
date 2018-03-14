package com.walmart.ticket.controller;

import com.walmart.ticket.assembler.SeatHoldAssembler;
import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.dto.*;
import com.walmart.ticket.service.TicketService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@Api(value="ticket-service", description="Operations pertaining to ticket reservation for the Venue")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @ApiOperation(value = "Get all available seats", response = SeatAvailabilityDTO.class)
    @GetMapping(value = "/v1/venue/seats", produces = "application/json")
    public SeatAvailabilityDTO allAvailableSeats() {
        return new SeatAvailabilityDTO(ticketService.numSeatsAvailable());
    }

    @ApiOperation(value = "Hold seats for the customer using customer email address", response = SeatHoldDTO.class)
    @PostMapping(value = "/v1/venue/seats/hold", produces = "application/json")
    public SeatHoldDTO holdSeats(@RequestBody SeatHoldRequest seatHoldRequest) {

        SeatHold seatHold = ticketService.findAndHoldSeats(seatHoldRequest.getNumSeats(),
                seatHoldRequest.getCustomerEmail());

        return new SeatHoldAssembler().mapToResponse(seatHold);
    }

    @ApiOperation(value = "Reserve seats for the customer using customer email address", response = SeatBookingDTO.class)
    @PostMapping(value = "/v1/venue/seats/reserve", produces = "application/json")
    public SeatBookingDTO reserveSeats(@RequestBody SeatReserveDTO seatReserveDTO) {

        String bookingCode = ticketService.reserveSeats(seatReserveDTO.getSeatHoldId(),
                seatReserveDTO.getCustomerEmail());

        return new SeatBookingDTO(bookingCode);
    }
}
