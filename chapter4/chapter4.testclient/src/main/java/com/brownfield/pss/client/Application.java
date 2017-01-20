package com.brownfield.pss.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.brownfield.pss.book.entity.BookingRecord;
import com.brownfield.pss.book.entity.Passenger;
import com.brownfield.pss.checkin.entity.CheckInRecord;
import com.brownfield.pss.search.controller.SearchQuery;
import com.brownfield.pss.search.entity.Flight;

@SpringBootApplication
public class Application implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	
	RestTemplate restClient = new RestTemplate();
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String... strings) throws Exception {
		//Search for a flight
		SearchQuery searchQuery = new SearchQuery("NYC","SFO","22-JAN-16");
 		Flight[] flights = restClient.postForObject("http://localhost:8083/search/get", searchQuery, Flight[].class); 
 		Arrays.asList(flights).forEach(flight -> logger.info(" flight >"+ flight));
  		
		//create a booking
		Flight flight = flights[0];
		BookingRecord booking = new BookingRecord(flight.getFlightNumber(),flight.getOrigin(),
												  flight.getDestination(), flight.getFlightDate(),null,
												  flight.getFares().getFare());
		Set<Passenger> passengers = new HashSet<Passenger>();
		passengers.add(new Passenger("Gavin","Franc","Male", booking));
		booking.setPassengers(passengers);
		
		long bookingId = restClient.postForObject("http://localhost:8080/booking/create", booking, long.class); 
		logger.info("Booking created "+ bookingId);
		
		//check in passenger
		CheckInRecord checkIn = new CheckInRecord("Franc", "Gavin", "28C", null, "BF101","22-JAN-16", bookingId);
		long checkinId = restClient.postForObject("http://localhost:8081/checkin/create", checkIn, long.class); 
		
		logger.info("Checked IN "+ checkinId);
	}
		
}


