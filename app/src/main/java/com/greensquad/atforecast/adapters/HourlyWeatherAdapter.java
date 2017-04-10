package com.greensquad.atforecast.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greensquad.atforecast.R;
import com.greensquad.atforecast.models.HourlyWeather;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {
    private ArrayList<HourlyWeather> mHourlyWeathers;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView date;
        public TextView condition;
        public TextView temp;
        public TextView wind;

        public ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.hourly_weather_date);
            condition = (TextView) v.findViewById(R.id.hourly_weather_condition);
            temp = (TextView) v.findViewById(R.id.hourly_weather_temp);
            wind = (TextView) v.findViewById((R.id.hourly_weather_wind));
        }
    }

    public void add(int position, HourlyWeather item) {
        mHourlyWeathers.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mHourlyWeathers.remove(position);
        notifyItemRemoved(position);
    }

    public HourlyWeatherAdapter(ArrayList<HourlyWeather> myHourlyWeathers) {
        mHourlyWeathers = myHourlyWeathers;
    }

    @Override
    public HourlyWeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_hourly_weather, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HourlyWeather hw = mHourlyWeathers.get(position);

        String hour = hw.getDate();
        Date parsedDate;
        String formattedDate;
        try {
            parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(hour);
            formattedDate = new SimpleDateFormat("h:mm a").format(parsedDate);
        } catch (ParseException pe) {
            formattedDate = hour;
            pe.printStackTrace();
        }

        holder.date.setText(formattedDate);
        holder.temp.setText(hw.getTemp() + "°");
        holder.condition.setText(hw.getDescription());
        holder.wind.setText(hw.getWind() + " mph");
    }

    @Override
    public int getItemCount() {
        return mHourlyWeathers.size();
    }

}