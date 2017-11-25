package com.development.black_preacher.uploadimage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String server;

    EditText ed_Server;
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ed_Server = findViewById(R.id.ed_server);
        btn_save = findViewById(R.id.btn_save_server);

        sharedPreferences = this.getSharedPreferences("com.development.black_preacher.uploadimage", Context.MODE_PRIVATE);
        server = sharedPreferences.getString("link","");

        ed_Server.setText(server);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverLink = ed_Server.getText().toString();
                sharedPreferences.edit().putString("link",serverLink).apply();
                server = serverLink;
            }
        });

    }
}
