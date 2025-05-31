package com.example.co2emissions;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import java.util.List;


public class FilterActivity extends AppCompatActivity {

    // The earliest and latest years allowed for filtering.
    private static final int MIN_YEAR = 1750;
    private static final int MAX_YEAR = 2020;

    private EditText etYear; // Where the user types the year.
    private Button btnShow, btnBackHome; // Buttons to show result and go back.
    private TextView tvCountryName, tvTotalEmissions; // For displaying results.

    // List of all countries loaded from the CSV.
    private List<CountryEmission> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter); // Set the UI layout for this screen.

        // Connect UI elements to code using their IDs from the XML layout.
        etYear = findViewById(R.id.etYear);
        btnShow = findViewById(R.id.btnShow);
        btnBackHome = findViewById(R.id.btnBackHome);
        tvCountryName = findViewById(R.id.tvCountryName);
        tvTotalEmissions = findViewById(R.id.tvTotalEmissions);

        // Load all country data from the CSV file.
        countries = CsvParser.parseCSV(this, "co2_emission_by_countries.csv");

        // When the user clicks the button...
        btnShow.setOnClickListener(v -> {
            String yearStr = etYear.getText().toString().trim(); // Get the year entered by the user.
            if (yearStr.isEmpty()) {
                // Show a message if the user didn't enter anything.
                Toast.makeText(this, "Please enter a year", Toast.LENGTH_SHORT).show(); //notification
                return;
            }
            int year;
            try {
                year = Integer.parseInt(yearStr); // Try to convert the input to an integer.
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid year", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if the year is within the valid range.
            if (year < MIN_YEAR || year > MAX_YEAR) {
                Toast.makeText(this, "Year must be between " + MIN_YEAR + " and " + MAX_YEAR, Toast.LENGTH_SHORT).show();
                return;
            }

            // Find the country with the highest emission for the selected year.
            CountryEmission maxCountry = null;
            long maxEmission = -1L;  // Start with -1 so any real emission is higher.
            for (CountryEmission c : countries) {
                // getOrDefault(year, 0L) returns the emission for the year, or 0 if not present.
                long emission = c.getCo2Emissions().getOrDefault(year, 0L);
                if (emission > maxEmission) {
                    maxEmission = emission;
                    maxCountry = c;
                }
            }

            // Display the result or a message if no data was found.
            if (maxCountry != null && maxEmission > 0) {
                tvCountryName.setText("Country: " + maxCountry.getCountryName());
                tvTotalEmissions.setText("Total CO2 Emissions in " + year + ": " + maxEmission);
            } else {
                tvCountryName.setText("No data found for year " + year);
                tvTotalEmissions.setText("");
            }
        });

        // When the user clicks "Back Home", close this screen and return to the previous one.
        btnBackHome.setOnClickListener(v -> finish());
    }
}
