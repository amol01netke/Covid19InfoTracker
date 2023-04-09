package com.example.covid_19infotracker;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HelpFragment extends Fragment {
    private static final String url = "https://api.rootnet.in/covid19-in/contacts";
    private static final String query = "geo:0,0?q=vaccine centres near me";
    private static final String bookSlotURL = "https://selfregistration.cowin.gov.in/";
    String location, tempLocation;
    String helplineNo;
    Spinner spinnerLocations;
    Button buttonVaccineCentres, buttonBookSlot, buttonHelplineNo, buttonMyGov;
    ImageButton buttonCall;
    TextView textHelplineNo;
    CardView cardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        textHelplineNo = (TextView) view.findViewById(R.id.text_helpline_no);
        spinnerLocations = (Spinner) view.findViewById(R.id.spinner_locations_3);
        buttonVaccineCentres = (Button) view.findViewById(R.id.button_vaccine_centres);
        buttonBookSlot = (Button) view.findViewById(R.id.button_book_vaccination_slot);
        buttonHelplineNo = (Button) view.findViewById(R.id.button_helpline_no);
        buttonMyGov = (Button) view.findViewById(R.id.button_mygov);
        buttonCall = (ImageButton) view.findViewById(R.id.button_call);
        cardView = (CardView) view.findViewById(R.id.card_view_1);

        buttonVaccineCentres.setOnClickListener(view2 -> {
            openMap();
        });

        buttonBookSlot.setOnClickListener(view3 -> {
            bookSlot();
        });

        buttonHelplineNo.setOnClickListener(view1 -> {
            new GetHelplineNo().execute();
        });

        buttonCall.setOnClickListener(view4 -> {
            makePhoneCall();
        });

        buttonMyGov.setOnClickListener(view5 -> {
            myGovHelpdesk();
        });
        return view;
    }

    private void openMap() {
        Uri uri = Uri.parse(query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


    private void bookSlot() {
        Intent bookSlotIntent = new Intent(Intent.ACTION_VIEW);
        bookSlotIntent.setData(Uri.parse(bookSlotURL));
        startActivity(bookSlotIntent);
    }


    private class GetHelplineNo extends AsyncTask<Void, Void, Void> {

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
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("contacts");
                    JSONArray jsonArray = jsonObject2.getJSONArray("regional");

                    // looping through all locations
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                        tempLocation = jsonObject3.getString("loc");
                        if (tempLocation.equalsIgnoreCase(location)) {
                            helplineNo = jsonObject3.getString("number");
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
            cardView.setVisibility(View.VISIBLE);
            textHelplineNo.setText(helplineNo);
        }
    }

    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder permissionDialog = new AlertDialog.Builder(getContext());
            permissionDialog.setTitle("Grant Permission!");
            permissionDialog.setMessage("Allow this app to make call");

            permissionDialog.setPositiveButton("Yes", (dialog, yes) -> {
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                startActivity(settingsIntent);
            });

            permissionDialog.setNegativeButton("No", (dialog, no) -> {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            });

            permissionDialog.create().show();
        } else {
            String number = "tel:" + helplineNo;
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(number));
            startActivity(callIntent);
        }
    }

    private void myGovHelpdesk() {
        String contactNumber = "+91 90131 51515";
        String url = "https://api.whatsapp.com/send?phone=" + contactNumber;
        Intent whatsAppIntent = new Intent(Intent.ACTION_VIEW);
        whatsAppIntent.setPackage("com.whatsapp");
        whatsAppIntent.setData(Uri.parse(url));
        startActivity(whatsAppIntent);
    }
}