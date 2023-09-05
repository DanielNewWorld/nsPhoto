package net.in.nsfoto.nsfoto;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import net.in.nsfoto.nsfoto.model.ArrayListWallpaper;
import net.in.nsfoto.nsfoto.model.DBWallpaper;
import net.in.nsfoto.nsfoto.network.DataService;
import net.in.nsfoto.nsfoto.requests.GetWallpaperRequest;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Daniel on 22.11.2015.
 */
public class ViewNew extends Fragment
        {

    DBHelper dbHelper;
    private static final String TAG = "myLogs";
    SpiceManager mSpiceManager = new SpiceManager(DataService.class);

    final int chekErrorOK = 0;
    public int chekError = 20;

    private RecyclerView rv;
    private List<CardImage> persons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.view_new, null);

        rv=(RecyclerView)v.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        mSpiceManager.execute(new GetWallpaperRequest("users_android"), new GetWallpaperListener());

    return v;
    }

    class GetWallpaperListener implements RequestListener<ArrayListWallpaper> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            RetrofitError cause = (RetrofitError) spiceException.getCause();
            if (cause == null ||
                    cause.isNetworkError() ||
                    cause.getResponse() == null) {
                Toast.makeText(getActivity(), "Нет доступа к Интернету", Toast.LENGTH_LONG).show();
            }

            switch (cause.getResponse().getStatus()) {
                case HttpStatus.SC_UNAUTHORIZED:
                    Toast.makeText(getActivity(), "Неправильная авторизация", Toast.LENGTH_LONG).show();
                    break;
            }
            return;
        }

        @Override
        public void onRequestSuccess(ArrayListWallpaper dbItems) {

            if (dbItems == null) {
                Toast.makeText(getActivity(), "Нет даных!", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG, "Error: " + chekError);

            //txtInfo.setText(String.valueOf(chekError));

            //Заполняем данными динамический объект
            persons = new ArrayList<>();
            //Toast.makeText(Start.this, "j=" + String.valueOf(j), Toast.LENGTH_LONG).show();
            for (DBWallpaper i: dbItems) {
                persons.add(new CardImage("", "", "", 0, 0, null));
                chekError = i.chekError;
            }

            int j= dbItems.size();
            for (DBWallpaper i: dbItems) {
                j=j-1;
                persons.set(j, new CardImage(i.dbURL, i.dbURLAndroid, i.dbType, i.dbID, i.dbSave, null));
            }

            initializeAdapter();

            switch (chekError) {
                case chekErrorOK:
                    Log.d(TAG, "OK" + chekError);
                    Toast.makeText(getActivity(), "ОК", Toast.LENGTH_LONG).show();
                    break;

               /* case chekErrorDate:
                    Log.d(TAG, "За указанный период списания не найдены" + chekError);

                    break;*/

                default:
                    Log.d(TAG, "Неизвестная ошибка: " + chekError);
                    Toast.makeText(getActivity(), "Неизвестная ошибка", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(persons);
        rv.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mSpiceManager.start(getActivity());

        // создаем объект для создания и управления версиями БД
        //dbHelper = new DBHelper(activity);
    }

            @Override
            public void onStop() {
                super.onStop();
                mSpiceManager.shouldStop();
            }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, getString(R.string.name_BD), null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "--- onCreate database ---");
            // создаем таблицу с полями
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}