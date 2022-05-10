package com.tuna_salmon.androidapp.services;

import com.tuna_salmon.androidapp.local_database.LocalDatabase;

public class DataService {
    private WebService _webService;
    private LocalDatabase _localDatabase;

    public DataService(WebService _webService, LocalDatabase _localDatabase) {
        this._webService = _webService;
        this._localDatabase = _localDatabase;
    }
}
