package com.java_rmi.data_conector;

public class GeoData {
    private String geonameID;
    private String name;
    private String countryCode;
    private String countryNameEn;
    private long population;
    private String timezone;
    private String coordinates;

    public GeoData() {
    }

    public GeoData(String geonameID, String name, String countryCode, String countryNameEn,
                   long population, String timezone, String coordinates) {
        this.geonameID = geonameID;
        this.name = name;
        this.countryCode = countryCode;
        this.countryNameEn = countryNameEn;
        this.population = population;
        this.timezone = timezone;
        this.coordinates = coordinates;
    }

    public String getGeonameID() {
        return geonameID;
    }

    public void setGeonameID(String geonameID) {
        this.geonameID = geonameID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
