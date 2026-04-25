package edu.dccc.flightops;

import edu.dccc.utils.CSVTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Flight implements CSVTemplate, Comparable<Flight> {

    enum FlightType {
        Arrival, Departure
    }

    enum OperationStatus {
        Scheduled, CancelDueCrash, CancelDuePassengerDisturbance, CancelDueDrunkPilot,
        CancelDueMaintenance, CancelNoPlane, Queued, NavigationError;

        public static OperationStatus getRandomStatus() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    String flightNumber;
    String aircraftNumber;
    String destinationOrigin;
    LocalDateTime schedule;
    FlightType flightType;
    OperationStatus operationStatus = OperationStatus.Scheduled;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
    private static final DateTimeFormatter TIME_ONLY = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String toCSV() {
        return String.join(",",
                flightNumber,
                aircraftNumber,
                destinationOrigin,
                schedule.format(DATE_FORMAT),
                flightType.name(),
                operationStatus.name()
        );
    }

    @Override
    public void fromCSV(String[] parts) {
        // We add .trim() to everything to handle invisible spaces
        this.flightNumber = parts[0].trim();
        this.aircraftNumber = parts[1].trim();
        this.destinationOrigin = parts[2].trim();

        // SWAPPED THESE: Most Flights.csv files have the Type before the Date
        setFlightType(parts[3].trim()); // Was 4, now 3
        setSchedule(parts[4].trim());  // Was 3, now 4

        if (parts.length > 5) {
            this.operationStatus = OperationStatus.valueOf(parts[5].trim());
        }
    }

    public Flight(String flightNumber, String aircraftNumber, String destinationOrigin, LocalDateTime schedule, FlightType flightType) {
        this.flightNumber = flightNumber;
        this.aircraftNumber = aircraftNumber;
        this.destinationOrigin = destinationOrigin;
        this.schedule = schedule;
        this.flightType = flightType;
    }

    public Flight() {}

    @Override
    public String toString() {
        return "FlightType: " + flightType +
                " | Flight: " + flightNumber +
                " | Aircraft: " + aircraftNumber +
                " | Schedule: " + (schedule != null ? schedule.format(TIME_ONLY) : "N/A") +
                " | Status: " + operationStatus;
    }

    public void setSchedule(String scheduleString) {
        try {
            this.schedule = LocalDateTime.parse(scheduleString, DATE_FORMAT);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + scheduleString);
        }
    }

    public void setFlightType(String type) {
        try {
            this.flightType = FlightType.valueOf(type);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Flight Type: " + type);
        }
    }

    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public void setAircraftNumber(String aircraftNumber) { this.aircraftNumber = aircraftNumber; }
    public void setDestinationOrigin(String destinationOrOrigin) { this.destinationOrigin = destinationOrOrigin; }

    // Required for the generic CSVReaderWriter to function
    @Override
    public int compareTo(Flight other) {
        if (this.schedule == null || other.schedule == null) return 0;
        return this.schedule.compareTo(other.schedule);
    }
}