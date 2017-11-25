package com.development.black_preacher.uploadimage;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    Button btn_choose;
    Button btn_send;
    ImageView imageView;
    Button btn_settings;
    SharedPreferences sharedPreferences;

    String server;

    Uri uri;

    Context context = this;

    final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/";

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_choose = findViewById(R.id.btn_choose);
        btn_send = findViewById(R.id.btn_send);
        imageView = findViewById(R.id.imageView);

        btn_settings = findViewById(R.id.btn_settings);

        sharedPreferences = this.getSharedPreferences("com.development.black_preacher.uploadimage", Context.MODE_PRIVATE);
        server = sharedPreferences.getString("link","");
        btn_send.setEnabled(false);

        int permission_write = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission_camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permission_internet = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if (permission_write != PackageManager.PERMISSION_GRANTED ||
                permission_camera != PackageManager.PERMISSION_GRANTED ||
                permission_internet != PackageManager.PERMISSION_GRANTED ||
                permission_read != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    200
            );
        }

        // Wähle Bild aus Galerie
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage();
            }
        });

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,SettingsActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        System.out.println(resultCode);
        System.out.println(data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            btn_send.setEnabled(true);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            //uri = data.getData();
            System.out.println("CAMERA");
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    public void sendImage() {

        AyncUploadTaskClass uploadObjekt = new AyncUploadTaskClass();
        uploadObjekt.setServer(server);
        uploadObjekt.setUri(uri);
        uploadObjekt.setContext(this);

        uploadObjekt.execute();

        imageView.setImageResource(android.R.color.transparent);
        btn_send.setEnabled(false);


    }
}
