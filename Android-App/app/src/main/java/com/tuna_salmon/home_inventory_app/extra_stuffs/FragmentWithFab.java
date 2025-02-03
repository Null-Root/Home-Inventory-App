package com.tuna_salmon.home_inventory_app.extra_stuffs;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class FragmentWithFab extends Fragment {

    public View FabOverlay;
    public FloatingActionButton Main_Fab;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
