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
        setContentView(R.layout.activity_first_screen);

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

        TextView tvTotalValue = findViewById(R.id.tvTotalValue);
        TextView tvAvgValue = findViewById(R.id.tvAvgValue);
        TextView tvYearValue = findViewById(R.id.tvYearValue);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        btnBackHome.setOnClickListener(v -> finish());

        if (selected != null) {
            tvTotalValue.setText(String.valueOf(selected.getTotalEmissions()));
            tvAvgValue.setText(String.format("%.2f", selected.getAverageEmissionsPerCapita()));
            tvYearValue.setText(String.valueOf(selected.getYearWithHighestEmission()));
        }
    }
}
