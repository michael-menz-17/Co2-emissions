package com.example.co2emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CountryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);

        String countryName = getIntent().getStringExtra("countryName");
        if (countryName == null) {
            finish();
            return;
        }

        List<CountryEmission> countries = CsvParser.parseCSV(this, "co2_emission_by_countries.csv");
        CountryEmission selected = null;
        for (CountryEmission c : countries) {
            if (c.getCountryName().equalsIgnoreCase(countryName)) {
                selected = c;
                break;
            }
        }

        TextView tvCountryName = findViewById(R.id.tvCountryName);
        TextView tvPopulation = findViewById(R.id.tvPopulation);
        TextView tvArea = findViewById(R.id.tvArea);
        TextView tvDensity = findViewById(R.id.tvDensity);
        TextView tvPercentageWorld = findViewById(R.id.tvPercentageWorld);
        TextView tvTotalEmissions = findViewById(R.id.tvTotalEmissions);
        TextView tvAvgPerCapita = findViewById(R.id.tvAvgPerCapita);
        TextView tvYearHighest = findViewById(R.id.tvYearHighest);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        btnBackHome.setOnClickListener(v -> finish());

        if (selected != null) {
            tvCountryName.setText(selected.getCountryName());
            tvPopulation.setText(String.valueOf(selected.getPopulation()));

            String densityStr = selected.getDensity();
            String areaStr = "N/A";
            try {
                double density = Double.parseDouble(densityStr.replaceAll("[^\\d.]", ""));
                if (density != 0) {
                    double area = selected.getPopulation() / density;
                    areaStr = String.format("%.2f", area);
                }
            } catch (Exception ignored) {}

            tvArea.setText(areaStr);
            tvDensity.setText(densityStr);
            tvPercentageWorld.setText(selected.getPercentageOfWorld());
            tvTotalEmissions.setText(String.valueOf(selected.getTotalEmissions()));
            tvAvgPerCapita.setText(String.format("%.2f", selected.getAverageEmissionsPerCapita()));
            tvYearHighest.setText(String.valueOf(selected.getYearWithHighestEmission()));
        }
    }
}
