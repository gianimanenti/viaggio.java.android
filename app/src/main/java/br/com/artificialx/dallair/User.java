
package br.com.artificialx.dallair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class User extends AppCompatActivity {

    Button UploadImageOnServerButton;

    //ImageView ShowSelectedImage;

    //EditText imageName;

    Bitmap FixBitmap;

    CircleImageView profile;

    String ImageTag = "image_tag" ;

    String ImageName = "image_data" ;

    ProgressDialog progressDialog ;

    ByteArrayOutputStream byteArrayOutputStream ;

    byte[] byteArray ;

    String ConvertImage ;

    String GetImageNameFromEditText;

    HttpURLConnection httpURLConnection ;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter ;

    int RC ;

    BufferedReader bufferedReader ;

    StringBuilder stringBuilder;

    boolean check = true;

    private int GALLERY = 1, CAMERA = 2;

    String profileImg = "";
    String sessionId;
    String mEmail = "";

    String mṔassword;
    String mName;
    String mLName;
    String mAge;
    String mLanguage1;
    String mLanguage2;

    EditText Email;
    EditText Password;
    EditText Name;
    EditText LName;
    EditText Age;
    Spinner Language1;
    Spinner Language2;

    String urlServer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Bundle extras = getIntent().getExtras();

        final Intent i1 = new Intent(User.this, Home.class);

        Email =(EditText)findViewById(R.id.userEmail);
        Name=(EditText)findViewById(R.id.userName);
        LName=(EditText)findViewById(R.id.userLName);
        Age=(EditText)findViewById(R.id.userAge);
        Password=(EditText)findViewById(R.id.userPassword);


        Language1 = (Spinner) findViewById(R.id.UserlanguageSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.userLanguageSpinner, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        Language1.setAdapter(adapter);

        Language2 = (Spinner) findViewById(R.id.UsersecondLanguageSpinner);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.userSecondLanguageSpinner, android.R.layout.simple_spinner_item);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        Language2.setAdapter(adapter2);





        if (extras != null) {

            sessionId = extras.getString("key");
            getUserFromDB(sessionId);

            i1.putExtra("key", sessionId);

            if (!mEmail.isEmpty()) {
                Email.setText(mEmail);
            }

            if (!mName.isEmpty()) {
                Name.setText(mName);
            }

            if (!mLName.isEmpty()) {
                LName.setText(mLName);
            }

            if (!mAge.isEmpty()) {
                Age.setText(mAge);
            }

            if (!mLanguage1.isEmpty()) {
                Language1.setSelection(adapter.getPosition(mLanguage1));
            }

            if (!mLanguage2.isEmpty()) {
                Language2.setSelection(adapter.getPosition(mLanguage2));
            }

        }




       profile = findViewById(R.id.userProfile_image);

        UploadImageOnServerButton = (Button)findViewById(R.id.userSave);

        Button home = (Button)findViewById(R.id.userHome);

        //ShowSelectedImage = (ImageView)findViewById(R.id.imageView);



        byteArrayOutputStream = new ByteArrayOutputStream();

        if (profileImg.isEmpty()) {
            profile.setImageResource(R.drawable.user);
        } else{
            Picasso.get().load(profileImg).resize(300,300).centerInside().into(profile);

        }

        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(i1);
                finish();
            }
        });



        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();


            }
        });


        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = "user_pic";

                UploadImageToServer();

            }
        });

        if (ContextCompat.checkSelfPermission(User.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    profile.setImageBitmap(FixBitmap);
                    UploadImageOnServerButton.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(User.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            profile.setImageBitmap(FixBitmap);
            UploadImageOnServerButton.setVisibility(View.VISIBLE);
            //  saveImage(thumbnail);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    public void UploadImageToServer(){

        if (FixBitmap != null) {

            FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        }

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        mName = Name.getText().toString();
        mLName = LName.getText().toString();
        mAge = Age.getText().toString();
        mLanguage1 = Language1.getSelectedItem().toString();
        mLanguage2 = Language2.getSelectedItem().toString();
        mṔassword = Password.getText().toString();

        if(!mṔassword.isEmpty()) {

            urlServer = "https://artificialx.com.br/projetos/viaggio/utils/uploadImage.php?email="+mEmail+"&nome="+mName+"&senha="+mṔassword+"&sobrenome="+mLName+"&idade="+mAge+"&idioma_1="+mLanguage1+"&idioma_2="+mLanguage2;

        } else {

            urlServer = "https://artificialx.com.br/projetos/viaggio/utils/uploadImage.php?email="+mEmail+"&nome="+mName+"&sobrenome="+mLName+"&idade="+mAge+"&idioma_1="+mLanguage1+"&idioma_2="+mLanguage2;
        }

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(User.this,"Updating your info","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                Toast.makeText(User.this,string1,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageTag, GetImageNameFromEditText);

                HashMapParams.put(ImageName, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(urlServer, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                Toast.makeText(User.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();

            }
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
            mEmail = object.getString("email");
            profileImg = object.getString("profile_photo");

            mName = object.getString("nome");
            mLName = object.getString("sobrenome");
            mAge = object.getString("idade");
            mLanguage1 = object.getString("idioma_1");
            mLanguage2 = object.getString("idioma_2");



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