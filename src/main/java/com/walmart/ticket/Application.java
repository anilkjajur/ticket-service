package com.walmart.ticket;

import com.walmart.ticket.domain.Venue;
import com.walmart.ticket.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(
	basePackageClasses = { Application.class, Jsr310JpaConverters.class }
)
@SpringBootApplication
@EnableJpaRepositories(value={"com.walmart.ticket.repository"})
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner demo(VenueRepository repository) {
		return (args) -> {
			// create the venue with 500 seats
			Venue venue = repository.findAll()
					.stream()
					.findFirst()
					.orElse(Venue.newInstance(500));

			repository.save(venue);
		};
	}
}
