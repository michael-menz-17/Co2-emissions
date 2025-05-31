package com.example.co2emissions;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class FirstScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen); // Set the layout for this screen.

        // Get the country name passed from another activity (e.g., HomeActivity) using an Intent.
        String countryName = getIntent().getStringExtra("countryName");
        if (countryName == null) {
            finish(); // If no country was passed, close this screen.
            return;
        }

        // Load all countries from the CSV file.
        List<CountryEmission> countries = CsvParser.parseCSV(this, "co2_emission_by_countries.csv");
        CountryEmission selected = null;
        // Find the country object matching the passed name.
        for (CountryEmission c : countries) {
            if (c.getCountryName().equalsIgnoreCase(countryName)) {
                selected = c;
                break;
            }
        }

        // Find UI elements in the layout.
        TextView tvTotalValue = findViewById(R.id.tvTotalValue); // Shows total emissions.
        TextView tvAvgValue = findViewById(R.id.tvAvgValue);     // Shows average per capita.
        TextView tvYearValue = findViewById(R.id.tvYearValue);   // Shows year with highest emission.
        Button btnBackHome = findViewById(R.id.btnBackHome);     // Button to go back.

        // When the user clicks back, close this screen.
        btnBackHome.setOnClickListener(v -> finish());

        // If the country was found, show its statistics.
        if (selected != null) {
            tvTotalValue.setText(String.valueOf(selected.getTotalEmissions()));
            tvAvgValue.setText(String.format("%.2f", selected.getAverageEmissionsPerCapita()));
            tvYearValue.setText(String.valueOf(selected.getYearWithHighestEmission()));
        }
    }
}
