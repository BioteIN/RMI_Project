package com.java_rmi.data_conector;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<GeoData> readDataCSV() {
        String csvFile = "src\\main\\java\\com\\java_rmi\\data_conector\\exercise_1_dataset.csv";
        List<GeoData> allData = new ArrayList<>();

        try {
            Reader reader = new FileReader(csvFile);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            boolean line1 = true;

            for (CSVRecord csvRecord : csvParser) {
                if (line1) {
                    line1 = false;
                    continue;
                }

                String geonameID = csvRecord.get(0);
                String name = csvRecord.get(1);
                String countryCode = csvRecord.get(2);
                String countryNameEn = csvRecord.get(3);
                long population = Long.parseLong("0"+csvRecord.get(4));
                String timezone = csvRecord.get(5);
                String coordinates = csvRecord.get(6);

                GeoData data = new GeoData(geonameID, name, countryCode, countryNameEn, population,
                        timezone, coordinates);

                allData.add(data);
            }

            csvParser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allData;
    }

}