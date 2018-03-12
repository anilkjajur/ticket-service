package com.walmart.ticket.repository;


import com.walmart.ticket.domain.Customer;
import com.walmart.ticket.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerEmail(String customerEmail);
}
