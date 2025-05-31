package com.example.co2emissions;

import java.io.Serializable;
import java.util.HashMap;

public class CountryEmission implements Serializable { // Lets you pass this object between activities (screens) in Android.
    private String countryName;
    private HashMap<Integer, Long> co2Emissions;  // Use Long for large values
    private long population;                      // Use long for population
    private String percentageOfWorld;
    private String density;


    // Constructor: creates a CountryEmission object with all its data.
    public CountryEmission(String countryName, HashMap<Integer, Long> co2Emissions,
                           long population, String percentageOfWorld, String density) {
        this.countryName = countryName;
        this.co2Emissions = co2Emissions;
        this.population = population;
        this.percentageOfWorld = percentageOfWorld;
        this.density = density;
    }

    // Getter methods to retrieve the value of private or protected attributes (fields) of an object.
    public String getCountryName() { return countryName; }
    public HashMap<Integer, Long> getCo2Emissions() { return co2Emissions; }
    public long getPopulation() { return population; }
    public String getPercentageOfWorld() { return percentageOfWorld; }
    public String getDensity() { return density; }

    public long getTotalEmissions() {  // Sums all the emission values to get the total emissions for this country.
        long total = 0L;
        for (long emission : co2Emissions.values()) total += emission;
        return total;
    }

    public double getAverageEmissionsPerCapita() {
        if (population <= 0) return 0;
        return (double) getTotalEmissions() / population; // average through total / population of country
    }

    public int getYearWithHighestEmission() {
        int maxYear = -1;
        long maxEmission = Long.MIN_VALUE; // Start with the lowest possible value.
        for (int year : co2Emissions.keySet()) {
            long emission = co2Emissions.get(year); //new variable for current value of co2 emissions
            if (emission > maxEmission) { //if that value is larger than the previous max
                maxEmission = emission; //set the variable max emission to the new value
                maxYear = year;
            }
        }
        return maxYear;
    }
}
