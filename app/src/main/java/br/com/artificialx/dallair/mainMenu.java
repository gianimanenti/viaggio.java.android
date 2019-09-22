
package br.com.artificialx.dallair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
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

public class mainMenu extends AppCompatActivity {

    BubblePicker bubblePicker;

    String[] name = {
            "Party",
            "Restaurant",
            "Pizza",
            "Art Gallery",
            "Museums",
            "Art on Street",
            "Music",
    };

    int [] images = {
            R.drawable.party,
            R.drawable.restaurant,
            R.drawable.pizza,
            R.drawable.art_gallery,
            R.drawable.museum,
            R.drawable.art_street,
            R.drawable.music,
    };

    int [] colors = {

            Color.parseColor("#7B1FA2"),
            Color.parseColor("#8E24AA"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#AB47BC"),
            Color.parseColor("#BA68C8"),
            Color.parseColor("#CE93D8"),
            Color.parseColor("#6A1B9A"),

    };

    @Override
    protected void onResume() {
        super.onResume();
        bubblePicker.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bubblePicker.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.peoplebubble);

        bubblePicker = (BubblePicker)findViewById(R.id.picker);

        final String[] titles = getResources().getStringArray(R.array.events);
        final TypedArray colors = getResources().obtainTypedArray(R.array.colors);
        final TypedArray images = getResources().obtainTypedArray(R.array.images);

        bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return titles.length;
            }

            @NotNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(titles[position]);


                //item.setColor(colors.getColor((position * 2) % 8 + 1, 0));
                item.setGradient(new BubbleGradient(colors.getColor((position * 2) % 8, 0),
                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));
                // item.setTypeface(mediumTypeface);
                item.setTextColor(ContextCompat.getColor(mainMenu.this, android.R.color.white));
                item.setBackgroundImage(ContextCompat.getDrawable(mainMenu.this, images.getResourceId(position, 0)));
                return item;
            }
        });

        bubblePicker.setBubbleSize(30);

        // bubblePicker.setItems(listItems);

        bubblePicker.setMaxSelectedCount(3);

        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem item) {
               //  Log.d(bubblePicker.getMaxSelectedCount().toString(), "onCreate: started.");

                if (bubblePicker.getSelectedItems().size() > 2) {

                        final Intent i4 = new Intent(mainMenu.this, People.class);
                        startActivity(i4);
                    }
                }




            @Override
            public void onBubbleDeselected(@NotNull PickerItem item) {



            }
        });








        final Button newBtnBubble = findViewById(R.id.newBtnBubble);
        newBtnBubble.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Intent i1 = new Intent(mainMenu.this, Home.class);
                startActivity(i1);
                 finish();
            }
        });

        final Button placesBtnBubble = findViewById(R.id.placesBtnBubble);
        placesBtnBubble.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Intent i2 = new Intent(mainMenu.this, Places.class);
                startActivity(i2);
                  finish();
            }
        });


        final Button nearbyBtnBubble = findViewById(R.id.nearbyBtnBubble);
        placesBtnBubble.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Intent i3 = new Intent(mainMenu.this, Nearby.class);
                startActivity(i3);
                finish();
            }
        });


    }




}
