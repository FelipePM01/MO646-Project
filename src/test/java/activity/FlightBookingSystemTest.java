package activity;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

class FlightBookingSystemTest {

    FlightBookingSystem system = new FlightBookingSystem();

    // Compra inválida por número de assentos
    @Test
    void tc1() {
        int passengers = 5;
        LocalDateTime bookingTime = LocalDateTime.now();
        int availableSeats = 4;
        double currentPrice = 200;
        int previousSales = 50;
        boolean isCancellation = false;
        LocalDateTime departureTime = bookingTime.plusDays(2);
        int rewardPointsAvailable = 0;
        FlightBookingSystem.BookingResult result = system.bookFlight(
            passengers,
            bookingTime,
            availableSeats,
            currentPrice,
            previousSales,
            isCancellation,
            departureTime,
            rewardPointsAvailable
        );

        assertFalse(result.confirmation);
        assertEquals(0, result.totalPrice, 0.001);
        assertEquals(0, result.refundAmount, 0.001);
        assertEquals(false, result.pointsUsed);
    }

    // Compra válida com last-minute fee, group discount e reward points
    @Test
    void tc2() {
        int passengers = 5;
        LocalDateTime bookingTime = LocalDateTime.now();
        int availableSeats = 5;
        double currentPrice = 200;
        int previousSales = 50;
        boolean isCancellation = false;
        LocalDateTime departureTime = bookingTime.plusHours(4);
        int rewardPointsAvailable = 40;
        FlightBookingSystem.BookingResult result = system.bookFlight(
            passengers,
            bookingTime,
            availableSeats,
            currentPrice,
            previousSales,
            isCancellation,
            departureTime,
            rewardPointsAvailable
        );
        
        
        double expectedPrice = (currentPrice * ((previousSales / 100.0) * 0.8) * passengers + 100)*0.95 - rewardPointsAvailable*0.01;
        assertEquals(expectedPrice, result.totalPrice, 0.001);
        assertTrue(result.confirmation);
        assertEquals(0, result.refundAmount, 0.001);
        assertEquals(true, result.pointsUsed);
    }

    // Cancelamento maior ou igual a 48 horas
    @Test
    void tc3() {
        int passengers = 2;
        LocalDateTime bookingTime = LocalDateTime.now();
        int availableSeats = 5;
        double currentPrice = 150;
        int previousSales = 30;
        boolean isCancellation = true;
        LocalDateTime departureTime = bookingTime.plusDays(2);
        int rewardPointsAvailable = 0;
        FlightBookingSystem.BookingResult result = system.bookFlight(
            passengers,
            bookingTime,
            availableSeats,
            currentPrice,
            previousSales,
            isCancellation,
            departureTime,
            rewardPointsAvailable
        );

        double expectedRefund = currentPrice * ((previousSales / 100.0) * 0.8) * passengers;
        assertEquals(expectedRefund, result.refundAmount, 0.001);
        assertFalse(result.confirmation);
        assertEquals(0, result.totalPrice, 0.001);
        assertEquals(false, result.pointsUsed);
    }

    // Cancelamento a menos de 48 horas
    @Test
    void tc4() {
        int passengers = 2;
        LocalDateTime bookingTime = LocalDateTime.now();
        int availableSeats = 5;
        double currentPrice = 150;
        int previousSales = 30;
        boolean isCancellation = true;
        LocalDateTime departureTime = bookingTime.plusHours(36);
        int rewardPointsAvailable = 0;
        FlightBookingSystem.BookingResult result = system.bookFlight(
            passengers,
            bookingTime,
            availableSeats,
            currentPrice,
            previousSales,
            isCancellation,
            departureTime,
            rewardPointsAvailable
        );
        
        double expectedRefund = currentPrice * ((previousSales / 100.0) * 0.8) * passengers * 0.5;
        assertEquals(expectedRefund, result.refundAmount, 0.001);
        assertFalse(result.confirmation);
        assertEquals(0, result.totalPrice, 0.001);
        assertEquals(false, result.pointsUsed);
    }

    // Compra válida sem last-minute fee, sem group discount e sem reward points para matar mutations
    @Test
    void tc5_mutations() {
        int passengers = 4;
        LocalDateTime bookingTime = LocalDateTime.now();
        int availableSeats = 4;
        double currentPrice = 200;
        int previousSales = 50;
        boolean isCancellation = false;
        LocalDateTime departureTime = bookingTime.plusHours(24);
        int rewardPointsAvailable = 0;
        FlightBookingSystem.BookingResult result = system.bookFlight(
            passengers,
            bookingTime,
            availableSeats,
            currentPrice,
            previousSales,
            isCancellation,
            departureTime,
            rewardPointsAvailable
        );
        
        
        double expectedPrice = (currentPrice * ((previousSales / 100.0) * 0.8) * passengers)- rewardPointsAvailable*0.01;
        assertEquals(expectedPrice, result.totalPrice, 0.001);
        assertTrue(result.confirmation);
        assertEquals(0, result.refundAmount, 0.001);
        assertEquals(false, result.pointsUsed);
    }


}
