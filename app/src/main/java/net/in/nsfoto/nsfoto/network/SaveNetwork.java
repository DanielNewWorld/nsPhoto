package net.in.nsfoto.nsfoto.network;

import net.in.nsfoto.nsfoto.model.DBSave;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;


/**
 * Created by root on 30.07.15.
 */

public interface SaveNetwork {
    @GET("/savescript.php")
    DBSave GetWallpapers(@Query("dbID") int dbID,
                         @Query("dbStatus") int dbStatus
    );

    @POST("/savescript.php")
    Void PostWallpapers(@Query("param1") double quantity, @Query("param2") String name, @Body DBSave data);
}
