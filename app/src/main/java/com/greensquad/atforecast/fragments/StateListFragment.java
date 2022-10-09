package com.greensquad.atforecast.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.greensquad.atforecast.APIController;
import com.greensquad.atforecast.ATForecastAPI;
import com.greensquad.atforecast.R;
import com.greensquad.atforecast.adapters.StateAdapter;
import com.greensquad.atforecast.base.BackButtonSupportFragment;
import com.greensquad.atforecast.base.BaseFragment;
import com.greensquad.atforecast.models.Shelter;
import com.greensquad.atforecast.models.State;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StateListFragment extends BaseFragment implements BackButtonSupportFragment {
    private static final int ANIM_DURATION = 300;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;

    public StateListFragment() {
    }

    public static StateListFragment newInstance() {
        return new StateListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state_list, container, false);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        recyclerView = view.findViewById(R.id.main_recycler_view);

        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
        swipeContainer.setOnRefreshListener(this::reloadShelter);

        final ArrayList<State> states = new ArrayList<>();

        recyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        List<State> dbStates = State.listAll(State.class);

        RecyclerView.Adapter<StateAdapter.ViewHolder> mAdapter = new StateAdapter(new ArrayList<>(dbStates));
        recyclerView.setAdapter(mAdapter);

        if (dbStates.isEmpty() || Shelter.listAll(Shelter.class).get(0).getElevation() == null) {
            swipeContainer.setRefreshing(true);

            ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);
            Call<List<State>> request = apiService.getStates(true, getString(R.string.atforecast_api_key));
            request.enqueue(new Callback<List<State>>() {
                @Override
                public void onResponse(Call<List<State>> request, Response<List<State>> response) {
                    List<State> statesList = response.body();
                    for (State state : statesList) {
                        states.add(state);
                        state.save();
                        List<Shelter> shelters = state.getShelters();
                        for (Shelter shelter : shelters) {
                            shelter.setStateId(state.getStateId());
                            shelter.save();
                        }
                    }

                    swipeContainer.setRefreshing(false);
                    ((StateAdapter) recyclerView.getAdapter()).refill(states);
                }

                @Override
                public void onFailure(Call<List<State>> request, Throwable t) {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(getContext(), "Error loading content. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Date updatedAtDate = dbStates.get(0).getUpdatedAt();
            int minutesUntilRefresh = 12 * 60;
            int millisecondsUntilRefresh = 1000 * 60 * minutesUntilRefresh;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(updatedAtDate);
            long updatedAtDateInMillis = calendar.getTimeInMillis();

            Date timeToUpdate = new Date(updatedAtDateInMillis + millisecondsUntilRefresh);
            Date currentDate = new Date(System.currentTimeMillis());

            if (currentDate.after(timeToUpdate)) {
                refreshData();
            }
        }

        return view;
    }

    private void refreshData() {
        swipeContainer.setRefreshing(true);

        ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);
        Call<List<State>> request = apiService.getStates(false, getString(R.string.atforecast_api_key));

        request.enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> request, Response<List<State>> response) {
                ArrayList<State> states = new ArrayList<>();
                List<State> statesList = response.body();
                for (State state : statesList) {
                    states.add(state);
                    state.save();
                }

                ((StateAdapter) recyclerView.getAdapter()).refill(states);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<State>> request, Throwable t) {
                swipeContainer.setRefreshing(false);
                Toast.makeText(getContext(), "Error loading content. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reloadShelter() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fragment_bottom_slide_exit);
        recyclerView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                recyclerView.setVisibility(View.GONE);
                refreshData();
                new Handler().postDelayed(() -> {
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
        recyclerView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    protected String getTitle() {
        return "AT Forecast";
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}