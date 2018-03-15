package com.walmart.ticket.domain;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "SEAT")
public class Seat extends BaseEntity implements Serializable {

    @Column(name = "SEAT_CODE", length = 500, nullable = false, updatable = false)
    private String seatCode;

    @Digits(integer=8, fraction=2)
    @Column(name = "PRICE", precision = 8, scale = 2)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 50, nullable = false)
    private Status status;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_hold_id", nullable = false)
    private SeatHold seatHold;

    private Seat() {
        super();
    }

    public String getSeatCode() {
        return seatCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public SeatHold getSeatHold() {
        return seatHold;
    }

    public void setSeatHold(SeatHold seatHold) {
        this.seatHold = seatHold;
    }

    public static SeatBuilder newInstance() {
        return new SeatBuilder();
    }

    public void makeOnHold() {
        this.status = Status.ON_HOLD;
    }

    public void makeAvailable() {
        this.status = Status.AVAILABLE;
    }

    public boolean isOnHold() {
        return Status.ON_HOLD.equals(this.status);
    }
    public boolean isReserved() {
        return Status.RESERVED.equals(this.status);
    }

    public void makeReserved() {
        this.status = Status.RESERVED;
    }

    public static class SeatBuilder {

        private Status status;
        private Venue venue;
        private SeatHold seatHold;


        private SeatBuilder() {
        }

        public SeatBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public SeatBuilder venue(Venue venue) {
            this.venue = venue;
            return this;
        }

        public SeatBuilder seatHold(SeatHold seatHold) {
            this.seatHold = seatHold;
            return this;
        }

        public Seat build() {
            Seat domain = new Seat();
            domain.status = Objects.requireNonNull(this.status, "Seat status is required");
            domain.venue = Objects.requireNonNull(this.venue, "Venue is required");
            domain.seatHold = Objects.requireNonNull(this.seatHold, "Seat hold is required");
            domain.price = BigDecimal.TEN;
            domain.seatCode = UUID.randomUUID().toString();
            return domain;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Seat seat = (Seat) o;

        return seatCode.equals(seat.seatCode);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + seatCode.hashCode();
        return result;
    }
}
