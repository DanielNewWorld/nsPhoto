package net.in.nsfoto.nsfoto;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
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

import net.in.nsfoto.nsfoto.model.DBSave;
import net.in.nsfoto.nsfoto.network.DataService;
import net.in.nsfoto.nsfoto.requests.GetSaveRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit.RetrofitError;

/**
 * Created by Daniel on 22.11.2015.
 */
public class ViewWallpaper extends Fragment {

    TextView txtStar;
    ImageView imgView;
    Button btnSave;
    Button btnWallpaper;

    DBHelper dbHelper;
    private static final String TAG = "myLogs";
    SpiceManager SaveSpiceManager = new SpiceManager(DataService.class);

    final int chekErrorView = 0;
    final int chekErrorSave = 1;
    public int chekError = 20;

    final int statusView = 0;
    final int statusSave = 1;

    String imageURL;
    String imageURLAndroid;
    //String imageName;
    String imageType;
    int imageID;

    final int PIC_CROP = 2;
    private Uri picUri;
    private Uri outputFileUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_wallpaper, null);

        txtStar = (TextView) v.findViewById(R.id.txtStar);
        imgView = (ImageView) v.findViewById(R.id.imgView);
        btnSave = (Button) v.findViewById(R.id.btnSave);
        btnWallpaper = (Button) v.findViewById(R.id.btnWallpaper);

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(TAG, "--- Rows in mytable: ---");
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int imageIDIndex = c.getColumnIndex("imageID");
            int imageURLIndex = c.getColumnIndex("imageURL");
            int imageURLAndroidIndex = c.getColumnIndex("imageURLAndroid");
            int imageTypeIndex = c.getColumnIndex("imageType");

            do {
                imageID = c.getInt(imageIDIndex);
                imageURL = c.getString(imageURLIndex);
                imageURLAndroid = c.getString(imageURLAndroidIndex);
                txtStar.setText("id: " + c.getString(imageIDIndex) + "\nТип: " + c.getString(imageTypeIndex));

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d(TAG, "0 rows");
        c.close();

        Picasso.with(imgView.getContext()) //передаем контекст приложения
                .load(imageURL) //адрес изображения
                .into(imgView); //ссылка на ImageView

        SaveSpiceManager.execute(new GetSaveRequest(imageID, statusView), new GetSaveListener());

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Работает диалог, не удалять
                /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image*//*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }*/

            }
        });

        btnWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ViewWallpaper.this, "Выберите область фото", Toast.LENGTH_LONG).show();

                //Activity activity = getActivity();
                //Определяет размеры экрана
                //DisplayMetrics metrics = new DisplayMetrics();
                //actigetWindowManager().getDefaultDisplay().getMetrics(metrics);
                // get the height and width of screen
                //int height = metrics.heightPixels;
                //int width = metrics.widthPixels;
                //Toast toast = Toast.makeText(getActivity(), height + "  " + width, Toast.LENGTH_SHORT);

                try {
                    // Намерение для кадрирования. Не все устройства поддерживают его
                    picUri = Uri.parse("file://mnt/sdcard/input.jpg");
                    //picUri = Uri.parse(imageURLAndroid);
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(picUri, "image*//*");
                    cropIntent.putExtra("crop", "true");
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    cropIntent.putExtra("outputX", 256);
                    cropIntent.putExtra("outputY", 256);
                    cropIntent.putExtra("return-data", true);
                    //куда сохраняем
                    File file = new File(Environment.getExternalStorageDirectory(),"output.jpg");
                    outputFileUri = Uri.fromFile(file);
                    cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cropIntent, PIC_CROP);
                }
                catch(ActivityNotFoundException anfe){
                    String errorMessage = "Извините, но ваше устройство не поддерживает кадрирование";
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                }
            }
        });
    return v;
    }

    class GetSaveListener implements RequestListener<DBSave> {
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
        public void onRequestSuccess(DBSave dbItems) {

            if (dbItems == null) {
                Toast.makeText(getActivity(), "Нет даных!", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG, "Error: " + chekError);

            chekError = dbItems.chekError;

            switch (chekError) {
                case chekErrorView:
                    Log.d(TAG, "View" + chekError);
                    Toast.makeText(getActivity(), "View", Toast.LENGTH_LONG).show();
                    break;

                case chekErrorSave:
                    Log.d(TAG, "Save" + chekError);
                    Toast.makeText(getActivity(), "Save", Toast.LENGTH_LONG).show();
                    break;

                default:
                    Log.d(TAG, "Неизвестная ошибка: " + chekError);
                    Toast.makeText(getActivity(), "Неизвестная ошибка", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(activity);

        SaveSpiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        SaveSpiceManager.shouldStop();

        // закрываем подключение к БД
        dbHelper.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == RESULT_OK) {
            //Вернулись от приложения Камера
            //if (requestCode == CAMERA_CAPTURE) {            }
            // Вернулись из операции кадрирования
            //else
            if(requestCode == PIC_CROP) {
                Uri imageUri = null;

                if (data != null) {
                    /*if (data.hasExtra("data")) {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        // TODO Какие-то действия с миниатюрой
                        Bundle extras = data.getExtras();
                        // Получим кадрированное изображение
                        Bitmap thePic = extras.getParcelable("data");
                        // передаём его в ImageView
                        imgView.setImageBitmap(thePic);
                    }*/
                }

                // TODO Какие-то действия с полноценным изображением,
                // сохраненным по адресу outputFileUri
                Activity activity = getActivity();
                activity.setTitle("круто!");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), outputFileUri);
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
                    wallpaperManager.setBitmap(bitmap);
                    Toast.makeText(getActivity(), "Обои установлены!" + outputFileUri, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    // handle exception
                    Toast.makeText(activity, "Что-то пошло не так!" + outputFileUri, Toast.LENGTH_LONG).show();
                }
                SaveSpiceManager.execute(new GetSaveRequest(imageID, statusSave), new GetSaveListener());
            }
        //}
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