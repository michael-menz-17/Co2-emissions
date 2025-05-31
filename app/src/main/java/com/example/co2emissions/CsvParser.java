package com.example.co2emissions;

import android.content.Context; // Used to access app resources like the assets folder.
import android.util.Log;        // For logging errors to the Android logcat.

import java.io.BufferedReader;  // Reads text from an input stream (like a file).
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
public class CsvParser {

    private static final String TAG = "CsvParser"; // Used for logging errors.

    // This static method reads a CSV file from the assets folder and returns a list of CountryEmission objects.
    public static List<CountryEmission> parseCSV(Context context, String fileName) {
        // LinkedHashMap keeps the order of insertion, so countries appear in the same order as in the file.
        Map<String, CountryEmission> countryMap = new LinkedHashMap<>();
        try (
                // Open the file from the app's assets folder.
                InputStream is = context.getAssets().open(fileName);
                // Wrap IS with a BufferedReader for efficient reading.
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))
        ) {
            String line;
            while ((line = reader.readLine()) != null) { // Read the file line by line.
                if (line.trim().isEmpty()) continue; // Skip empty lines.
                // Split the line into columns using commas. -1 keeps empty columns.
                String[] tokens = line.split(",", -1);
                if (tokens.length < 9) continue; // Skip lines with missing columns.

                // Extract country name from the first column.
                String countryName = tokens[0].trim();

                // Parse year, emission, and population, handling missing or invalid data.
                int year = 0;
                try { year = Integer.parseInt(tokens[3].trim()); } catch (Exception ignored) {}

                long emission = 0L;
                try {
                    emission = Long.parseLong(tokens[4].trim());
                    if (emission < 0) emission = 0; // Defensive: no negative emissions allowed.
                } catch (Exception ignored) {}

                long population = 0L;
                try {
                    population = Long.parseLong(tokens[5].trim());
                    if (population < 0) population = 0; // Defensive: no negative population allowed.
                } catch (Exception ignored) {}

                // Get the percentage of world landmass and density from the CSV.
                String percentOfWorld = tokens[7].trim();
                String density = tokens[8].trim();

                // If this country hasn't been seen yet, create a new CountryEmission for it.
                CountryEmission c = countryMap.get(countryName);
                if (c == null) {
                    c = new CountryEmission(countryName, new HashMap<>(), population, percentOfWorld, density);
                    countryMap.put(countryName, c);
                }
                // Add or update the emission for this year.
                c.getCo2Emissions().put(year, emission);
            }
        } catch (Exception e) {
            // Log any errors that occur while reading the file.
            Log.e(TAG, "Error reading CSV", e);
        }
        // Return all the CountryEmission objects as a Arraylist.
        return new ArrayList<>(countryMap.values());
    }
}
