package com.example.stormmasterclient;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stormmasterclient.helpers.RecyclerViewAdapters.BrainstormAdapter;
import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormViewModel;
import com.example.stormmasterclient.helpers.others.LoggerOut;
import com.google.android.material.transition.MaterialFade;
import com.google.android.material.transition.MaterialFadeThrough;


/**
 * Fragment for displaying the history of brainstorms.
 */
public class HistoryFragment extends Fragment {

    final private BrainstormAdapter brainstormAdapter = new BrainstormAdapter();

    /**
     * Required empty constructor for HistoryFragment.
     */
    public HistoryFragment() {
    }

    /**
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up transitions for the fragment
        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
        setEnterTransition(materialFadeThrough);
        setExitTransition(materialFadeThrough);
    }

    /**
     * Called when the fragment is first created.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the RecyclerView for displaying brainstorms
        RecyclerView recyclerView = view.findViewById(R.id.brainStormsList);
        recyclerView.setAdapter(brainstormAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Get the token from the shared preferences
        SharedPreferences preferences = requireContext().getSharedPreferences("USER_DATA", 0);
        String token = preferences.getString("token", "null");

        // Create a ViewModel for the brainstorms
        BrainstormViewModel brainstormViewModel = new BrainstormViewModel(requireActivity().getApplication(),
                token);

        // Observe the brainstorms and submit them to the adapter, so data will always be up-to-date
        brainstormViewModel.getAllBrainstorms().observe(getViewLifecycleOwner(),
                brainstormAdapter::submitList);

        // Fetch data from the API
        brainstormViewModel.fetchDataFromApi();
    }
}