package com.java_rmi.data_conector;

/**
 * The GeoData class represents geographical data for a location.
 * It includes information such as geoname ID, name, country code, country name (in English),
 * population, timezone, and coordinates.
 */
public class GeoData {
    private String geonameID;
    private String name;
    private String countryCode;
    private String countryNameEn;
    private long population;
    private String timezone;
    private String coordinates;

    /**
     * Default constructor for the GeoData class.
     * Initializes all fields to default values (null or 0).
     */
    public GeoData() {
    }

    /**
     * Constructor for the GeoData class with parameters.
     *
     * @param geonameID     The unique identifier for the location.
     * @param name          The name of the location.
     * @param countryCode   The country code for the location.
     * @param countryNameEn The country name in English.
     * @param population    The population of the location.
     * @param timezone      The timezone of the location.
     * @param coordinates   The coordinates of the location (e.g., latitude and longitude).
     */
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
