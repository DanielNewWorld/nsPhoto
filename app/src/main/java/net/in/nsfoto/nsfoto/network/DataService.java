package net.in.nsfoto.nsfoto.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class DataService extends RetrofitGsonSpiceService {
    private final static String BASE_URL = "http://nsfoto.in.net/script";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

}