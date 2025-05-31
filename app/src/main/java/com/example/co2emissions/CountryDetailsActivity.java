package com.example.co2emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CountryDetailsActivity extends AppCompatActivity {
    // onCreate is called by Android when this screen is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // // super.onCreate calls the parent class's code, which is required for the activity to work correctly.
        setContentView(R.layout.activity_country_details); // setContentView tells Android which XML layout file to use for this screen's UI.

        // Intents are Android's way of passing data between screens.
        // getIntent() gets the Intent that started this activity.
        // getStringExtra("countryName") gets the value for the key "countryName" (sent from another activity).
        String countryName = getIntent().getStringExtra("countryName");
        if (countryName == null) { //if there is nothing the screen is closed
            finish();
            return;
        }
        // create a list from my CsvParser for reading a CSV file and turning it into a list of CountryEmission objects.

        List<CountryEmission> countries = CsvParser.parseCSV(this, "co2_emission_by_countries.csv");
        CountryEmission selected = null;
        for (CountryEmission c : countries) { // Find the country in the list that matches the passed name (ignoring case)
            if (c.getCountryName().equalsIgnoreCase(countryName)) {
                selected = c;
                break;
            }
        }
        // findViewById connects the code to UI elements defined in the XML layout.

        TextView tvCountryName = findViewById(R.id.tvCountryName);
        TextView tvPopulation = findViewById(R.id.tvPopulation);
        TextView tvArea = findViewById(R.id.tvArea);
        TextView tvDensity = findViewById(R.id.tvDensity);
        TextView tvPercentageWorld = findViewById(R.id.tvPercentageWorld);
        TextView tvTotalEmissions = findViewById(R.id.tvTotalEmissions);
        TextView tvAvgPerCapita = findViewById(R.id.tvAvgPerCapita);
        TextView tvYearHighest = findViewById(R.id.tvYearHighest);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        // setOnClickListener attaches code to run when the button is clicked.
        // finish() closes this screen and returns to the previous one.
        btnBackHome.setOnClickListener(v -> finish());

        if (selected != null) { //  Only update the UI if the country was found
            tvCountryName.setText(selected.getCountryName()); //set the variables with the values found
            tvPopulation.setText(String.valueOf(selected.getPopulation())); //set the variables with the values found

            // Get the density as a string
            String densityStr = selected.getDensity();
            String areaStr = "N/A"; //default if nothing is found
            try {
                double density = Double.parseDouble(densityStr.replaceAll("[^\\d.]", "")); // removes all non-digit and non dot characters
                if (density != 0) {
                    double area = selected.getPopulation() / density;
                    areaStr = String.format("%.2f", area); //formats the number to 2 decimal places.
                }
            } catch (Exception ignored) {} //if nothing is found area = n/a

            // Set all the calculated and retrieved values in the UI.
            tvArea.setText(areaStr);
            tvDensity.setText(densityStr);
            tvPercentageWorld.setText(selected.getPercentageOfWorld());
            tvTotalEmissions.setText(String.valueOf(selected.getTotalEmissions()));
            tvAvgPerCapita.setText(String.format("%.2f", selected.getAverageEmissionsPerCapita()));
            tvYearHighest.setText(String.valueOf(selected.getYearWithHighestEmission()));
        }
    }
}
