package com.humber.saynn.sinanmapsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    LayoutInflater layoutInflater;
    ArrayList<Weather> weathers;

    public ForecastAdapter(Context ctx,ArrayList<Weather> weathers){
        this.weathers = weathers;
        layoutInflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.forecast_item,parent,false);

        return new ForecastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Weather w = weathers.get(position);

        Picasso.get().load("https://openweathermap.org/img/wn/"+w.getIconURL()+"@2x.png").into(holder.forecastImage);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder{

        CardView forecastCard;
        LinearLayout forecastLayout;
        ImageView forecastImage;
        TextView forecastDescription;
        TextView forecastTemperature;
        TextView forecastFeels;
        TextView forecastTime;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            forecastCard = itemView.findViewById(R.id.forecastCard);
            forecastLayout = itemView.findViewById(R.id.forecastLayout);
            forecastImage = itemView.findViewById(R.id.forecastImage);
            forecastDescription = itemView.findViewById(R.id.forecastDescription);
            forecastTemperature = itemView.findViewById(R.id.forecastTemperature);
            forecastFeels = itemView.findViewById(R.id.forecastFeels);
            forecastTime = itemView.findViewById(R.id.forecastTime);
        }
    }
}
