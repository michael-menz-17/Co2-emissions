package com.example.co2emissions;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CsvParser {

    private static final String TAG = "CsvParser";

    public static List<CountryEmission> parseCSV(Context context, String fileName) {
        Map<String, CountryEmission> countryMap = new LinkedHashMap<>();
        try (InputStream is = context.getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",", -1);
                if (tokens.length < 9) continue;

                String countryName = tokens[0].trim();

                int year = 0;
                try {
                    year = Integer.parseInt(tokens[3].trim());
                } catch (Exception ignored) {}

                long emission = 0L;
                try {
                    emission = Long.parseLong(tokens[4].trim());
                    if (emission < 0) emission = 0; // Defensive: no negative emissions
                } catch (Exception ignored) {}

                long population = 0L;
                try {
                    population = Long.parseLong(tokens[5].trim());
                    if (population < 0) population = 0; // Defensive: no negative population
                } catch (Exception ignored) {}

                String percentOfWorld = tokens[7].trim();
                String density = tokens[8].trim();

                CountryEmission c = countryMap.get(countryName);
                if (c == null) {
                    c = new CountryEmission(countryName, new HashMap<>(), population, percentOfWorld, density);
                    countryMap.put(countryName, c);
                }
                c.getCo2Emissions().put(year, emission);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading CSV", e);
        }
        return new ArrayList<>(countryMap.values());
    }
}
