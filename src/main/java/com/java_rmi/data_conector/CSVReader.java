package com.java_rmi.data_conector;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * The CSVReader class is responsible for reading geographical data from a CSV file and
 * converting it into a list of GeoData objects.
 */
public class CSVReader {

    /**
     * Reads geographical data from a CSV file and returns it as a list of GeoData objects.
     * @return A list of GeoData objects containing the read data.
     */
    public static List<GeoData> readDataCSV() {
        String csvFile = "src\\main\\java\\com\\java_rmi\\data_conector\\exercise_1_dataset.csv";
        List<GeoData> allData = new ArrayList<>();

        try {
            Reader reader = new FileReader(csvFile);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            boolean line1 = true;

            // Loop through each record in the CSV file
            for (CSVRecord csvRecord : csvParser) {
                if (line1) {
                    line1 = false;
                    continue; // Skip the first line (header)
                }

                // Extract data from CSV record
                String geonameID = csvRecord.get(0);
                String name = csvRecord.get(1);
                String countryCode = csvRecord.get(2);
                String countryNameEn = csvRecord.get(3);
                long population = Long.parseLong("0" + csvRecord.get(4));
                String timezone = csvRecord.get(5);
                String coordinates = csvRecord.get(6);

                // Create a GeoData object with the extracted data
                GeoData data = new GeoData(geonameID, name, countryCode, countryNameEn, population,
                        timezone, coordinates);

                allData.add(data); // Add GeoData to the list
            }

            csvParser.close();
        } catch (IOException e) {
            System.out.println("Error reading data from CSV!!!");
        }

        return allData;
    }
}
