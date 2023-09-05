package net.in.nsfoto.nsfoto.requests;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import net.in.nsfoto.nsfoto.model.DBSave;
import net.in.nsfoto.nsfoto.network.SaveNetwork;

/**
 * Created by root on 19.10.15.
 */
public class GetSaveRequest extends RetrofitSpiceRequest<DBSave, SaveNetwork> {
    private int mdbID;
    private int mstatus;

    public GetSaveRequest(int dbid, int status) {
        super(DBSave.class, SaveNetwork.class);
        mdbID = dbid;
        mstatus = status;
    }

    @Override
    public DBSave loadDataFromNetwork() throws Exception {
        return getService().GetWallpapers(mdbID, mstatus);
    }
}
