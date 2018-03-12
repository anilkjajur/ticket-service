package com.walmart.ticket.assembler;

import com.walmart.ticket.domain.Seat;
import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.dto.SeatDTO;
import com.walmart.ticket.dto.SeatHoldResponse;

import java.util.stream.Collectors;

public class SeatHoldAssembler {

    public SeatHoldResponse mapToResponse(SeatHold seatHold) {
        SeatHoldResponse response = new SeatHoldResponse();
        response.setSeatHoldId(seatHold.getId());
        response.setCustomerEmail(seatHold.getCustomerEmail());
        response.setHoldingTime(seatHold.getHoldingTime());
        response.setExpirationTime(seatHold.getExpirationTime());
        response.setSeats(seatHold.getSeats().stream()
                .map(this::mapSeat)
                .collect(Collectors.toSet()));
        return response;
    }

    private SeatDTO mapSeat(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getId());
        dto.setStatus(seat.getStatus());
        dto.setPrice(seat.getPrice());
        return dto;
    }

}
