package com.tuna_salmon.androidapp.repositories;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class DashboardRepository {
    private static DashboardRepository s_instance;
    private DashboardRepository() {}

    @Provides
    @ViewModelScoped
    public static DashboardRepository getRepository() {
        if (s_instance == null)
            s_instance = new DashboardRepository();
        return s_instance;
    }
}
