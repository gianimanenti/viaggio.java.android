
package br.com.artificialx.dallair;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import br.com.artificialx.dallair.Utils.GridImageAdapter;
import br.com.artificialx.dallair.Utils.MapViewActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static br.com.artificialx.dallair.Nearby.MY_PERMISSIONS_REQUEST_LOCATION;

//import br.com.artificialx.dallair.Utils.BottomNavigationViewHelper;

/**
 * Created by User on 5/28/2017.
 */

public class Places extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Places";
    private static final int ACTIVITY_NUM = 4;

    private Context mContext = Places.this;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;
    private FusedLocationProviderClient mFusedLocationClient;



    final ArrayList<Double> tplacesLat = new ArrayList<>();
    final ArrayList<Double> tplacesLong = new ArrayList<>();
    final ArrayList<String> tplacesInfo = new ArrayList<>();

    final ArrayList<Double> rplacesLat = new ArrayList<>();
    final ArrayList<Double> rplacesLong = new ArrayList<>();
    final ArrayList<String> rplacesInfo = new ArrayList<>();

    Integer placesLength = 0;
    Integer rPlacesLength = 0;

    LatLng tPlace;
    LatLng rPlace;
    LatLng userLocation;
    Location local;

    String profileImg;
    String sessionId;

    Integer i = 0;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

                    mMap.addMarker(new MarkerOptions().position(userLocation).title(""));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));


                }
            }, 2000);


        };




        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final RelativeLayout content = (RelativeLayout) findViewById(R.id.content);

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

        NavigationView navigationView = findViewById(R.id.nav_view);




              //userLat = userLocation.latitude;
             // userLong = userLocation.longitude;



        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped



                        int id = menuItem.getItemId();
                        switch (id) {

                            case R.id.restaurant:
                                mMap.clear();

                                getRplacesFromDB();

                                while (i < rPlacesLength) {

                                    rPlace = new LatLng(rplacesLat.get(i), rplacesLong.get(i));

                                    mMap.addMarker(new MarkerOptions().position(rPlace).title(""));

                                    i++;

                                }

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));


                                drawerLayout.closeDrawers();
                                break;

                            case R.id.tourist_attractions:
                                mMap.clear();

                                geTplacesFromDB();


                                while (i < placesLength) {

                                    tPlace = new LatLng(tplacesLat.get(i), tplacesLong.get(i));

                                    mMap.addMarker(new MarkerOptions().position(tPlace).title(""));

                                    i++;

                                }

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                                drawerLayout.closeDrawers();

                                break;
                        }
                        i = 0;
                        return true;
                    }
                });


        final CircleImageView profile = findViewById(R.id.placesProfile_image);

        Bundle extras = getIntent().getExtras();

        final Intent i1 = new Intent(Places.this, Home.class);

        final Intent i2 = new Intent(Places.this, Nearby.class);

        final Intent i3 = new Intent(Places.this, mainMenu.class);

        final Intent i4 = new Intent(Places.this, User.class);

        if (extras != null) {
           // sessionId = extras.getString("key");

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

        final Button homebtnPlaces = findViewById(R.id.newBtnPlaces);
        homebtnPlaces.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i1);

                finish();
            }
        });



        final Button nearbybtnPlaces = findViewById(R.id.nearbyBtnPlaces);
        nearbybtnPlaces.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                startActivity(i2);

                finish();
            }
        });



        final Button peoplebtnPlaces = findViewById(R.id.peopleBtnPlaces);
        peoplebtnPlaces.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i3);

                finish();
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i4);
                //finish();
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.g_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(11);
        mMap.setMaxZoomPreference(110);

       LatLng userL = new LatLng(40.833333, 14.266667);
       mMap.moveCamera(CameraUpdateFactory.newLatLng(userL));


    }



    private void setupActivityWidgets(){
        mProgressBar = (ProgressBar) findViewById(R.id.gridImageProgressBar);
        mProgressBar.setVisibility(View.GONE);
        profilePhoto = (ImageView) findViewById(R.id.gridImageView);
    }


    private void geTplacesFromDB() {


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

                tplacesLat.add(object.getDouble("latitude"));
                tplacesLong.add(object.getDouble("longitude"));
                tplacesInfo.add(object.getString("info"));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


    private void getRplacesFromDB() {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://artificialx.com.br/projetos/viaggio/utils/viaggiotRestaurant.php")
                .build();
        try {
            Response response = client.newCall(request).execute();

            JSONArray array = new JSONArray(response.body().string());

            rPlacesLength = array.length();

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                rplacesLat.add(object.getDouble("latitude"));
                rplacesLong.add(object.getDouble("longitude"));
                rplacesInfo.add(object.getString("info"));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

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
                                ActivityCompat.requestPermissions(Places.this,
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

                                mMap.addMarker(new MarkerOptions().position(userLocation).title(""));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));


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


    private void getUserFromDB(String id) {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://artificialx.com.br/projetos/viaggio/utils/viaggioUsers.php?email="+id)
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
