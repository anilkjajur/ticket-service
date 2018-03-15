package com.walmart.ticket.scheduler;

import com.walmart.ticket.domain.Customer;
import com.walmart.ticket.domain.Seat;
import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.domain.Venue;
import com.walmart.ticket.repository.SeatHoldRepository;
import com.walmart.ticket.repository.SeatRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketSchedulerTest {

    @InjectMocks
    private TicketScheduler classToTest = new TicketScheduler();

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @Mock
    private SeatRepository seatRepository;

    @Captor
    private ArgumentCaptor<HashSet<SeatHold>> seatHolds;

    private AtomicLong seatHoldId = new AtomicLong(100);
    private AtomicLong seatId = new AtomicLong(100);

    @Test
    public void removeExpiredSeatsOnHoldWhenNoExpiredSeatHolds() {
        //given
        when(seatHoldRepository.findAll()).thenReturn(getSeatHold(false));

        //when
        classToTest.removeExpiredSeatsOnHold();

        //then
        verify(seatHoldRepository, never()).deleteAll(anyCollection());
        verify(seatRepository, never()).deleteAll(anyCollection());
    }

    @Test
    public void removeExpiredSeatsOnHoldWhenHasExpiredSeatHolds() {
        //given
        when(seatHoldRepository.findAll()).thenReturn(getSeatHold(true));

        //when
        classToTest.removeExpiredSeatsOnHold();

        //then
        verify(seatHoldRepository).deleteAll(seatHolds.capture());
        verify(seatRepository, times(3)).deleteAll(anyCollection());

        assertEquals(3, seatHolds.getValue().size());
    }

    private List<SeatHold> getSeatHold(boolean needExpired) {
        SeatHold seatHold1 = createSeatHold(needExpired);
        SeatHold seatHold2 = createSeatHold(needExpired);
        SeatHold seatHold3 = createSeatHold(needExpired);
        SeatHold seatHold4 = createSeatHold(false);
        return Arrays.asList(seatHold1, seatHold2, seatHold3, seatHold4);
    }

    private SeatHold createSeatHold(boolean needExpired) {
        Customer customer = new Customer("abc@xyz.com");
        Venue venue = Venue.newInstance(500);

        SeatHold seatHold = SeatHold.newInstance(customer, venue, 1);
        seatHold.getSeats().forEach(s -> ReflectionTestUtils.setField(s, "id", seatId.getAndIncrement()));
        seatHold.setHoldingTime(Instant.now());
        if (needExpired) {
            seatHold.setHoldingTime(Instant.now().minusSeconds(75));
        }
        ReflectionTestUtils.setField(seatHold, "id", seatHoldId.getAndIncrement());
        return seatHold;
    }
}