
package br.com.artificialx.dallair;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

import br.com.artificialx.dallair.Utils.GridImageAdapter;

//import br.com.artificialx.dallair.Utils.BottomNavigationViewHelper;

/**
 * Created by User on 5/28/2017.
 */

public class Nearby extends AppCompatActivity {
    private static final String TAG = "Nearby";
    private static final int ACTIVITY_NUM = 4;

    private Context mContext = Nearby.this;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;
    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;




    final ArrayList<Double> placesLat = new ArrayList<>();
    final ArrayList<Double> placesLong = new ArrayList<>();
    final ArrayList<String> placesInfo = new ArrayList<>();

    final ArrayList<Double> restLat = new ArrayList<>();
    final ArrayList<Double> restLong = new ArrayList<>();
    final ArrayList<String> restInfo = new ArrayList<>();

    ArrayList<Double> dist = new ArrayList<>();
    ArrayList<Double> dista = new ArrayList<>();

  ArrayList<String> distInfo = new ArrayList<>();
  ArrayList<String> distInfoa = new ArrayList<>();


    Integer placesLength = 0;
    Integer restLength = 0;
    Integer i = 0;

    LatLng userLocation;
    Location local;


  //  Location local;

    String profileImg;
    String sessionId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby);

        final CircleImageView profile = findViewById(R.id.nearbyProfile_image);

        Bundle extras = getIntent().getExtras();

        final Intent i1 = new Intent(Nearby.this, Home.class);

        final Intent i2 = new Intent(Nearby.this, Places.class);

        final Intent i3 = new Intent(Nearby.this, mainMenu.class);

        final Intent i4 = new Intent(Nearby.this, User.class);


        if (extras != null) {
            //sessionId = extras.getString("key");

            sessionId = "johnmanenti@gmail.com";

            getUserFromDB(sessionId);

            i1.putExtra("key", sessionId);

            i2.putExtra("key", sessionId);

            i3.putExtra("key", sessionId);

            i4.putExtra("key", sessionId);

            //The key argument here must match that used in the other activity
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



        final WebView myWebView = (WebView) findViewById(R.id.nearbyWebview);

        myWebView.getSettings().setDomStorageEnabled(true);

        myWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        //webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        myWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
               // System.out.println("hello");
                return true;
            }
        });

       // Log.d(TAG, "onCreate: started.");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        if (checkLocationPermission()) {


            final Task<Location> info = mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {


                            if (location != null) {

                                // myWebView.loadUrl("https://artificialx.com.br");
                                // Logic to handle location object
                            }
                        }
                    });

            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {

                    local = info.getResult();

                    userLocation = new LatLng(local.getLatitude(), local.getLongitude());

//                    mMap.addMarker(new MarkerOptions().position(userLocation).title(""));
                  //  mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));


                }
            }, 2000);

        }


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.nearby_drawer_layout);
        final RelativeLayout content = (RelativeLayout) findViewById(R.id.nearby_content);

        drawerLayout.setScrimColor(Color.TRANSPARENT);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.cancel) {
            private float scaleFactor = 6f;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
                content.setScaleX(1 - (slideOffset / scaleFactor));
                content.setScaleY(1 - (slideOffset / scaleFactor));
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        NavigationView navigationView = findViewById(R.id.nearby_nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped

                        int id = menuItem.getItemId();

                        switch (id) {

                            case R.id.nearbyRestaurant:

                                getrestaurantFromDB();

                                while (i < restLength) {

                                    Location temp = new Location(LocationManager.GPS_PROVIDER);

                                    temp.setLatitude(restLat.get(i));
                                    temp.setLongitude(restLong.get(i));
                                    // Got last known location. In some rare situations this can be null.
                                    double distanceInKiloMeters = (local.distanceTo(temp)); // as distance is in meter


                                    if (distanceInKiloMeters <= 1000.00) {

                                        dista.add(distanceInKiloMeters);

                                        distInfoa.add(restInfo.get(i));

                                    }

                                    i++;
                                }

                                i = 0;


                                if (dista.size() > 0) {

                                    Integer infoIndex = dista.indexOf(Collections.min(dista));

                                    myWebView.setWebViewClient(new WebViewClient()
                                    {
                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url)
                                        {
                                            view.loadUrl(url);
                                            //System.out.println("hello");
                                            return true;
                                        }
                                    });

                                    myWebView.loadUrl(distInfoa.get(infoIndex));
                                    drawerLayout.closeDrawers();
                                    // break;

                                } else {

// not in range of 1 km

                                    myWebView.setWebViewClient(new WebViewClient()
                                    {
                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url)
                                        {
                                            view.loadUrl(url);
                                            //System.out.println("hello");
                                            return true;
                                        }
                                    });

                                    myWebView.loadUrl("http://artificialx.com.br/projetos/viaggio/napoli/404.html");


                                    drawerLayout.closeDrawers();
                                    //float distance = location.distanceTo(temp);


                                }

                                break;

                            case R.id.nearbyTourist_attractions:

                                getplacesFromDB();

                                //float distance = location.distanceTo(temp);
                                while (i < placesLength) {

                                    Location temp = new Location(LocationManager.GPS_PROVIDER);

                                    temp.setLatitude(placesLat.get(i));
                                    temp.setLongitude(placesLong.get(i));
                                    // Got last known location. In some rare situations this can be null.
                                    double distanceInKiloMeters = (local.distanceTo(temp)); // as distance is in meter


                                    if (distanceInKiloMeters <= 1000.00) {

                                        dist.add(distanceInKiloMeters);

                                        distInfo.add(placesInfo.get(i));

                                    }

                                    i++;
                                }

                                i = 0;


                                if (dist.size() > 0) {

                                    Integer infoIndex = dist.indexOf(Collections.min(dist));

                                    myWebView.setWebViewClient(new WebViewClient() {
                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                            view.loadUrl(url);
                                            //System.out.println("hello");
                                            return true;
                                        }
                                    });

                                    myWebView.loadUrl(distInfo.get(infoIndex));
                                    drawerLayout.closeDrawers();
                                    // break;

                                } else {

// not in range of 1 km

                                    myWebView.setWebViewClient(new WebViewClient() {
                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                            view.loadUrl(url);
                                            //System.out.println("hello");
                                            return true;
                                        }
                                    });

                                    myWebView.loadUrl("http://artificialx.com.br/projetos/viaggio/napoli/404.html");

                                    drawerLayout.closeDrawers();
                                    //float distance = location.distanceTo(temp);


                                }

                                break;
                        }
                        i = 0;
                        return true;
                    }
                });




        final Button homebtnNearby = findViewById(R.id.newBtnNearby);
        homebtnNearby.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i1);

                finish();
            }
        });

        final Button placesbtnNearby = findViewById(R.id.placesBtnNearby);
        placesbtnNearby.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i2);


                finish();
            }
        });


        final Button peoplebtnNearby = findViewById(R.id.peopleBtnNearby);
        peoplebtnNearby.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i3);



                finish();
            }
        });





        if (checkLocationPermission()) {


            final Task<Location> info = mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {


                            if (location != null) {

                                // myWebView.loadUrl("https://artificialx.com.br");
                                // Logic to handle location object
                            }
                        }
                    });

            Handler handle = new Handler();
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {


                    // local = info.getResult();



                    getplacesFromDB();


                    //float distance = location.distanceTo(temp);
                    while (i < placesLength) {

                        Location temp = new Location(LocationManager.GPS_PROVIDER);

                        temp.setLatitude(placesLat.get(i));
                        temp.setLongitude(placesLong.get(i));
                        // Got last known location. In some rare situations this can be null.
                        double distanceInKiloMeters = (local.distanceTo(temp)); // as distance is in meter


                        if (distanceInKiloMeters <= 1000.00) {

                            dist.add(distanceInKiloMeters);

                            distInfo.add(placesInfo.get(i));

                        }

                        i++;
                    }

                    i = 0;



                    if (dist.size() > 0) {

                        Integer infoIndex = dist.indexOf(Collections.min(dist));


                        myWebView.setWebViewClient(new WebViewClient()
                        {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url)
                            {
                                view.loadUrl(url);
                                //System.out.println("hello");
                                return true;
                            }
                        });

                        myWebView.loadUrl(distInfo.get(infoIndex));

                        // break;

                    } else {

// not in range of 1 km

                        myWebView.setWebViewClient(new WebViewClient()
                        {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url)
                            {
                                view.loadUrl(url);
                                //System.out.println("hello");
                                return true;
                            }
                        });

                        myWebView.loadUrl("http://artificialx.com.br/projetos/viaggio/napoli/404.html");



                        //float distance = location.distanceTo(temp);


                    }
                }
            }, 2000);




        };


    }


    private void setupActivityWidgets(){
        mProgressBar = (ProgressBar) findViewById(R.id.gridImageProgressBar);
        mProgressBar.setVisibility(View.GONE);
        profilePhoto = (ImageView) findViewById(R.id.gridImageView);
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Deixa localização?")
                        .setMessage("Deixa localização?")
                        .setPositiveButton("OK" , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Nearby.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        final Task<Location> info = mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {


                                        if (location != null) {

                                            // myWebView.loadUrl("https://artificialx.com.br");
                                            // Logic to handle location object
                                        }
                                    }
                                });


                        Handler handle = new Handler();
                        handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                local = info.getResult();

                                userLocation = new LatLng(local.getLatitude(), local.getLongitude());

                               // mMap.addMarker(new MarkerOptions().position(userLocation).title(""));
                               // mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));


                            }
                        }, 2000);

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


    private void getplacesFromDB() {


                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://artificialx.com.br/projetos/viaggio/utils/viaggiotPlaces.php")
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    placesLength = array.length();

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        placesLat.add(object.getDouble("latitude"));
                        placesLong.add(object.getDouble("longitude"));
                        placesInfo.add(object.getString("info"));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



    }


    private void getrestaurantFromDB() {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://artificialx.com.br/projetos/viaggio/utils/viaggiotRestaurant.php")
                .build();
        try {
            Response response = client.newCall(request).execute();

            JSONArray array = new JSONArray(response.body().string());

            restLength = array.length();

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                restLat.add(object.getDouble("latitude"));
                restLong.add(object.getDouble("longitude"));
                restInfo.add(object.getString("info"));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


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

            profileImg = object.getString("profile_photo");
            //}


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }










    }






/**
 * Responsible for setting up the profile toolbar
 */


    /**
     * BottomNavigationView setup
     */
