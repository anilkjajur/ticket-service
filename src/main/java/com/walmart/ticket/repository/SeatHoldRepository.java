package com.walmart.ticket.repository;


import com.walmart.ticket.domain.SeatHold;
import com.walmart.ticket.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {
}
