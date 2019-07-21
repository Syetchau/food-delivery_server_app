package com.example.liew.ideliveryserver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSignInAsAdmin, btnSignInAsStaff;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());


        setContentView(R.layout.activity_main);

        btnSignInAsAdmin = (Button)findViewById(R.id.btnSignInAsAdmin);
        btnSignInAsStaff = (Button)findViewById(R.id.btnSignInAsStaff);

        btnSignInAsStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInAsStaff = new Intent(MainActivity.this, SignInAsStaff.class);
                startActivity(signInAsStaff);
            }
        });

        btnSignInAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInAsAdmin = new Intent(MainActivity.this, SignInAsAdmin.class);
                startActivity(signInAsAdmin);
            }
        });
    }

}
