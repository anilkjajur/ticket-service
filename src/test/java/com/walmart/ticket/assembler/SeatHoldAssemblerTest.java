package com.walmart.ticket.assembler;

import com.walmart.ticket.domain.*;
import com.walmart.ticket.dto.SeatDTO;
import com.walmart.ticket.dto.SeatHoldDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SeatHoldAssemblerTest {

    private SeatHoldAssembler classToTest = new SeatHoldAssembler();

    @Test
    public void mapToResponse() {
        //given
        SeatHold seatHold = createSeatHold();

        //when
        SeatHoldDTO dto = classToTest.mapToResponse(seatHold);

        //then
        assertEquals(dto.getSeatHoldId(), seatHold.getId());
        assertEquals(dto.getCustomerEmail(), seatHold.getCustomerEmail());
        assertEquals(dto.getNumberOfSeats(), seatHold.getNumberOfSeats());
        assertEquals(dto.getHoldingTime(), seatHold.getHoldingTime());
        assertEquals(dto.getExpirationTime(), seatHold.getExpirationTime());

        assertEquals(dto.getSeats().size(), seatHold.getSeats().size());

        Map<Long, SeatDTO> seatsDTOMap = dto.getSeats().stream().collect(Collectors.toMap(SeatDTO::getSeatId, c -> c));
        Map<Long, Seat> seatMap = seatHold.getSeats().stream().collect(Collectors.toMap(Seat::getId, c -> c));

        seatMap.keySet().forEach(s -> {
            Seat seat = seatMap.get(s);
            SeatDTO seatDTO = seatsDTOMap.get(s);

            compareSeat(seat, seatDTO);
        });
    }

    private void compareSeat(Seat seat, SeatDTO dto) {
        assertEquals(dto.getSeatId(), seat.getId());
        assertEquals(dto.getPrice(), seat.getPrice());
        assertEquals(dto.getStatus(), seat.getStatus());
    }

    private SeatHold createSeatHold() {
        Customer customer = new Customer("abc@xyz.com");
        Venue venue = Venue.newInstance(500);

        SeatHold seatHold = SeatHold.newInstance(customer, venue, 1);
        seatHold.getSeats().forEach(s -> ReflectionTestUtils.setField(s, "id", 1L));
        seatHold.setHoldingTime(Instant.now());
        ReflectionTestUtils.setField(seatHold, "id", 1L);
        return seatHold;
    }
}