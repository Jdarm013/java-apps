package edu.dccc.flightops;

import edu.dccc.ReadCSVWithScanner;
import edu.dccc.utils.CSVReaderWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class FlightOps {

    LinkedList<Flight> flts = new LinkedList<>();

    private void printFlights() {
        for (Flight flt : flts) {
            System.out.println(flt);
        }
        System.out.println("-----------------------------------");
    }

    private void removeCancelledFlights() {
        Stack<Flight> removeStack = new Stack<>();

        // Identify flights for removal
        for (Flight flt: flts) {
            if (flt.operationStatus == Flight.OperationStatus.CancelDueCrash
                    || flt.operationStatus == Flight.OperationStatus.CancelDueDrunkPilot
                    || flt.operationStatus == Flight.OperationStatus.CancelDueMaintenance
                    || flt.operationStatus == Flight.OperationStatus.CancelDuePassengerDisturbance
                    || flt.operationStatus == Flight.OperationStatus.NavigationError
                    || flt.operationStatus == Flight.OperationStatus.CancelNoPlane) {
                removeStack.push(flt);
            }
        }

        // Execute removal using stack to prevent concurrency exceptions
        while (!removeStack.isEmpty()) {
            flts.remove(removeStack.pop());
        }
    }

    private void changeStatuses() {
        for (Flight flt : flts) {
            Flight.OperationStatus status = Flight.OperationStatus.getRandomStatus();
            if (flt.flightType == Flight.FlightType.Arrival) {
                if (status == Flight.OperationStatus.CancelDueCrash || status == Flight.OperationStatus.NavigationError || status == Flight.OperationStatus.Scheduled || status == Flight.OperationStatus.Queued) {
                    flt.operationStatus = status;
                }
            } else if ((flt.flightType == Flight.FlightType.Arrival && status == Flight.OperationStatus.CancelDueMaintenance)
                    || (flt.flightType == Flight.FlightType.Departure && status == Flight.OperationStatus.NavigationError)) {
                continue;
            } else {
                flt.operationStatus = status;
            }
        }
    }

    private void moveQueuedFlights() {
        Stack<Flight> moveStack = new Stack<>();

        // Identify queued flights
        for (Flight flt : flts) {
            if (flt.operationStatus == Flight.OperationStatus.Queued) {
                moveStack.push(flt);
            }
        }

        // Move queued flights to the end of the list
        while (!moveStack.isEmpty()) {
            Flight flight = moveStack.pop();
            flts.remove(flight);
            flts.addLast(flight);
        }
    }

    private void presidentAndCroniesJumpTheQueue() {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
        try {
            LocalDateTime date1 = LocalDateTime.parse("10/15/2020 07:30", sdf);
            LocalDateTime date2 = LocalDateTime.parse("10/15/2020 07:45", sdf);
            LocalDateTime date3 = LocalDateTime.parse("10/15/2020 08:00", sdf);

            Flight vipFlight1 = new Flight("Vip001", "AF-01", "CDG", date1, Flight.FlightType.Departure);
            Flight vipFlight2 = new Flight("Vip002", "AF-01", "CDG", date2, Flight.FlightType.Departure);
            Flight vipFlight3 = new Flight("Vip003", "AF-01", "CDG", date3, Flight.FlightType.Arrival);

            // Insert VIP flights at the head of the list
            flts.addFirst(vipFlight3);
            flts.addFirst(vipFlight2);
            flts.addFirst(vipFlight1);
        } catch (Exception e) {
            System.out.println("Date Parse Exception: " + e.getMessage());
        }
    }

    public void doSimuluation(String filePath) {
        flts = (LinkedList<Flight>) initializeFlightListOld(filePath);

        changeStatuses();
        System.out.println("Changed statuses");
        printFlights();

        System.out.println("Remove cancelled flights");
        removeCancelledFlights();
        printFlights();

        presidentAndCroniesJumpTheQueue();
        System.out.println("Cronies jump queue");
        printFlights();

        moveQueuedFlights();
        System.out.println("Moved queued flights");
        printFlights();
    }

    public static void main(String[] args) {
        FlightOps fltOPs = new FlightOps();

        String filePath = "FlightOperationsStunb/resources/Flights.csv";
        fltOPs.doSimuluation(filePath);
    }

    // Version utilizing manual scanner implementation
    public List<Flight> initializeFlightListOld(String filePath) {
        ReadCSVWithScanner csvReader = new ReadCSVWithScanner();
        return csvReader.getFlightListFromCSV(filePath);
    }

    // Version utilizing generic CSVReaderWriter
    public List<Flight> initializeFlightList(String filePath) {
        LinkedList<Flight> storage = new LinkedList<>();
        CSVReaderWriter<Flight> csv = new CSVReaderWriter<>(filePath, storage, Flight.class);
        csv.loadFromCSV(true);
        return storage;
    }
}
