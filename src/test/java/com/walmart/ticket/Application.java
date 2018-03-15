package com.walmart.ticket;


import com.walmart.ticket.controller.TicketController;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackageClasses = TicketController.class)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class Application {
}
