package com.tuna_salmon.androidapp.services;

import android.content.Context;
import android.view.LayoutInflater;

import com.tuna_salmon.androidapp.local_database.LocalDatabase;

// Singleton
public class ServiceCollection {

    private static ServiceCollection s_serviceCollection;

    private AppService appService;
    private DataService dataService;
    private WebService webService;

    private ServiceCollection() {
    }

    public static ServiceCollection getServiceCollection() {
        if(s_serviceCollection == null)
            s_serviceCollection = new ServiceCollection();
        return s_serviceCollection;
    }

    public void Initialize(Context _ctx, LayoutInflater layoutInflater) {
        // Initialize Database
        LocalDatabase.Initialize(_ctx);

        // Initialize Services
        webService = new WebService();
        dataService = new DataService(webService, LocalDatabase.getDatabase());
        appService = new AppService(_ctx, layoutInflater, dataService);
    }

    public WebService getWebService() {
        return webService;
    }

    public DataService getDataService() {
        return dataService;
    }

    public AppService getAppService() {
        return appService;
    }


}
