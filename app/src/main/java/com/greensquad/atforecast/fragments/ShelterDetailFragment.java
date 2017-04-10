package com.greensquad.atforecast.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greensquad.atforecast.APIController;
import com.greensquad.atforecast.ATForecastAPI;
import com.greensquad.atforecast.adapters.DailyWeatherAdapter;
import com.greensquad.atforecast.R;
import com.greensquad.atforecast.base.BackButtonSupportFragment;
import com.greensquad.atforecast.base.BaseFragment;
import com.greensquad.atforecast.models.DailyWeather;
import com.greensquad.atforecast.models.Shelter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShelterDetailFragment extends BaseFragment implements BackButtonSupportFragment {
    private static final String LOG_TAG = ShelterDetailFragment.class.getSimpleName();
    private static final String ARG_SHELTER_ID = "shelter_id";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

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
        final TextView shelterMile = (TextView) view.findViewById(R.id.shelter_mileage);
        final TextView shelterElevation = (TextView) view.findViewById(R.id.shelter_elevation);
        final ArrayList<DailyWeather> dailyWeathers = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.shelter_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);

        Call<Shelter> call = apiService.getShelter(mShelterId);
        call.enqueue(new Callback<Shelter>() {
            @Override
            public void onResponse(Call<Shelter> call, Response<Shelter> response) {
                Shelter shelter = response.body();
                mShelterName = shelter.getName();
                getActivity().setTitle(getTitle());

                shelterMile.setText(shelter.getMileage().toString());
                shelterElevation.setText(shelter.getElevation().toString());

                for (DailyWeather dailyWeather : shelter.getDailyWeather()) {
                    dailyWeathers.add(dailyWeather);
                }
                mAdapter = new DailyWeatherAdapter(dailyWeathers);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<Shelter>call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });
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

}
