package com.greensquad.atforecast.fragments;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.greensquad.atforecast.APIController;
import com.greensquad.atforecast.ATForecastAPI;
import com.greensquad.atforecast.R;
import com.greensquad.atforecast.adapters.DailyWeatherAdapter;
import com.greensquad.atforecast.adapters.StateAdapter;
import com.greensquad.atforecast.base.BackButtonSupportFragment;
import com.greensquad.atforecast.base.BaseFragment;
import com.greensquad.atforecast.models.DailyWeather;
import com.greensquad.atforecast.models.HourlyWeather;
import com.greensquad.atforecast.models.Shelter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShelterDetailFragment extends BaseFragment implements BackButtonSupportFragment {
    private static final String LOG_TAG = ShelterDetailFragment.class.getSimpleName();
    private static final String ARG_SHELTER_ID = "shelter_id";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private View loadingBar;
    private SwipeRefreshLayout swipeContainer;

    private Integer mShelterId;
    private String mShelterName;

    public ShelterDetailFragment() {}

    public static ShelterDetailFragment newInstance(Integer shelterId) {
        ShelterDetailFragment fragment = new ShelterDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SHELTER_ID, shelterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mShelterId = getArguments().getInt(ARG_SHELTER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shelter, container, false);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimary);

        loadingBar = getActivity().findViewById(R.id.loadingPanel);

        recyclerView = (RecyclerView) view.findViewById(R.id.shelter_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        ArrayList<DailyWeather> dailyWeathers = new ArrayList<>();
        mAdapter = new DailyWeatherAdapter(dailyWeathers);
        recyclerView.setAdapter(mAdapter);

        List<DailyWeather> dailyWeatherQuery = DailyWeather.find(DailyWeather.class, "shelter_id = ?", mShelterId.toString());
        if (dailyWeatherQuery.size() > 0) {
            Date updatedAtDate = dailyWeatherQuery.get(0).getUpdatedAt();
            int minutesUntilRefresh = 2 * 60;
            int millisecondsUntilRefresh = 1000 * 60 * minutesUntilRefresh;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(updatedAtDate);
            long updatedAtDateInMillis = calendar.getTimeInMillis();
            Date timeToUpdate = new Date(updatedAtDateInMillis + millisecondsUntilRefresh);

            Date currentDate = new Date(System.currentTimeMillis());

            if(currentDate.after(timeToUpdate)) {
                Log.d(LOG_TAG, "Time to refresh");
                loadingBar.setVisibility(View.VISIBLE);
                refresh();
            } else {
                Log.d(LOG_TAG, "Daily Weather > 0");
                List<Shelter> shelterQuery = Shelter.find(Shelter.class, "shelter_id = ?", mShelterId.toString());
                Shelter shelter = shelterQuery.get(0);
                mShelterName = shelter.getName();
                getActivity().setTitle(getTitle());
                for (DailyWeather dailyWeather : dailyWeatherQuery) {
                    dailyWeathers.add(dailyWeather);
                }
                mAdapter = new DailyWeatherAdapter(dailyWeathers);
                recyclerView.setAdapter(mAdapter);
            }
        } else {
            Log.d(LOG_TAG, "Daily Weather == 0");
            loadingBar.setVisibility(View.VISIBLE);
            refresh();
        }
        return view;
    }

    @Override
    protected String getTitle() {
        return mShelterName;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void refresh() {
        final ArrayList<DailyWeather> dailyWeathers = new ArrayList<>();
        ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);
        Call<Shelter> call = apiService.getShelter(mShelterId, getString(R.string.atforecast_api_key));
        call.enqueue(new Callback<Shelter>() {
            @Override
            public void onResponse(Call<Shelter> call, Response<Shelter> response) {
                Shelter shelter = response.body();
                Log.d(LOG_TAG, shelter.getShelterId().toString());
                mShelterName = shelter.getName();
                getActivity().setTitle(getTitle());

                DailyWeather.deleteAll(DailyWeather.class, "shelter_id = ?", mShelterId + "");
                for (DailyWeather dailyWeather : shelter.getDailyWeather()) {
                    dailyWeathers.add(dailyWeather);
                    dailyWeather.save();
                    HourlyWeather.deleteAll(HourlyWeather.class, "daily_weather_id = ?", dailyWeather.getDailyWeatherId() + "");
                    for (HourlyWeather hourlyWeather : dailyWeather.getHourlyWeather()) {
                        hourlyWeather.setDailyWeatherId(dailyWeather.getDailyWeatherId());
                        hourlyWeather.save();
                    }
                }
                loadingBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);
                ((DailyWeatherAdapter)recyclerView.getAdapter()).refill(dailyWeathers);
            }

            @Override
            public void onFailure(Call<Shelter> call, Throwable t) {
                loadingBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);
                Toast.makeText(getContext(), "Error loading content. Please try again.", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

}
