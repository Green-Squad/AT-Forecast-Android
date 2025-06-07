package com.greensquad.atforecast.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.greensquad.atforecast.APIController;
import com.greensquad.atforecast.ATForecastAPI;
import com.greensquad.atforecast.R;
import com.greensquad.atforecast.adapters.DailyWeatherAdapter;
import com.greensquad.atforecast.base.BackButtonSupportFragment;
import com.greensquad.atforecast.base.BaseFragment;
import com.greensquad.atforecast.models.DailyWeather;
import com.greensquad.atforecast.models.HourlyWeather;
import com.greensquad.atforecast.models.Shelter;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

public class ShelterDetailFragment extends BaseFragment implements BackButtonSupportFragment {
    private static final String LOG_TAG = ShelterDetailFragment.class.getSimpleName();
    private static final String ARG_SHELTER_ID = "shelter_id";
    private static final Integer DIST_MILES = 100;
    private static final int ANIM_DURATION = 300;
    private static final int MINUTES_UNTIL_REFRESH = 2 * 60;
    private static ATForecastAPI apiService;
    private static ExecutorService executor;

    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerView;
    private View loadingBar;
    private SwipeRefreshLayout swipeContainer;
    private TextView lastUpdatedTextView;
    private TextView mileageTextView;
    private TextView elevationTextView;

    private Shelter mShelter;
    private Integer mShelterId;
    private String mShelterName;
    private List<DailyWeather> dailyWeatherQuery;

    public ShelterDetailFragment() {
    }

    public static ShelterDetailFragment newInstance(Integer shelterId) {
        ShelterDetailFragment fragment = new ShelterDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SHELTER_ID, shelterId);
        fragment.setArguments(args);
        executor = Executors.newSingleThreadExecutor();
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
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_shelter, container, false);

        apiService = APIController.getClient().create(ATForecastAPI.class);

        constraintLayout = view.findViewById(R.id.shelter_constraint_layout);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        recyclerView = view.findViewById(R.id.shelter_recycler_view);
        lastUpdatedTextView = view.findViewById(R.id.text_last_updated);
        mileageTextView = view.findViewById(R.id.text_mileage);
        elevationTextView = view.findViewById(R.id.text_elevation);
        loadingBar = getActivity().findViewById(R.id.loadingPanel);

        RecyclerViewHeader recyclerHeader = view.findViewById(R.id.shelter_recycler_header);
        AppCompatButton previousButton = view.findViewById(R.id.previous_button);
        AppCompatButton nextButton = view.findViewById(R.id.next_button);

        if (mShelter == null) {
            mShelter = Select.from(Shelter.class)
                    .where(Condition.prop("shelter_id").eq(mShelterId))
                    .first();
        }

        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
        swipeContainer.setOnRefreshListener(this::reloadShelter);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        if (mShelter.getMileage() != null) {
            mileageTextView.setText(getString(R.string.mileage_text, mShelter.getMileage().toString()));
        } else {
            mileageTextView.setText(getString(R.string.mileage_placeholder_text));
        }
        if (mShelter.getElevation() != null) {
            elevationTextView.setText(getString(R.string.elevation_text, mShelter.getElevation().toString()));
        } else {
            elevationTextView.setText(getString(R.string.elevation_placeholder_text));
        }

        final Shelter previousShelter = mShelter.getPrevious();
        final Shelter nextShelter = mShelter.getNext();

        if (previousShelter == null) {
            previousButton.setAlpha(.5f);
            previousButton.setEnabled(false);
        }

        if (nextShelter == null) {
            nextButton.setAlpha(.5f);
            nextButton.setEnabled(false);
        }

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderShelter(mShelter, previousShelter);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderShelter(mShelter, nextShelter);
            }
        });

        recyclerHeader.attachTo(recyclerView);

        ArrayList<DailyWeather> dailyWeathers = new ArrayList<>();
        RecyclerView.Adapter mAdapter = new DailyWeatherAdapter(mShelter, dailyWeathers);
        recyclerView.setAdapter(mAdapter);

        dailyWeatherQuery = DailyWeather.find(DailyWeather.class, "shelter_id = ?", mShelterId.toString());
        if (dailyWeatherQuery.size() > 0) {
            Date updatedAtDate = dailyWeatherQuery.get(0).getUpdatedAt();

            String lastUpdatedTime = (String) getRelativeTimeSpanString(updatedAtDate.getTime());
            lastUpdatedTime = getString(R.string.string_last_updated) + " " + lastUpdatedTime + ".";
            lastUpdatedTextView.setText(lastUpdatedTime);

            mShelterName = mShelter.getName();
            getActivity().setTitle(getTitle());

            for (DailyWeather dailyWeather : dailyWeatherQuery) {
                dailyWeathers.add(dailyWeather);
            }

            mAdapter = new DailyWeatherAdapter(mShelter, dailyWeathers);
            recyclerView.setAdapter(mAdapter);

            int millisecondsUntilRefresh = 1000 * 60 * MINUTES_UNTIL_REFRESH;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(updatedAtDate);
            long updatedAtDateInMillis = calendar.getTimeInMillis();

            Date timeToUpdate = new Date(updatedAtDateInMillis + millisecondsUntilRefresh);
            Date currentDate = new Date(System.currentTimeMillis());

            if (currentDate.after(timeToUpdate)) {
                loadingBar.setVisibility(View.VISIBLE);
                refreshShelterData();
            } else {
                loadingBar.setVisibility(View.GONE);
            }
        } else {
            loadingBar.setVisibility(View.VISIBLE);
            refreshShelterData();
        }

        return view;
    }

    private void reloadShelter() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fragment_bottom_slide_exit);
        constraintLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                constraintLayout.setVisibility(View.GONE);
                refreshShelterData();
                new Handler().postDelayed(() -> {
                    loadingBar.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                    fadeUpFragment();
                }, ANIM_DURATION);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void fadeUpFragment() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fragment_bottom_slide_enter);
        constraintLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                constraintLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void renderShelter(Shelter currentShelter, Shelter newShelter) {
        mShelterId = newShelter.getShelterId();
        mShelter = newShelter;

        FragmentManager manager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (newShelter.getMileage() > currentShelter.getMileage()) {
            transaction.setCustomAnimations(
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_right_exit
            );
        } else {
            transaction.setCustomAnimations(
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_right_exit,
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit
            );
        }

        transaction
                .detach(this)
                .attach(this)
                .commit();
    }

    private void refreshShelterData() {
        Call<List<Shelter>> shelterRequest = apiService.getShelter(mShelterId, getString(R.string.atforecast_api_key), null);
        shelterRequest.enqueue(new Callback<List<Shelter>>() {
            @Override
            public void onResponse(Call<List<Shelter>> request, Response<List<Shelter>> response) {
                final List<Shelter> shelters = response.body();
                Shelter shelter = shelters.get(0);
                mShelterName = shelter.getName();
                getActivity().setTitle(getTitle());

                ArrayList<DailyWeather> dailyWeathers = new ArrayList<>(shelter.getDailyWeather());

                loadingBar.setVisibility(View.GONE);
                lastUpdatedTextView.setText(R.string.string_last_updated_now);

                ((DailyWeatherAdapter) recyclerView.getAdapter()).refill(dailyWeathers);
                storeShelterData(dailyWeathers);
            }

            @Override
            public void onFailure(Call<List<Shelter>> request, Throwable t) {
                String toastMessage;
                if (dailyWeatherQuery.size() == 0) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                    toastMessage = "Sorry, there was no offline data available and we could not load new data for this shelter.";
                } else {
                    toastMessage = "Sorry we could not load the weather for this shelter. Please try again.";
                }
                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                loadingBar.setVisibility(View.GONE);
            }
        });

        Call<List<Shelter>> massShelterRequest = apiService.getShelter(mShelterId, getString(R.string.atforecast_api_key), DIST_MILES);
        massShelterRequest.enqueue(new Callback<List<Shelter>>() {
            @Override
            public void onResponse(Call<List<Shelter>> request, Response<List<Shelter>> response) {
                final List<Shelter> shelters = response.body();

                executor.execute(() -> {
                    //Background work here
                    //todo need some kind of last updated or is updating thing
                    storeMassHistoryData(shelters);
                });
            }

            @Override
            public void onFailure(Call<List<Shelter>> request, Throwable t) {
                Toast.makeText(getContext(), "Sorry, we could not load the next " + DIST_MILES + " miles. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeShelterData(ArrayList<DailyWeather> dailyWeathers) {
        DailyWeather.deleteAll(DailyWeather.class, "shelter_id = ?", mShelterId + "");
        for (DailyWeather dailyWeather : dailyWeathers) {
            dailyWeather.save();
            HourlyWeather.deleteAll(HourlyWeather.class, "daily_weather_id = ?", dailyWeather.getDailyWeatherId() + "");
            for (HourlyWeather hourlyWeather : dailyWeather.getHourlyWeather()) {
                hourlyWeather.setDailyWeatherId(dailyWeather.getDailyWeatherId());
                hourlyWeather.save();
            }
        }
    }

    private void storeMassHistoryData(List<Shelter> shelters) {
        for (Shelter shelter : shelters) {
            DailyWeather.deleteAll(DailyWeather.class, "shelter_id = ?", shelter.getShelterId() + "");
            for (DailyWeather dailyWeather : shelter.getDailyWeather()) {
                dailyWeather.save();
                HourlyWeather.deleteAll(HourlyWeather.class, "daily_weather_id = ?", dailyWeather.getDailyWeatherId() + "");
                for (HourlyWeather hourlyWeather : dailyWeather.getHourlyWeather()) {
                    hourlyWeather.setDailyWeatherId(dailyWeather.getDailyWeatherId());
                    hourlyWeather.save();
                }
            }
        }
    }

    @Override
    protected String getTitle() {
        return mShelterName;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.detail_menu, menu);
    }
}