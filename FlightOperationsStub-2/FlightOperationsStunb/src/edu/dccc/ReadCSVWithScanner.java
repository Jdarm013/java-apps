package edu.dccc;

import edu.dccc.flightops.Flight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class ReadCSVWithScanner {

    public static void main(String[] args) {
        ReadCSVWithScanner r = new ReadCSVWithScanner();
        // Updated path to reflect the "src" folder location
        System.out.println(r.getFlightListFromCSV("./src/resources/Flights.csv"));
    }

    public LinkedList<Flight> getFlightListFromCSV(String filePath) {
        LinkedList<Flight> fltList = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;

            reader.readLine();

            // Parse comma-separated line into an array and map to Flight object
            while ((line = reader.readLine()) != null) {
                Flight flt = new Flight();
                String[] data = line.split(",");
                flt.fromCSV(data);
                fltList.add(flt);
            }
            reader.close();
        }
        catch (IOException e) {
            System.out.println("File not found");
        }
        return fltList;
    }
}