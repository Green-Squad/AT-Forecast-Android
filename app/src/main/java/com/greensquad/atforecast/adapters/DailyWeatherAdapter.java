package com.greensquad.atforecast.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.greensquad.atforecast.R;
import com.greensquad.atforecast.models.DailyWeather;
import com.greensquad.atforecast.models.HourlyWeather;
import com.greensquad.atforecast.models.State;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.support.v4.content.ContextCompat.getColor;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {
    static final String LOG_TAG = DailyWeatherAdapter.class.getSimpleName();
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
        public ImageView weatherImage;

        public ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date);
            condition = (TextView) v.findViewById(R.id.condition);
            temps = (TextView) v.findViewById(R.id.temps);
            hourlyWeathers = (RecyclerView) v.findViewById(R.id.hourly_weather_recycler_view);
            hourlyWeatherTable = (TableLayout) v.findViewById(R.id.hourly_weather_table_layout);
            weatherImage = (ImageView) v.findViewById(R.id.weather_img);
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
        Log.d(LOG_TAG, "onBindViewHolder: " + position);
        DailyWeather dw = mDailyWeathers.get(position);
        String weatherDate = dw.getWeatherDate();
        Date parsedDate;
        String formattedDate;
        try {
            parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(weatherDate);
            formattedDate = new SimpleDateFormat("EEEE MMMM d, yyyy", Locale.US).format(parsedDate);
        } catch (ParseException pe) {
            formattedDate = weatherDate;
            pe.printStackTrace();
        }

        ArrayMap<String, String[]> weatherTypes = new ArrayMap<>();

        weatherTypes.put("clear", new String[] {"clear"});
        weatherTypes.put("rain", new String[] {"rain", "drizzle"});
        weatherTypes.put("snow", new String[] {"snow", "sleet"});
        weatherTypes.put("clouds", new String[] {"clouds"});
        weatherTypes.put("extreme", new String[] {"thunderstorm", "tornado", "hurricane", "tropical storm", "hail", "storm"});
        weatherTypes.put("fog", new String[] {"fog", "smoke", "haze", "mist"});

        String weatherType = null;
        for(String key : weatherTypes.keySet()) {
            String[] array = weatherTypes.get(key);
            for (String condition : array) {
                if (dw.getDescription().contains(condition)) {
                    weatherType = key;
                }
            }
            if (weatherType != null) {
                break;
            }
        }

        if (weatherType == null) {
            weatherType = "clear";
        }

        int imgId = context.getResources().getIdentifier(weatherType, "drawable", context.getPackageName());

        holder.weatherImage.setImageResource(imgId);
        holder.date.setText(formattedDate);
        holder.condition.setText(dw.getDescription());
        holder.temps.setText(dw.getHigh() + "° / " + dw.getLow() + "°");
        holder.hourlyWeathers.setHasFixedSize(true);

        ArrayList<HourlyWeather> hourlyWeatherArrayList = new ArrayList<>(dw.getHourlyWeatherFromDb());

        // remove views to prevent hourly weather duplication
        holder.hourlyWeatherTable.removeAllViews();

        // Header
        TableRow header = new TableRow(context);

        TextView hourHeader = new TextView(context);
        setRowHeader(hourHeader, "Time");

        hourHeader.setPadding(context.getResources().getDimensionPixelSize(R.dimen.row_padding), 0, 0, 0);
        header.addView(hourHeader);

        TextView tempHeader = new TextView(context);
        setRowHeader(tempHeader, "Temp");
        header.addView(tempHeader);

        TextView conditionHeader = new TextView(context);
        setRowHeader(conditionHeader, "Conditions");
        header.addView(conditionHeader);

        TextView windHeader = new TextView(context);
        setRowHeader(windHeader, "Wind");
        windHeader.setPadding(0, 0, context.getResources().getDimensionPixelSize(R.dimen.row_padding), 0);
        header.addView(windHeader);

        holder.hourlyWeatherTable.addView(header);

        if (hourlyWeatherArrayList.size() == 0) {
            TableRow border = getBorder();
            TableRow row = generateRow("N/A", "N/A", "No hourly weather available.", "N/A");

            holder.hourlyWeatherTable.addView(border);
            holder.hourlyWeatherTable.addView(row);
        }

        for (int i = 0; i < hourlyWeatherArrayList.size(); i++) {
            HourlyWeather hw = hourlyWeatherArrayList.get(i);

            String hourString = hw.getDate();
            Date parsedHourlyDate;
            String formattedHourlyDate;

            try {
                parsedHourlyDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(hourString);
                formattedHourlyDate = new SimpleDateFormat("h:mm a").format(parsedHourlyDate);
            } catch (ParseException pe) {
                formattedHourlyDate = hourString;
                pe.printStackTrace();
            }

            TableRow row = generateRow(formattedHourlyDate, hw.getTemp() + "°", hw.getDescription(), hw.getWind());
            TableRow border = getBorder();

            holder.hourlyWeatherTable.addView(border);
            holder.hourlyWeatherTable.addView(row);

        }
    }

    private TableRow generateRow(String hourStr, String tempStr, String conditionStr, String windStr) {
        TableRow row = new TableRow(context);

        TextView hour = new TextView(context);
        hour.setText(hourStr);
        hour.setPadding(context.getResources().getDimensionPixelSize(R.dimen.row_padding), 0, 0, 0);
        row.addView(hour);

        TextView temp = new TextView(context);
        temp.setText(tempStr);
        row.addView(temp);

        TextView condition = new TextView(context);
        condition.setText(conditionStr);
        condition.setHeight(context.getResources().getDimensionPixelSize(R.dimen.table_row_height));
        condition.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(condition);

        TextView wind = new TextView(context);
        wind.setText(windStr);
        wind.setPadding(0, 0, context.getResources().getDimensionPixelSize(R.dimen.row_padding), 0);
        row.addView(wind);

        return row;
    }

    private void setRowHeader(TextView view, String text) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        view.setTypeface(null, Typeface.BOLD);
        view.setTextColor(getColor(context, R.color.tableHeaderText));
        view.setHeight(context.getResources().getDimensionPixelSize(R.dimen.table_row_height));
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setText(text);
    }

    private TableRow getBorder() {
        TableRow border = new TableRow(context);
        border.setBackgroundColor(getColor(context, R.color.rowBorder));
        TextView borderView = new TextView(context);
        borderView.setHeight(context.getResources().getDimensionPixelSize(R.dimen.row_padding_height));
        border.addView(borderView);
        return border;
    }

    @Override
    public int getItemCount() {
        return mDailyWeathers.size();
    }

    public void refill(ArrayList<DailyWeather> dailyWeathers) {
        Log.d(LOG_TAG, "refill");
        mDailyWeathers.clear();
        mDailyWeathers.addAll(dailyWeathers);
        notifyDataSetChanged();
    }

}