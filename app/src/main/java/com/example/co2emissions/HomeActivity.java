package com.example.co2emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import java.util.*;

public class HomeActivity extends AppCompatActivity {

    private int latestYear = 0;

    private List<CountryEmission> countries;
    private List<CountryEmission> top10Countries;
    private int currentIndex = 0;

    private TextView tvCountry, tvEmission;
    private Button btnPrev, btnNext, btnDetails, btnFirstScreen, btnFilter;
    private EditText etSearch;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvCountry = findViewById(R.id.tvCountry);
        tvEmission = findViewById(R.id.tvEmission);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnDetails = findViewById(R.id.btnDetails);
        btnFirstScreen = findViewById(R.id.btnFirstScreen);
        btnFilter = findViewById(R.id.btnFilter);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        countries = CsvParser.parseCSV(this, "co2_emission_by_countries.csv");

        if (countries.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        latestYear = findLatestYear(countries);
        if (latestYear == 0) {
            Toast.makeText(this, "No valid emission years found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        List<CountryEmission> filtered = new ArrayList<>();
        for (CountryEmission c : countries) {
            if (c.getCo2Emissions().getOrDefault(latestYear, 0L) > 0) filtered.add(c);
        }

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No countries with emissions data for " + latestYear, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        filtered.sort((a, b) -> Long.compare(
                b.getCo2Emissions().getOrDefault(latestYear, 0L),
                a.getCo2Emissions().getOrDefault(latestYear, 0L)));

        top10Countries = filtered.size() > 10 ? filtered.subList(0, 10) : filtered;

        showPolluter(currentIndex);

        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showPolluter(currentIndex);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < top10Countries.size() - 1) {
                currentIndex++;
                showPolluter(currentIndex);
            }
        });

        btnDetails.setOnClickListener(v -> {
            CountryEmission c = top10Countries.get(currentIndex);
            Intent intent = new Intent(this, CountryDetailsActivity.class);
            intent.putExtra("countryName", c.getCountryName());
            startActivity(intent);
        });

        btnFirstScreen.setOnClickListener(v -> {
            CountryEmission c = top10Countries.get(currentIndex);
            Intent intent = new Intent(this, FirstScreenActivity.class);
            intent.putExtra("countryName", c.getCountryName());
            startActivity(intent);
        });

        btnFilter.setOnClickListener(v -> {
            startActivity(new Intent(this, FilterActivity.class));
        });

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim().toLowerCase();
            for (CountryEmission c : countries) {
                if (c.getCountryName().toLowerCase().equals(query)) {
                    Intent intent = new Intent(this, CountryDetailsActivity.class);
                    intent.putExtra("countryName", c.getCountryName());
                    startActivity(intent);
                    return;
                }
            }
            Toast.makeText(this, "Country not found", Toast.LENGTH_SHORT).show();
        });
    }

    private int findLatestYear(List<CountryEmission> countries) {
        int latest = 0;
        for (CountryEmission c : countries) {
            for (int year : c.getCo2Emissions().keySet()) {
                if (year > latest) latest = year;
            }
        }
        return latest;
    }

    private void showPolluter(int index) {
        CountryEmission c = top10Countries.get(index);
        tvCountry.setText((index + 1) + ". " + c.getCountryName());
        tvEmission.setText("CO₂ Emissions (" + latestYear + "): " + c.getCo2Emissions().getOrDefault(latestYear, 0L));
    }
}
