package com.walmart.ticket.service;

import com.walmart.ticket.domain.Customer;
import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.domain.Status;
import com.walmart.ticket.domain.Venue;
import com.walmart.ticket.repository.CustomerRepository;
import com.walmart.ticket.repository.SeatHoldRepository;
import com.walmart.ticket.repository.SeatRepository;
import com.walmart.ticket.repository.VenueRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    @InjectMocks
    private TicketServiceImpl classToTest = new TicketServiceImpl();

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @Mock
    private CustomerRepository customerRespository;

    @Mock
    private SeatRepository seatRepository;


    @Test
    public void numSeatsAvailable() {
        //given
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(createVenue(500)));

        //when
        int actual = classToTest.numSeatsAvailable();

        //then
        assertEquals(500, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void numSeatsAvailableWhenVenueNotFound() {
        //given
        when(venueRepository.findById(anyLong())).thenThrow(new IllegalArgumentException("Venue is not found"));

        //when
        classToTest.numSeatsAvailable();
    }

    @Test (expected = IllegalArgumentException.class)
    public void findAndHoldSeatsWhenNumSeatsIsZero() {
        //when
        classToTest.findAndHoldSeats(0, "xyz@domain.com");
    }

    @Test (expected = IllegalArgumentException.class)
    public void findAndHoldSeatsWhenNoSeatsAvailableInVenue() {
        //given
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(createVenue(0)));

        //when
        classToTest.findAndHoldSeats(3, "xyz@domain.com");
    }

    @Test (expected = IllegalArgumentException.class)
    public void findAndHoldSeatsWhenAvailableSeatsInVenueIsLessThanSeatsToHold() {
        //given
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(createVenue(3)));

        //when
        classToTest.findAndHoldSeats(5, "xyz@domain.com");
    }

    @Test
    public void findAndHoldSeatsWhenNumberOfSeatsToHold() {
        //given
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(createVenue(21)));

        //when
        SeatHold seatHold = classToTest.findAndHoldSeats(5, "xyz@domain.com");

        //then
        verify(customerRespository).save(any(Customer.class));
        verify(seatHoldRepository).save(any(SeatHold.class));

        assertEquals(5, seatHold.getNumberOfSeats());
        assertEquals("xyz@domain.com", seatHold.getCustomerEmail());
        assertNotNull(seatHold.getHoldingTime());
        assertNotNull(seatHold.getExpirationTime());
        assertNotNull(seatHold.getVenue());
        assertNotNull(seatHold.getCustomer());
        assertEquals(5, seatHold.getSeats().size());

        assertThat(seatHold.getSeats(), hasItem(hasProperty("status", is(Status.ON_HOLD))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void reserveSeatsWhenInvalidSeatHoldId() {
        //given
        when(seatHoldRepository.findById(anyLong())).thenThrow(new IllegalArgumentException("Not found"));

        //when
        classToTest.reserveSeats("3", "xyz@domain.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void reserveSeatsWhenInvalidCustomerEmailForSeatHoldId() {
        //given
        when(seatHoldRepository.findById(anyLong())).thenReturn(Optional.of(createSeatHold()));

        //when
        classToTest.reserveSeats("3", "xyz@domain.com");
    }

    @Test
    public void reserveSeatsWhenCustomerEmailAndSeatHoldIdAreValid() {
        //given
        ArgumentCaptor<SeatHold> beforeSave = ArgumentCaptor.forClass(SeatHold.class);
        SeatHold seatHold = createSeatHold();
        assertEquals(false, seatHold.getBooking().isPresent());
        when(seatHoldRepository.findById(anyLong())).thenReturn(Optional.of(seatHold));

        //when
        String bookingCode = classToTest.reserveSeats("3", "abc@domain.com");

        //then
        verify(seatHoldRepository).save(beforeSave.capture());
        assertEquals(seatHold.getBookingCode().get(), bookingCode);
    }


    @Test(expected = IllegalArgumentException.class)
    public void reserveSeatsWhenCustomerEmailAndSeatHoldIdAllReadyUsed() {
        //given
        SeatHold seatHold = createSeatHold();
        seatHold.addBooking();
        assertEquals(true, seatHold.getBooking().isPresent());
        when(seatHoldRepository.findById(anyLong())).thenReturn(Optional.of(seatHold));

        //when
        classToTest.reserveSeats("3", "abc@domain.com");

        //then
        verify(seatHoldRepository, never()).save(any(SeatHold.class));
    }

    private SeatHold createSeatHold() {
        Customer customer = new Customer("abc@domain.com");
        Venue venue = Venue.newInstance(100);
        return SeatHold.newInstance(customer, venue, 3);
    }

    private Venue createVenue(int totalSeats) {
        return Venue.newInstance(totalSeats);
    }
}