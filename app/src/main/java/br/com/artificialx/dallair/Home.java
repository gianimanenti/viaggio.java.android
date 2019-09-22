
package br.com.artificialx.dallair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
//import br.com.artificialx.dallair.Utils.BottomNavigationViewHelper;
import br.com.artificialx.dallair.Utils.GridImageAdapter;
import br.com.artificialx.dallair.Utils.MapViewActivity;
import br.com.artificialx.dallair.Utils.UniversalImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import br.com.artificialx.dallair.*;

import static br.com.artificialx.dallair.Nearby.MY_PERMISSIONS_REQUEST_LOCATION;
import static java.security.AccessController.getContext;

/**
 * Created by User on 5/28/2017.
 */

public class Home extends AppCompatActivity {
    private static final String TAG = "Home";
    private static final int ACTIVITY_NUM = 4;

    private Context mContext = Home.this;

    public static Picasso picassoWithCache;


    Bitmap userPic;

    ArrayList<String> feedImg = new ArrayList<>();
    ArrayList<String> feedInfo = new ArrayList<>();
    ArrayList<Integer> feedScore = new ArrayList<>();

    Integer feedLength = 0;

    String profileImg = "";
    String sessionId;
    String mEmail = "";



    boolean flag=false;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;
    Activity mActivity = Home.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        // Log.d(TAG, "onCreate: started.");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);



        final CircleImageView profile = findViewById(R.id.profile_image);

        final Bundle extras = getIntent().getExtras();



        final Intent i1 = new Intent(Home.this, Nearby.class);

        final Intent i2 = new Intent(Home.this, Places.class);

        final Intent i3 = new Intent(Home.this, mainMenu.class);

        final Intent i4 = new Intent(Home.this, User.class);

        if (extras != null) {

           // sessionId = extras.getString("key");
            sessionId = "johnmanenti@gmail.com";
            getUserFromDB(sessionId);

            i1.putExtra("key", sessionId);

            i2.putExtra("key", sessionId);

            i3.putExtra("key", sessionId);

            i4.putExtra("key", sessionId);
        }

        profileImg = "https:/artificialx.com.br/projetos/viaggio/utils/userfile/johnmanenti@gmail.com/user_pic.jpg";

        if (profileImg.isEmpty()) {
            profile.setImageResource(R.drawable.user);
        } else{
            Picasso.get().load(profileImg).resize(300,300).centerInside().into(profile);

        }


        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i4);
                //finish();
            }
        });









        final Button nearbybtnHome = findViewById(R.id.nearbyBtnHome);
        nearbybtnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                startActivity(i1);
            //    finish();
//                Log.d(TAG, mEmail);
                    //The key argument here must match that used in the other activity

            }
        });

        final Button placesbtnHome = findViewById(R.id.placesBtnHome);
        placesbtnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                startActivity(i2);
             //   finish();
            }
        });



        final Button peoplebtnHome = findViewById(R.id.peopleBtnHome);
        peoplebtnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                startActivity(i3);
              //  finish();
            }
        });


        tempGridSetup();

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {





            }
        }, 2000);





    }

    private void tempGridSetup() {

        getFeedFromDB();

        setupImageGrid(feedImg);




    }

    public void onClick(View v) {
        showPopup();
    }

    private void setupImageGrid(ArrayList<String> imgURLs) {
        GridView gridView = (GridView) findViewById(R.id.gridView);
       // TextView textView = (TextView) findViewById(R.id.titleFeed);

        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_image_grid_view, "", imgURLs);

        gridView.setAdapter(adapter);

    }

    private void setupActivityWidgets(){
        mProgressBar = (ProgressBar) findViewById(R.id.gridImageProgressBar);
//        mProgressBar.setVisibility(View.GONE);
        profilePhoto = (ImageView) findViewById(R.id.gridImageView);
    }



    private void getFeedFromDB() {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://artificialx.com.br/projetos/viaggio/utils/viaggioFeed.php")
                .build();
        try {
            Response response = client.newCall(request).execute();

            JSONArray array = new JSONArray(response.body().string());

            feedLength = array.length();

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                feedImg.add(object.getString("image"));
                feedInfo.add(object.getString("info"));
                feedScore.add(object.getInt("score"));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


    private PopupWindow pw;
    private GoogleMap mMap;

    private void showPopup() {

        try {

            MapViewActivity mapViewActivity = new MapViewActivity();

//            mapViewActivity.onMapReady(mMap);
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = this.getLayoutInflater();
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.mpopup,
                    (ViewGroup) findViewById(R.id.home));
            pw = new PopupWindow(layout, 770, 870, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            Button Close = (Button) layout.findViewById(R.id.popCLose);
            Close.setOnClickListener(cancel_button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancel_button = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };




    private void getUserFromDB(String id) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://artificialx.com.br/projetos/viaggio/utils/viaggioUsers.php?email="+id)
                .build();
        try {
            Response response = client.newCall(request).execute();

            JSONArray array = new JSONArray(response.body().string());

            //userInfo = array.length();

            //  for (int i = 0; i < array.length(); i++) {



            JSONObject object = array.getJSONObject(0);
            mEmail = object.getString("email");
            profileImg = object.getString("profile_photo");

          // userPic = drawable_from_url(profileImg);



            //}


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //return profileImg;

    }






}
/**
 * Responsible for setting up the profile toolbar
 */


/**
 * BottomNavigationView setup
 */