package com.example.co2emissions;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent; // Used to switch between screens and pass data.
import android.os.Bundle;
import android.widget.*;

import java.util.*;

public class HomeActivity extends AppCompatActivity {

    private int latestYear = 0; // The most recent year found in the data.

    private List<CountryEmission> countries;      // All countries loaded from the CSV.
    private List<CountryEmission> top10Countries; // The top 10 polluters for the latest year.
    private int currentIndex = 0;                 // The index of the currently displayed country in the top 10 list.

    // UI elements for displaying and navigating the top 10 list.
    private TextView tvCountry, tvEmission;
    private Button btnPrev, btnNext, btnDetails, btnFirstScreen, btnFilter;
    private EditText etSearch;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Set the layout for this screen.

        // Connect UI elements to code.
        tvCountry = findViewById(R.id.tvCountry);
        tvEmission = findViewById(R.id.tvEmission);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnDetails = findViewById(R.id.btnDetails);
        btnFirstScreen = findViewById(R.id.btnFirstScreen);
        btnFilter = findViewById(R.id.btnFilter);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        // Load all country data from the CSV file.
        countries = CsvParser.parseCSV(this, "co2_emission_by_countries.csv");
        if (countries.isEmpty()) {
            // pop-up message for the user.
            Toast.makeText(this, "No data found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Find the latest year present in the dataset.
        latestYear = findLatestYear(countries);
        if (latestYear == 0) {
            Toast.makeText(this, "No valid emission years found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Filter countries to only those with emission data for the latest year.
        List<CountryEmission> filtered = new ArrayList<>();
        for (CountryEmission c : countries) {
            if (c.getCo2Emissions().getOrDefault(latestYear, 0L) > 0) filtered.add(c); //add this country
        }

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No countries with emissions data for " + latestYear, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Sort the filtered list in descending order by emissions for the latest year.
        // The lambda expression (a, b) -> ... is a short way to write a Comparator.
        // It compares two countries (a and b) by their emissions for latestYear.
        // Long.compare(x, y) returns a negative number if x < y, 0 if x == y, positive if x > y.
        // By comparing b to a (not a to b), we get descending order (highest first).
        filtered.sort((a, b) -> Long.compare(
                b.getCo2Emissions().getOrDefault(latestYear, 0L),
                a.getCo2Emissions().getOrDefault(latestYear, 0L)));

        // Keep only the top 10 polluters (or fewer if less than 10 countries).
        top10Countries = filtered.size() > 10 ? filtered.subList(0, 10) : filtered; //? concise if else
        // if > 10 use only index 0,10 if not use the whole fitered list

        // Show the first polluter in the UI.
        showPolluter(currentIndex);

        // Navigation buttons for the top 10 list.
        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--; //go back only if at first country
                showPolluter(currentIndex);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < top10Countries.size() - 1) {
                currentIndex++; //go forwards only if at last country
                showPolluter(currentIndex);
            }
        });

        // Show detailed info for the selected country.
        btnDetails.setOnClickListener(v -> {
            CountryEmission c = top10Countries.get(currentIndex);
            Intent intent = new Intent(this, CountryDetailsActivity.class);//pass data to this new screen
            intent.putExtra("countryName", c.getCountryName()); //adds the data to the intent
            startActivity(intent); //start new screen
        });

        // Show summary info for the selected country.
        btnFirstScreen.setOnClickListener(v -> {
            CountryEmission c = top10Countries.get(currentIndex);
            Intent intent = new Intent(this, FirstScreenActivity.class);
            intent.putExtra("countryName", c.getCountryName());
            startActivity(intent);
        });

        // Go to filter screen.
        btnFilter.setOnClickListener(v -> {
            startActivity(new Intent(this, FilterActivity.class));
        });

        // Search for a country by name and show its details.
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim().toLowerCase(); //.trim() remove space
            for (CountryEmission c : countries) {
                if (c.getCountryName().toLowerCase().equals(query)) { //check if same (case sensitive)
                    Intent intent = new Intent(this, CountryDetailsActivity.class);
                    intent.putExtra("countryName", c.getCountryName()); //put values into this intent
                    startActivity(intent);
                    return; //stop after first country match
                }
            }
            Toast.makeText(this, "Country not found", Toast.LENGTH_SHORT).show();
        });
    }

    // Helper method to find the most recent year in all countries' data.
    private int findLatestYear(List<CountryEmission> countries) {
        int latest = 0;
        for (CountryEmission c : countries) {
            for (int year : c.getCo2Emissions().keySet()) {
                if (year > latest) latest = year;
            }
        }
        return latest;
    }

    // Show the country and emission data for the given index in the top 10 list.
    private void showPolluter(int index) {
        CountryEmission c = top10Countries.get(index);
        tvCountry.setText((index + 1) + ". " + c.getCountryName()); // (index + 1) is used so the list is numbered from 1 to 10, not 0 to 9.
        tvEmission.setText("CO₂ Emissions (" + latestYear + "): " + c.getCo2Emissions().getOrDefault(latestYear, 0L));
    }
}
