package net.in.nsfoto.nsfoto.requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import net.in.nsfoto.nsfoto.model.ArrayListWallpaper;
import net.in.nsfoto.nsfoto.network.WallpaperNetwork;

/**
 * Created by root on 19.10.15.
 */
public class GetWallpaperRequest extends RetrofitSpiceRequest<ArrayListWallpaper, WallpaperNetwork> {
    private String strLogin;

    public GetWallpaperRequest(String strlogin) {
        super(ArrayListWallpaper.class, WallpaperNetwork.class);
        strLogin = strlogin;
    }

    @Override
    public ArrayListWallpaper loadDataFromNetwork() throws Exception {
        return getService().GetWallpapers(strLogin);
    }
}
