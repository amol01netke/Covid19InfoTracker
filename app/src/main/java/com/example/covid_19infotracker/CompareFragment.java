package com.example.covid_19infotracker;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CompareFragment extends Fragment {
    private static final String url = "https://api.rootnet.in/covid19-in/stats/latest";
    String location1, location2, parameter, tempLocation;
    String totalInfectedValue1, totalDeathsValue1, totalRecoveredValue1;
    String totalInfectedValue2, totalDeathsValue2, totalRecoveredValue2;
    Spinner spinnerLocations1, spinnerLocations2, spinnerParameters;
    Button buttonCompareInfo;
    BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);

        spinnerLocations1 = view.findViewById(R.id.spinner_locations_1);
        spinnerLocations2 = view.findViewById(R.id.spinner_locations_2);
        spinnerParameters = view.findViewById(R.id.spinner_parameters);
        buttonCompareInfo = view.findViewById(R.id.button_compare_info);
        barChart = view.findViewById(R.id.comparison_chart);

        buttonCompareInfo.setOnClickListener(view1 -> {
            new CompareData().execute();
        });
        return view;
    }

    private class CompareData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            location1 = spinnerLocations1.getSelectedItem().toString();
            location2 = spinnerLocations2.getSelectedItem().toString();

            HttpHandler httpHandler = new HttpHandler();
            // making a request to url and getting response
            String jsonString = httpHandler.makeServiceCall(url);

            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("summary");
                    JSONArray jsonArray = jsonObject1.getJSONArray("regional");

                    //for spinner 1
                    if (location1.equalsIgnoreCase("India")) {
                        totalInfectedValue1 = jsonObject2.getString("total");
                        totalDeathsValue1 = jsonObject2.getString("deaths");
                        totalRecoveredValue1 = jsonObject2.getString("discharged");
                    } else {
                        // looping through all locations
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                            tempLocation = jsonObject3.getString("loc");
                            if (tempLocation.equalsIgnoreCase(location1)) {
                                totalInfectedValue1 = jsonObject3.getString("totalConfirmed");
                                totalDeathsValue1 = jsonObject3.getString("deaths");
                                totalRecoveredValue1 = jsonObject3.getString("discharged");
                                break;
                            }
                        }
                    }

                    //for spinner2
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                        tempLocation = jsonObject3.getString("loc");
                        if (tempLocation.equalsIgnoreCase(location2)) {
                            totalInfectedValue2 = jsonObject3.getString("totalConfirmed");
                            totalDeathsValue2 = jsonObject3.getString("deaths");
                            totalRecoveredValue2 = jsonObject3.getString("discharged");
                            break;
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

            int region1Data, region2Data;
            parameter = spinnerParameters.getSelectedItem().toString();
            if (parameter.equalsIgnoreCase("Total Infected")) {
                region1Data = Integer.parseInt(totalInfectedValue1);
                region2Data = Integer.parseInt(totalInfectedValue2);
            } else if (parameter.equalsIgnoreCase("Total Deaths")) {
                region1Data = Integer.parseInt(totalDeathsValue1);
                region2Data = Integer.parseInt(totalDeathsValue2);
            } else {
                region1Data = Integer.parseInt(totalRecoveredValue1);
                region2Data = Integer.parseInt(totalRecoveredValue2);
            }

            ArrayList<BarEntry> data = new ArrayList<>();
            data.add(new BarEntry(1, region1Data));
            data.add(new BarEntry(2, region2Data));

            BarDataSet barDataSet = new BarDataSet(data, parameter);
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSet.setBarBorderColor(Color.BLACK);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(15f);

            BarData barData = new BarData(barDataSet);

            barChart.setVisibility(View.VISIBLE);
            barChart.setData(barData);
            barChart.animateY(2000);
            barChart.getDescription().setText(parameter);
            barChart.setFitBars(true);
        }
    }
}