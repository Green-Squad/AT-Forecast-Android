package com.greensquad.atforecast.fragments;


import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

public class StateListFragment extends BaseFragment implements BackButtonSupportFragment{
    private static final String LOG_TAG = StateListFragment.class.getSimpleName();
    private static  final int ANIM_DURATION = 300;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private View loadingBar;

    public StateListFragment() {}

    public static StateListFragment newInstance() { return new StateListFragment(); }

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
        loadingBar = getActivity().findViewById(R.id.loadingPanel);

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
                                refresh();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {}

                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        })
                        .playOn(recyclerView);
            }
        });

        final ArrayList<State> states = new ArrayList<>();

        recyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        List<State> dbStates = State.listAll(State.class);

        RecyclerView.Adapter mAdapter = new StateAdapter(new ArrayList<>(dbStates));
        recyclerView.setAdapter(mAdapter);

        if (dbStates.isEmpty()) {
            ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);
            Call<List<State>> call = apiService.getStates(true, getString(R.string.atforecast_api_key));

            loadingBar.setVisibility(View.VISIBLE);

            call.enqueue(new Callback<List<State>>() {
                @Override
                public void onResponse(Call<List<State>> call, Response<List<State>> response) {
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

                    loadingBar.setVisibility(View.GONE);
                    ((StateAdapter)recyclerView.getAdapter()).refill(states);
                }

                @Override
                public void onFailure(Call<List<State>>call, Throwable t) {
                    loadingBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error loading content. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, t.toString());
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

            if(currentDate.after(timeToUpdate)) {
                loadingBar.setVisibility(View.VISIBLE);
                refresh();
            }
        }

        return view;
    }

    private void refresh() {
        ATForecastAPI apiService = APIController.getClient().create(ATForecastAPI.class);
        Call<List<State>> call = apiService.getStates(false, getString(R.string.atforecast_api_key));

        call.enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> call, Response<List<State>> response) {
                ArrayList<State> states = new ArrayList<>();
                List<State> statesList = response.body();
                for (State state : statesList) {
                    states.add(state);
                    state.save();
                }

                loadingBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);
                ((StateAdapter)recyclerView.getAdapter()).refill(states);

                slideInAnimation();
            }

            @Override
            public void onFailure(Call<List<State>>call, Throwable t) {
                loadingBar.setVisibility(View.GONE);
                swipeContainer.setRefreshing(false);
                Toast.makeText(getContext(), "Error loading content. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, t.toString());

                slideInAnimation();
            }
        });
    }

    public void slideInAnimation() {
        YoYo.with(Techniques.SlideInLeft)
                .duration(ANIM_DURATION)
                .repeat(1)
                .playOn(recyclerView);
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
