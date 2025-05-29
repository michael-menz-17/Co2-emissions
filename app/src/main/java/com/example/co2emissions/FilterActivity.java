package com.example.co2emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;

import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private static final int MIN_YEAR = 1750;
    private static final int MAX_YEAR = 2020;

    private EditText etYear;
    private Button btnShow, btnBackHome;
    private TextView tvCountryName, tvTotalEmissions;

    private List<CountryEmission> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        etYear = findViewById(R.id.etYear);
        btnShow = findViewById(R.id.btnShow);
        btnBackHome = findViewById(R.id.btnBackHome);
        tvCountryName = findViewById(R.id.tvCountryName);
        tvTotalEmissions = findViewById(R.id.tvTotalEmissions);

        countries = CsvParser.parseCSV(this, "co2_emission_by_countries.csv");

        btnShow.setOnClickListener(v -> {
            String yearStr = etYear.getText().toString().trim();
            if (yearStr.isEmpty()) {
                Toast.makeText(this, "Please enter a year", Toast.LENGTH_SHORT).show();
                return;
            }
            int year;
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid year", Toast.LENGTH_SHORT).show();
                return;
            }
            if (year < MIN_YEAR || year > MAX_YEAR) {
                Toast.makeText(this, "Year must be between " + MIN_YEAR + " and " + MAX_YEAR, Toast.LENGTH_SHORT).show();
                return;
            }

            CountryEmission maxCountry = null;
            long maxEmission = -1L;  // Use long here
            for (CountryEmission c : countries) {
                long emission = c.getCo2Emissions().getOrDefault(year, 0L);
                if (emission > maxEmission) {
                    maxEmission = emission;
                    maxCountry = c;
                }
            }

            if (maxCountry != null && maxEmission > 0) {
                tvCountryName.setText("Country: " + maxCountry.getCountryName());
                tvTotalEmissions.setText("Total CO2 Emissions in " + year + ": " + maxEmission);
            } else {
                tvCountryName.setText("No data found for year " + year);
                tvTotalEmissions.setText("");
            }
        });

        btnBackHome.setOnClickListener(v -> finish());
    }
}
