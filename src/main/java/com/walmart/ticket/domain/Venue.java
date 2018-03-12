package com.walmart.ticket.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "VENUE")
public class Venue extends BaseEntity implements Serializable {

    @Column(name = "TOTAL_SEATS", nullable = false)
    private int totalSeats;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private Set<Seat> seats;

    private Venue() {
        super();
    }

    @PostLoad
    private void postLoad() {
        if (this.seats == null) {
            this.seats = new HashSet<>();
        }
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        if (seats == null) {
            this.seats = new HashSet<>();
        }
        else {
            this.seats = seats;
        }
    }

    public static VenueBuilder newInstance() {
        return new VenueBuilder();
    }

    public int getAvailableSeats() {
        return this.totalSeats - getOnHoldOrReservedCount();
    }

    private int getOnHoldOrReservedCount() {
        return Long.valueOf(this.seats.stream()
                .filter(s -> s.isOnHold() || s.isReserved())
                .count())
                .intValue();
    }

    public void addSeats(Set<Seat> seats) {
        this.seats.addAll(seats);
    }

    private static class VenueBuilder {

        private Integer totalSeats;
        private Set<Seat> seats;

        VenueBuilder() {
        }

        public VenueBuilder seats(Set<Seat> seats) {
            if (seats == null) {
                this.seats = new HashSet<>();
            }
            else {
                this.seats = seats;
            }
            return this;
        }

        public VenueBuilder totalSeats(Integer numberOfSeats) {
            this.totalSeats = numberOfSeats;
            return this;
        }

        public Venue build() {
            Venue domain = new Venue();
            domain.seats = this.seats;
            domain.totalSeats = this.totalSeats;
            return domain;
        }
    }
}
