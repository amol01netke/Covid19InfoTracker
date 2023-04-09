package com.example.covid_19infotracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String url = "https://api.rootnet.in/covid19-in/stats/latest";
    String location, tempLocation;
    String lastUpdatedValue, totalInfectedValue, totalDeathsValue, totalRecoveredValue;
    Spinner spinnerLocations;
    Button buttonTrackInfo;
    CardView cardView;
    TextView totalInfected, totalDeaths, totalRecovered, lastUpdated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        spinnerLocations = view.findViewById(R.id.spinner_locations);
        buttonTrackInfo = view.findViewById(R.id.button_track_info);
        lastUpdated = view.findViewById(R.id.text_last_updated);
        totalInfected = view.findViewById(R.id.text_total_infected_number);
        totalDeaths = view.findViewById(R.id.text_total_deaths_number);
        totalRecovered = view.findViewById(R.id.text_total_recovered_number);
        cardView = view.findViewById(R.id.card_view);

        buttonTrackInfo.setOnClickListener(view1 -> {
            new TrackData().execute();
        });

        return view;
    }

    private class TrackData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            location = spinnerLocations.getSelectedItem().toString();

            HttpHandler httpHandler = new HttpHandler();
            // making a request to url and getting response
            String jsonString = httpHandler.makeServiceCall(url);

            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    lastUpdatedValue = jsonObject.getString("lastRefreshed");

                    if (location.equalsIgnoreCase("India")) {
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("summary");
                        totalInfectedValue = jsonObject2.getString("total");
                        totalDeathsValue = jsonObject2.getString("deaths");
                        totalRecoveredValue = jsonObject2.getString("discharged");
                    }
                    else {
                        //get the array of data object
                        JSONArray jsonArray = jsonObject1.getJSONArray("regional");

                        // looping through all locations
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                            tempLocation = jsonObject3.getString("loc");

                            if (tempLocation.equalsIgnoreCase(location)) {
                                totalInfectedValue = jsonObject3.getString("totalConfirmed");
                                totalDeathsValue = jsonObject3.getString("deaths");
                                totalRecoveredValue = jsonObject3.getString("discharged");
                                break;
                            }
                        }

                    }
                } catch (final JSONException e) {
                    Toast.makeText(getContext(), "Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            cardView.setVisibility(View.VISIBLE);
            lastUpdated.setText(new StringBuilder().append("Last Updated:").append('\n').append(lastUpdatedValue).toString());
            totalInfected.setText(totalInfectedValue);
            totalDeaths.setText(totalDeathsValue);
            totalRecovered.setText(totalRecoveredValue);
        }
    }
}
