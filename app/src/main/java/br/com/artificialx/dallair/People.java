
package br.com.artificialx.dallair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import br.com.artificialx.dallair.Utils.GridImageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import br.com.artificialx.dallair.Utils.BottomNavigationViewHelper;

/**
 * Created by User on 5/28/2017.
 */

public class People extends AppCompatActivity {
    private static final String TAG = "People";
    private static final int ACTIVITY_NUM = 4;

    private Context mContext = People.this;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;

    String profileImg;
    String sessionId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people);
       // Log.d(TAG, "onCreate: started.");

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final CircleImageView profile = findViewById(R.id.peopleProfile_image);

        Bundle extras = getIntent().getExtras();

        final Intent i1 = new Intent(People.this, Nearby.class);

        final Intent i2 = new Intent(People.this, Places.class);

        final Intent i3 = new Intent(People.this, Home.class);

        final Intent i4 = new Intent(People.this, User.class);




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

        if (profileImg == null) {
            profile.setImageResource(R.drawable.user);
        } else{
            Picasso.get().load(profileImg).resize(300,300).centerInside().into(profile);

        }


        final Button  nearbybtnPeople = findViewById(R.id.nearbyBtnPeople);
        nearbybtnPeople.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i1);

                finish();
                                      }
            });

        final Button placesbtnPeople = findViewById(R.id.placesBtnPeople);
        placesbtnPeople.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i2);

                finish();
            }
        });



        final Button homebtnPeople = findViewById(R.id.newBtnPeople);
        homebtnPeople.setOnClickListener(new View.OnClickListener() {
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





        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {


                tempGridSetup();
            }
        }, 2000);





    }

    private void tempGridSetup() {
        ArrayList<String> imgURLs = new ArrayList<>();
        imgURLs.add("http://artificialx.com.br/projetos/dallair/bnb/images/giulia.png");
        imgURLs.add("http://artificialx.com.br/projetos/dallair/bnb/images/giovanni.png");
        imgURLs.add("http://artificialx.com.br/projetos/dallair/bnb/images/antonia.png");
        imgURLs.add("http://artificialx.com.br/projetos/dallair/bnb/images/francesco.png");
        imgURLs.add("http://artificialx.com.br/projetos/dallair/bnb/images/sofia.png");
        imgURLs.add("http://artificialx.com.br/projetos/dallair/bnb/images/marco.png");
        imgURLs.add("http://artificialx.com.br/projetos/dallair/bnb/images/francesca.png");
        imgURLs.add("http://artificialx.com.br/projetos/dallair/bnb/images/alice.png");


        setupImageGrid(imgURLs);

    }

    private void setupImageGrid(ArrayList<String> imgURLs) {

        final Intent i5 = new Intent(this, videoCall.class);

        GridView gridView = (GridView) findViewById(R.id.gridView);

        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.people_image_grid_view, "", imgURLs);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                startActivity(i5);
            }
        });

    }

    private void setupActivityWidgets(){
        mProgressBar = (ProgressBar) findViewById(R.id.gridImageProgressBar);
        mProgressBar.setVisibility(View.GONE);
        profilePhoto = (ImageView) findViewById(R.id.gridImageView);
    }


    private void getUserFromDB(String id) {

        id = "johnmanenti@gmail.com";

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
