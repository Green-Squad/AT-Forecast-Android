package com.greensquad.atforecast.fragments;


import android.animation.Animator;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.greensquad.atforecast.APIController;
import com.greensquad.atforecast.ATForecastAPI;
import com.greensquad.atforecast.R;
import com.greensquad.atforecast.adapters.DailyWeatherAdapter;
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

import static android.text.format.DateUtils.getRelativeTimeSpanString;

public class ShelterDetailFragment extends BaseFragment implements BackButtonSupportFragment {
    private static final String LOG_TAG = ShelterDetailFragment.class.getSimpleName();
    private static final String ARG_SHELTER_ID = "shelter_id";
    private static  final int ANIM_DURATION = 300;

    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerView;
    private View loadingBar;
    private SwipeRefreshLayout swipeContainer;
    private TextView lastUpdatedTextView;

    private Shelter mShelter;
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shelter, container, false);

        constraintLayout = (ConstraintLayout) view.findViewById(R.id.shelter_constraint_layout);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        recyclerView = (RecyclerView) view.findViewById(R.id.shelter_recycler_view);
        lastUpdatedTextView = (TextView) view.findViewById(R.id.text_last_updated);
        loadingBar = getActivity().findViewById(R.id.loadingPanel);

        RecyclerViewHeader recyclerHeader = (RecyclerViewHeader) view.findViewById(R.id.shelter_recycler_header);
        AppCompatButton previousButton = (AppCompatButton) view.findViewById(R.id.previous_button);
        AppCompatButton nextButton = (AppCompatButton) view.findViewById(R.id.next_button);

        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                YoYo.with(Techniques.SlideOutRight)
                        .duration(ANIM_DURATION)
                        .repeat(1)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {}

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                refresh(true);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {}

                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        })
                        .playOn(constraintLayout);
            }
        });

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mShelter = Shelter.find(Shelter.class, "shelter_id = ?", mShelterId.toString()).get(0);
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

        previousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                renderShelter(previousShelter);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                renderShelter(nextShelter);
            }
        });

        recyclerHeader.attachTo(recyclerView);

        ArrayList<DailyWeather> dailyWeathers = new ArrayList<>();
        RecyclerView.Adapter mAdapter = new DailyWeatherAdapter(dailyWeathers);
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
                loadingBar.setVisibility(View.VISIBLE);
                refresh(false);
            } else {
                String lastUpdatedTime = (String) getRelativeTimeSpanString(updatedAtDate.getTime());
                lastUpdatedTime = getString(R.string.string_last_updated) + " " + lastUpdatedTime + ".";
                lastUpdatedTextView.setText(lastUpdatedTime);

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
            loadingBar.setVisibility(View.VISIBLE);
            refresh(false);
        }

        return view;
    }

    private void renderShelter(Shelter shelter) {
        int oldShelterId = mShelterId;
        mShelterId = shelter.getShelterId();

        ShelterDetailFragment fragment = (ShelterDetailFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main);

        if(mShelterId > oldShelterId) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.fragment_slide_left_enter,
                            R.anim.fragment_slide_left_exit,
                            R.anim.fragment_slide_right_enter,
                            R.anim.fragment_slide_right_exit)
                    .detach(fragment)
                    .attach(fragment)
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.fragment_slide_right_enter,
                            R.anim.fragment_slide_right_exit,
                            R.anim.fragment_slide_left_enter,
                            R.anim.fragment_slide_left_exit)
                    .detach(fragment)
                    .attach(fragment)
                    .commit();
        }

    }

    private void refresh(final boolean animationEnabled) {
        lastUpdatedTextView.setText(R.string.string_last_updated_now);
        final ArrayList<DailyWeather> dailyWeathers = new ArrayList<>();

        ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);
        Call<Shelter> call = apiService.getShelter(mShelterId, getString(R.string.atforecast_api_key));
        call.enqueue(new Callback<Shelter>() {
            @Override
            public void onResponse(Call<Shelter> call, Response<Shelter> response) {
                Shelter shelter = response.body();
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

                if(animationEnabled){
                    slideInAnimation();
                }
            }

            @Override
            public void onFailure(Call<Shelter> call, Throwable t) {
                loadingBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);

                Toast.makeText(getContext(), "Error loading content. Please try again.", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
                Log.e(LOG_TAG, t.toString());

                if(animationEnabled){
                    slideInAnimation();
                }
            }
        });
    }

    public void slideInAnimation() {
        YoYo.with(Techniques.SlideInLeft)
                .duration(ANIM_DURATION)
                .repeat(1)
                .playOn(constraintLayout);
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