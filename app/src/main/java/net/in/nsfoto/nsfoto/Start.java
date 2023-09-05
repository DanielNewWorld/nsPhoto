package net.in.nsfoto.nsfoto;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.in.nsfoto.nsfoto.model.ArrayListWallpaper;
import net.in.nsfoto.nsfoto.model.DBWallpaper;
import net.in.nsfoto.nsfoto.network.DataService;
import net.in.nsfoto.nsfoto.requests.GetWallpaperRequest;

import org.apache.http.HttpStatus;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

public class Start extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RVAdapter.OnCardClikedListener {

    private static final String TAG = "myLogs";
    private static long back_pressed;

    private CharSequence mTitle;
    FragmentTransaction fTrans;
    ViewNew fragViewNew;
    ViewWallpaper fragViewWallpaper;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //http://developer.android.com/intl/ru/guide/components/intents-common.html для отправки email
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mTitle = getString(persons.get(viewindex.getId()).imageURLAndroid);
        fTrans = getFragmentManager().beginTransaction();
        if (fragViewNew == null) fragViewNew = new ViewNew();
        fTrans.replace(R.id.lnrContent, fragViewNew);
        fTrans.addToBackStack(null);
        fTrans.commit();

        //setTitle("Работает");
    }

    @Override
    public void onCardCliked(int imageID, String imageURL, String imageURLAndroid, String imageType) {
        //Toast.makeText(Start.this, String.valueOf(viewindex.getId()), Toast.LENGTH_SHORT).show();

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // подготовим данные для вставки в виде пар: наименование столбца - значение
        cv.put("imageID", imageID);
        cv.put("imageURL", imageURL);
        cv.put("imageURLAndroid", imageURLAndroid);
        cv.put("imageType", imageType);

        // вставляем запись и получаем ее ID
        long rowID = db.insert("mytable", null, cv);
        Log.d(TAG, "row inserted, ID = " + rowID);

        // закрываем подключение к БД
        dbHelper.close();

        //mTitle = getString(persons.get(viewindex.getId()).imageURLAndroid);
        fTrans = getFragmentManager().beginTransaction();
        if (fragViewWallpaper == null) fragViewWallpaper = new ViewWallpaper();
        fTrans.replace(R.id.lnrContent, fragViewWallpaper);
        fTrans.addToBackStack("ViewNew");
        fTrans.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mSpiceManager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mSpiceManager.shouldStop();
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis())
        {
            //    super.onBackPressed();
            moveTaskToBack(true);
            finish();
            System.runFinalizersOnExit(true);
            System.exit(0);
        }
        else
            Toast.makeText(getBaseContext(), "Нажмите еще раз, чтобы выйти!",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    //Navigator menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //mTitle = getString(persons.get(viewindex.getId()).imageURLAndroid);
            fTrans = getFragmentManager().beginTransaction();
            if (fragViewNew == null) fragViewNew = new ViewNew();
            fTrans.replace(R.id.lnrContent, fragViewNew);
            fTrans.addToBackStack(null);
            fTrans.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new) {
            // Handle the camera action
            //mTitle = getString(persons.get(viewindex.getId()).imageURLAndroid);
            fTrans = getFragmentManager().beginTransaction();
            if (fragViewNew == null) fragViewNew = new ViewNew();
            fTrans.replace(R.id.lnrContent, fragViewNew);
            fTrans.addToBackStack(null);
            fTrans.commit();
        }

        /*else if (id == R.id.nav_Gun_Suk) {

        } else if (id == R.id.nav_Pak) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_no_reklama) {

        } else if (id == R.id.nav_premium) {

        } else if (id == R.id.nav_like) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Navigator menu close

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, getString(R.string.name_BD), null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "imageID integer,"
                    + "imageURL text,"
                    + "imageURLAndroid text,"
                    + "imageName text,"
                    + "imageType text"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}

/*
Uri address = Uri.parse("http://developer.alexanderklimov.ru");
Intent openlink = new Intent(Intent.ACTION_VIEW, address);
    startActivity(openlink);*/
