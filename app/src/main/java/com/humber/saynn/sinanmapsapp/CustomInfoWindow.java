package com.humber.saynn.sinanmapsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final View window;
    private Context context;

    public CustomInfoWindow(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.custom_marker_info, null);
    }

    private void setTexts(Marker m, View v) {

        TextView titleText = v.findViewById(R.id.titleText);
        TextView descriptionText = v.findViewById(R.id.descriptionText);

        if (m != null) {
            String title = m.getTitle();
            if (!title.equals("")) {
                titleText.setText(title);
            }
            String description = m.getSnippet();
            if (description != null) {
                if (!description.equals("")) {
                    descriptionText.setText(description);
                }
            }

        } else {
            titleText.setText("Location");
            descriptionText.setText("Marker selected.");
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        setTexts(marker, window);
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        setTexts(marker, window);
        return null;
    }
}
