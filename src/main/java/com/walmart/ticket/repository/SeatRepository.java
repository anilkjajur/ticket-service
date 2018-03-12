package com.walmart.ticket.repository;


import com.walmart.ticket.domain.Seat;
import com.walmart.ticket.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
