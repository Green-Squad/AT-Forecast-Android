package com.greensquad.atforecast.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greensquad.atforecast.R;
import com.greensquad.atforecast.models.DailyWeather;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {
    private ArrayList<DailyWeather> mDailyWeathers;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView date;
        public TextView condition;
        public TextView temps;

        public ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date);
            condition = (TextView) v.findViewById(R.id.condition);
            temps = (TextView) v.findViewById(R.id.temps);
        }
    }

    public void add(int position, DailyWeather item) {
        mDailyWeathers.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mDailyWeathers.remove(position);
        notifyItemRemoved(position);
    }

    public DailyWeatherAdapter(ArrayList<DailyWeather> myDailyWeathers) {
        mDailyWeathers = myDailyWeathers;
    }

    @Override
    public DailyWeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_forecast, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DailyWeather dw = mDailyWeathers.get(position);
        String weatherDate = dw.getWeatherDate();
        Date parsedDate;
        String formattedDate;
        try {
            parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(weatherDate);
            formattedDate = new SimpleDateFormat("EEEE MMMM d, YYYY").format(parsedDate);
        } catch (ParseException pe) {
            formattedDate = weatherDate;
            pe.printStackTrace();
        }

        holder.date.setText(formattedDate);
        holder.condition.setText(dw.getDescription());
        holder.temps.setText(dw.getHigh() + "° / " + dw.getLow() + "°");
    }

    @Override
    public int getItemCount() {
        return mDailyWeathers.size();
    }

}