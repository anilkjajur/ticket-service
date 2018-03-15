package com.walmart.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.walmart.ticket.domain.Customer;
import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.domain.Status;
import com.walmart.ticket.domain.Venue;
import com.walmart.ticket.dto.SeatDTO;
import com.walmart.ticket.dto.SeatHoldDTO;
import com.walmart.ticket.dto.SeatHoldRequestDTO;
import com.walmart.ticket.dto.SeatReserveDTO;
import com.walmart.ticket.service.TicketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = TicketController.class, secure = false)
@AutoConfigureMockMvc
@WithMockUser
public class TicketControllerTest {

    private static final String API_V1 = "/api/v1";
    private static final String VENUE_SEATS = "/venue/seats";
    private static final String HOLD = "/hold";
    private static final String RESERVE = "/reserve";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TicketService ticketService;

    @MockBean(name = "demoVenue")
    private CommandLineRunner demoVenue;

    @Test
    public void getAllAvailableSeats() throws Exception {
        //given
        when(ticketService.numSeatsAvailable()).thenReturn(500);

        //when
        mvc.perform(get(API_V1 + VENUE_SEATS)
                .with(user("test").password("changeit"))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("availableSeats", is(500)));
    }

    @Test
    public void holdSeats() throws Exception {
        //given
        SeatHoldRequestDTO request = new SeatHoldRequestDTO();
        request.setNumSeats(1);
        request.setCustomerEmail("abc@xyz.com");

        Instant holdingTime = Instant.now();
        Instant expirationTime = holdingTime.plusSeconds(60);

        SeatDTO seatDTO = getSeatDTO();
        SeatHoldDTO expectedDTO = getSeatHoldDTO(holdingTime, expirationTime, seatDTO);


        when(ticketService.findAndHoldSeats(anyInt(), anyString())).thenReturn(getSeatHold(holdingTime));

        //when
        mvc.perform(post(API_V1 + VENUE_SEATS + HOLD)
                .with(user("test").password("changeit"))
                .content(getJsonString(request))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("seatHoldId", is(expectedDTO.getSeatHoldId().intValue())))
                .andExpect(jsonPath("customerEmail", is(expectedDTO.getCustomerEmail())))
                .andExpect(jsonPath("numberOfSeats", is(expectedDTO.getNumberOfSeats())))
                .andExpect(jsonPath("holdingTime", is(expectedDTO.getHoldingTime().toString())))
                .andExpect(jsonPath("expirationTime", is(expectedDTO.getExpirationTime().toString())))
                .andExpect(jsonPath("seats", hasSize(1)))
                .andExpect(jsonPath("seats[0].price", is(seatDTO.getPrice().intValue())))
                .andExpect(jsonPath("seats[0].status", is(seatDTO.getStatus().toString())))
                .andExpect(jsonPath("seats[0].seatId", is(seatDTO.getSeatId().intValue())));
    }

    @Test
    public void reserveSeats() throws Exception {
        //given
        SeatReserveDTO request = new SeatReserveDTO();
        request.setSeatHoldId("1");
        request.setCustomerEmail("abc@xyz.com");

        String bookingCode = UUID.randomUUID().toString();

        when(ticketService.reserveSeats(anyString(), anyString())).thenReturn(bookingCode);

        //when
        mvc.perform(post(API_V1 + VENUE_SEATS + RESERVE)
                .with(user("test").password("changeit"))
                .content(getJsonString(request))
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("bookingCode", is(bookingCode)));
    }

    private SeatHoldDTO getSeatHoldDTO(Instant holdingTime, Instant expirationTime, SeatDTO seatDTO) {
        SeatHoldDTO expectedDTO = new SeatHoldDTO();
        expectedDTO.setSeatHoldId(1L);
        expectedDTO.setNumberOfSeats(1);
        expectedDTO.setCustomerEmail("abc@xyz.com");
        expectedDTO.setHoldingTime(holdingTime);
        expectedDTO.setExpirationTime(expirationTime);

        expectedDTO.addSeat(seatDTO);
        return expectedDTO;
    }

    private SeatDTO getSeatDTO() {
        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setSeatId(1L);
        seatDTO.setPrice(BigDecimal.TEN);
        seatDTO.setStatus(Status.ON_HOLD);
        return seatDTO;
    }


    private SeatHold getSeatHold(Instant holdingTime) {
        Customer customer = new Customer("abc@xyz.com");
        Venue venue = Venue.newInstance(500);

        SeatHold seatHold = SeatHold.newInstance(customer, venue, 1);
        seatHold.getSeats().forEach(s -> ReflectionTestUtils.setField(s, "id", 1L));
        seatHold.setHoldingTime(holdingTime);
        ReflectionTestUtils.setField(seatHold, "id", 1L);
        return seatHold;
    }

    private String getJsonString(Object anObject) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(anObject);
        }
        catch (Exception ignore) {
        }
        return "";
    }
}