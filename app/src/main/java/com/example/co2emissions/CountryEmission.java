package com.example.co2emissions;

import java.io.Serializable;
import java.util.HashMap;

public class CountryEmission implements Serializable {
    private String countryName;
    private HashMap<Integer, Long> co2Emissions;  // Use Long for large values
    private long population;                      // Use long for population
    private String percentageOfWorld;
    private String density;

    public CountryEmission(String countryName, HashMap<Integer, Long> co2Emissions,
                           long population, String percentageOfWorld, String density) {
        this.countryName = countryName;
        this.co2Emissions = co2Emissions;
        this.population = population;
        this.percentageOfWorld = percentageOfWorld;
        this.density = density;
    }

    public String getCountryName() { return countryName; }
    public HashMap<Integer, Long> getCo2Emissions() { return co2Emissions; }
    public long getPopulation() { return population; }
    public String getPercentageOfWorld() { return percentageOfWorld; }
    public String getDensity() { return density; }

    public long getTotalEmissions() {
        long total = 0L;
        for (long emission : co2Emissions.values()) total += emission;
        return total;
    }

    public double getAverageEmissionsPerCapita() {
        if (population <= 0) return 0;
        return (double) getTotalEmissions() / population;
    }

    public int getYearWithHighestEmission() {
        int maxYear = -1;
        long maxEmission = Long.MIN_VALUE;
        for (int year : co2Emissions.keySet()) {
            long emission = co2Emissions.get(year);
            if (emission > maxEmission) {
                maxEmission = emission;
                maxYear = year;
            }
        }
        return maxYear;
    }
}
