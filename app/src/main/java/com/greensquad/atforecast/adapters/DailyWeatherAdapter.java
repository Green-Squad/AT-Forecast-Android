package com.greensquad.atforecast.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.greensquad.atforecast.R;
import com.greensquad.atforecast.models.DailyWeather;
import com.greensquad.atforecast.models.HourlyWeather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {
    private ArrayList<DailyWeather> mDailyWeathers;
    private ViewGroup mParent;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView date;
        public TextView condition;
        public TextView temps;
        public RecyclerView hourlyWeathers;
        public TableLayout hourlyWeatherTable;

        public ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date);
            condition = (TextView) v.findViewById(R.id.condition);
            temps = (TextView) v.findViewById(R.id.temps);
            hourlyWeathers = (RecyclerView) v.findViewById(R.id.hourly_weather_recycler_view);
            hourlyWeatherTable = (TableLayout) v.findViewById(R.id.hourly_weather_table_layout);
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
        mParent = parent;
        context = parent.getContext();
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
        holder.hourlyWeathers.setHasFixedSize(true);

        ArrayList<HourlyWeather> hourlyWeatherArrayList = new ArrayList<>(dw.getHourlyWeather());

        for (int i = 0; i < hourlyWeatherArrayList.size(); i++) {
            HourlyWeather hw = hourlyWeatherArrayList.get(i);

            String hourString = hw.getDate();
            Date parsedHourlyDate;
            String formattedHourlyDate;
            try {
                parsedHourlyDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(hourString);
                formattedHourlyDate = new SimpleDateFormat("h:mm a").format(parsedHourlyDate);
            } catch (ParseException pe) {
                formattedHourlyDate = hourString;
                pe.printStackTrace();
            }

            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView hour = new TextView(context);
            hour.setText(formattedHourlyDate);
            hour.setPadding(5, 5, 5, 5);
            row.addView(hour);

            TextView temp = new TextView(context);
            temp.setText(hw.getTemp() + "°");
            temp.setPadding(5, 5, 5, 5);
            row.addView(temp);

            TextView condition = new TextView(context);
            condition.setText(hw.getDescription());
            condition.setPadding(5, 5, 5, 5);
            row.addView(condition);

            TextView wind = new TextView(context);
            wind.setText(hw.getWind());
            wind.setPadding(5, 5, 5, 5);
            row.addView(wind);

            holder.hourlyWeatherTable.addView(row);
        }
    }

    @Override
    public int getItemCount() {
        return mDailyWeathers.size();
    }

}