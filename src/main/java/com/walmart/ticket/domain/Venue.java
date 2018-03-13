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

    @Column(name = "REMAINING_SEATS", nullable = false)
    private int remainingSeats;

    @Column(name = "RESERVED_SEATS", nullable = false)
    private int reservedSeats;

    @Column(name = "ON_HOLD_SEATS", nullable = false)
    private int onHoldSeats;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seat> seats;

    private Venue() {
        super();
    }

    @PreUpdate
    private void preUpdate() {
        remainingSeats = getAvailableSeats();
        onHoldSeats = getOnHoldCount();
        reservedSeats = getReservedCount();
    }

    @PostLoad
    private void postLoad() {
        if (this.seats == null) {
            this.seats = new HashSet<>();
        }
        remainingSeats = getAvailableSeats();
        onHoldSeats = getOnHoldCount();
        reservedSeats = getReservedCount();
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

    public int getRemainingSeats() {
        return remainingSeats;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    public int getOnHoldSeats() {
        return onHoldSeats;
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

    private int getOnHoldCount() {
        return Long.valueOf(this.seats.stream()
                .filter(Seat::isOnHold)
                .count())
                .intValue();
    }

    private int getReservedCount() {
        return Long.valueOf(this.seats.stream()
                .filter(Seat::isReserved)
                .count())
                .intValue();
    }

    public void addSeats(Set<Seat> seats) {
        this.seats.addAll(seats);
        preUpdate();
    }

    public static Venue newInstance(int totalSeats) {
        Venue domain = new Venue();
        domain.totalSeats = totalSeats;
        return domain;
    }

    public void removeSeats(Set<Seat> seats) {
        this.seats.removeAll(seats);
        preUpdate();
    }
}
