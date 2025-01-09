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


public class HistoryFragment extends Fragment {

    final private BrainstormAdapter brainstormAdapter = new BrainstormAdapter();
    private BrainstormViewModel brainstormViewModel;
    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
        setEnterTransition(materialFadeThrough);
        setExitTransition(materialFadeThrough);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.brainStormsList);
        recyclerView.setAdapter(brainstormAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        SharedPreferences preferences = requireContext().getSharedPreferences("USER_DATA", 0);
        String token = preferences.getString("token", "null");
        brainstormViewModel = new BrainstormViewModel(requireActivity().getApplication(),
                token);

        brainstormViewModel.getAllBrainstorms().observe(getViewLifecycleOwner(),
                brainstormAdapter::submitList);

        brainstormViewModel.fetchDataFromApi();
    }
}