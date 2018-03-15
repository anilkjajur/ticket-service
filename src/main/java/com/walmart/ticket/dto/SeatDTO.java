package com.walmart.ticket.dto;


import com.walmart.ticket.domain.Status;

import java.io.Serializable;
import java.math.BigDecimal;

public class SeatDTO implements Serializable {

    private BigDecimal price;
    private Status status;
    private Long seatId;

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Long getSeatId() {
        return seatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeatDTO seatDTO = (SeatDTO) o;

        return seatId.equals(seatDTO.seatId);
    }

    @Override
    public int hashCode() {
        return seatId.hashCode();
    }
}
