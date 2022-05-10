package com.tuna_salmon.androidapp.inventory.bulk_edit;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tuna_salmon.androidapp.R;

public class BulkEditFragment extends Fragment {

    private BulkEditViewModel mViewModel;

    public static BulkEditFragment newInstance() {
        return new BulkEditFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bulk_edit_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BulkEditViewModel.class);
        // TODO: Use the ViewModel
    }

}