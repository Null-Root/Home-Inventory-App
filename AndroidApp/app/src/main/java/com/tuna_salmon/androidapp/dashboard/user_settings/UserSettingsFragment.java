package com.tuna_salmon.androidapp.dashboard.user_settings;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tuna_salmon.androidapp.Const;
import com.tuna_salmon.androidapp.R;
import com.tuna_salmon.androidapp.ext.ExtFunctions;
import com.tuna_salmon.androidapp.services.AppService;
import com.tuna_salmon.androidapp.services.ServiceCollection;

public class UserSettingsFragment extends Fragment implements UserSettingsListener {
    private AppService.Data appDataService;
    private AppService.UI appUIService;

    private NavController navController;
    private UserSettingsViewModel mViewModel;

    private Button Change_ID;
    private Button Delete_Account;

    private ImageView LiveLockState;
    private ImageView SetStateToLock;
    private ImageView SetStateToUnlock;

    private TextView LiveVisibilityState;
    private Spinner SetVisibilityState;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_userSettingsFragment_to_dashboardFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appDataService = ServiceCollection.getServiceCollection().getAppService().getAppDataService();
        appUIService = ServiceCollection.getServiceCollection().getAppService().getAppUIService();
        return inflater.inflate(R.layout.user_settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mViewModel = new ViewModelProvider(this).get(UserSettingsViewModel.class);
        mViewModel.userSettingsListener = this;

        Change_ID = view.findViewById(R.id.userSettingsChangeIDButton);
        Delete_Account = view.findViewById(R.id.userSettingsDeleteAccountButton);
        LiveLockState = view.findViewById(R.id.settingsLiveLockState);
        SetStateToLock = view.findViewById(R.id.settingsSetStateToLock);
        SetStateToUnlock = view.findViewById(R.id.settingsSetStateToUnlock);
        LiveVisibilityState = view.findViewById(R.id.settingsVisibilityState);
        SetVisibilityState = view.findViewById(R.id.settingsSetVisibilityState);

        mViewModel.GetLockState();
        mViewModel.GetVisibilityState();

        Change_ID.setOnClickListener(v -> {
            AlertDialog builder = new AlertDialog.Builder(getContext())
                    .setTitle("Change ID")
                    .setMessage("Do You Want To Change Your Public ID?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mViewModel.Change_ID();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create();
            builder.show();
        });

        SetStateToLock.setOnClickListener(v -> {
            mViewModel.SetLockState(true);
        });

        SetStateToUnlock.setOnClickListener(v -> {
            mViewModel.SetLockState(false);
        });

        SetVisibilityState.setSelection(0, false);
        SetVisibilityState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(view != null) {
                    mViewModel.SetVisibilityState(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Delete_Account.setOnClickListener(v -> {
            AlertDialog builder = new AlertDialog.Builder(getContext())
                    .setTitle("Delete Account")
                    .setMessage("Do You Want To Permanently Delete Your Account?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mViewModel.Delete_Account();

                        // Delete Data Locally
                        if(ExtFunctions.isVarSet(appDataService.getAppData().get(Const.ID_Type.USER_ID)))
                            appDataService.setIDInstance(null, null);
                        else
                            appDataService.setAppData(Const.ID_Type.USER_ID, null);

                        appUIService.simpleDialog("Account Action", "Account Deleted!");

                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create();
            builder.show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void OnChangeIDCallback(LiveData<String> liveData) {
        liveData.observe(this, s -> {
            appUIService.endLoadingAnimation();
        });
    }

    @Override
    public void OnChangeLockStateCallback(LiveData<String> liveData) {
        liveData.observe(this, s -> {
            appUIService.endLoadingAnimation();
        });
    }

    @Override
    public void OnChangeVisibilityStateCallback(LiveData<String> liveData) {
        liveData.observe(this, s -> {
            appUIService.endLoadingAnimation();
        });
    }

    @Override
    public void OnDeleteAccountCallback(LiveData<String> liveData) {
        liveData.observe(this, s -> {
            appUIService.endLoadingAnimation();
        });
    }
}