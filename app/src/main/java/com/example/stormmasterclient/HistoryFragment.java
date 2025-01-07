package com.example.stormmasterclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.transition.MaterialFade;
import com.google.android.material.transition.MaterialFadeThrough;


public class HistoryFragment extends Fragment {

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
}